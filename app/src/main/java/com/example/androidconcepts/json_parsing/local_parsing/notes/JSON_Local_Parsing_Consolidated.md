# JSON Local Parsing — Consolidated Notes

Retrofit se pehle JSON parsing ke fundamentals — Object, Array, File. Teeno mein core engine same hai: **Gson**. Har section: Kya Hai → Code (actual project code) → Interview Q&A → Gotchas.

---

## 1. JSON Object Parsing

### Kya Hai
Hardcoded JSON string (`"""..."""` raw string) ko `Gson().fromJson()` se ek Kotlin `data class` mein convert karna. Isi demo mein **saare JSON data types** cover kiye — String, Int, Double, Boolean, aur null.

### Code
```kotlin
// Post.kt — saare JSON data types cover karta model
data class Post(
    val id: Int,             // Number (integer)
    val title: String,       // String
    val body: String,        // String
    val rating: Double,      // Number (decimal)
    val isPublished: Boolean,// Boolean
    val publishedAt: String? // null → nullable type
)

// JsonObjectParsingActivity.kt
private val jsonString = """
    {
        "id": 1,
        "title": "First Post",
        "body": "This is my first post. Hope You liked it",
        "rating": 4.5,
        "isPublished": true,
        "publishedAt": null
    }
""".trimIndent()

private fun bindUi() {
    binding.tvRawJson.text = jsonString
    val post = Gson().fromJson(jsonString, Post::class.java)
    binding.tvParsedId.text = post.id.toString()
    binding.tvParsedTitle.text = post.title
    binding.tvParsedBody.text = post.body
    binding.tvParsedRating.text = post.rating.toString()
    binding.tvParsedIsPublished.text = post.isPublished.toString()
    binding.tvParsedPublishedAt.text = post.publishedAt ?: "null"  // null-safe display
}
```

### JSON → Kotlin Type Mapping
| JSON Type | Kotlin Type |
|---|---|
| `"text"` | `String` |
| `123` | `Int` |
| `12.5` | `Double` |
| `true` / `false` | `Boolean` |
| `null` | Nullable type (`String?`) |
| `{ }` | `data class` |
| `[ ]` | `List<T>` |

### Common Interview Questions
**Q: Gson field matching kaise karta hai?**
> Reflection use karke JSON key ka naam Kotlin property ke naam se match karta hai. Agar naam alag ho (jaise JSON mein `user_name`, Kotlin mein `userName`), toh `@SerializedName("user_name")` annotation lagani padti hai.

**Q: `"""..."""` (triple-quote) kyun use karte hain, normal `"..."` kyun nahi?**
> JSON mein bahut saare double-quotes (`"`) hote hain. Normal string mein har ek ko `\"` escape karna padta — raw string (triple-quote) mein escaping ki zaroorat nahi, JSON as-is likh sakte hain.

### Gotchas
```kotlin
// GALAT — braces { } missing, comma missing — invalid JSON, Gson crash karega (JsonSyntaxException)
val jsonString = """
    "id" : 1,
    "title" : "First Post"
    "body" : "..."
""".trimIndent()

// SAHI — { } se wrapped, har key-value ke baad comma (last ko chhodkar)
val jsonString = """
    {
        "id": 1,
        "title": "First Post",
        "body": "..."
    }
""".trimIndent()
```
> **Real mistake jo hui thi:** Yeh exact bug is project mein aaya tha — bina `{ }` ke JSON invalid ho jaata hai aur `Gson().fromJson()` crash kar deta. JSON object hamesha `{ }` se start-end hona chahiye.

---

## 2. JSON Array Parsing

### Kya Hai
JSON array (`[ {...}, {...} ]`) ko `List<Post>` mein parse karna. Object Parsing se sirf itna fark — target class `Post::class.java` ki jagah `Array<Post>::class.java` hota hai.

### Code
```kotlin
private val jsonString = """
    [
        { "id": 1, "title": "First Post", "body": "..." },
        { "id": 2, "title": "Second Post", "body": "..." },
        { "id": 3, "title": "Third Post", "body": "..." }
    ]
""".trimIndent()

private fun bindUi() {
    val posts = Gson().fromJson(jsonString, Array<Post>::class.java).toList()
    binding.tvRawJson.text = jsonString

    posts.forEach { post ->
        val row = layoutInflater.inflate(R.layout.item_post_row, binding.containerResults, false)
        row.findViewById<TextView>(R.id.tvItemTitle).text = post.title
        row.findViewById<TextView>(R.id.tvItemId).text = post.id.toString()
        binding.containerResults.addView(row)
    }
}
```

### Deep Dive — Generic Type Erasure (Detailed)

**Generics sirf compile-time cheez hai.** Jab code likhte waqt aap `List<Post>` aur `List<String>` likhte ho, compiler type-safety deta hai — lekin `.class` file banne ke baad, JVM `<Post>`/`<String>` wala part **hata (erase) deta hai**. Runtime pe dono sirf `List` reh jaate hain:

```kotlin
val posts: List<Post> = listOf(Post(1, "A"))
val names: List<String> = listOf("hello")

println(posts.javaClass == names.javaClass)  // TRUE! Runtime pe dono sirf "List" hain
```

Gson ko JSON parse karne ke liye ek **Class object** chahiye (`Post::class.java` jaisa). `List<Post>::class.java` likhna Kotlin mein possible hi nahi — kyunki JVM ke paas `<Post>` ka info bacha hi nahi (erase ho chuka).

**Array iska exception hai** — Array generics nahi, ek special JVM feature hai jo apna component type runtime pe **yaad rakhta hai**:

```kotlin
val posts: Array<Post> = arrayOf(Post(1, "A"))
val names: Array<String> = arrayOf("hello")

println(posts.javaClass == names.javaClass)  // FALSE! Alag classes — Post[] aur String[]
```

Isliye `Array<Post>::class.java` valid hai — JVM ko pata hai "yeh `Post[]` hai", erase nahi hota. Gson isse pehchaan ke sahi type mein parse kar leta hai, phir `.toList()` se `List<Post>` bana lete hain.

### Common Interview Questions
**Q: `List<Post>::class.java` direct kyun nahi likh sakte, `Array<Post>::class.java` kyun?**
> Kotlin/Java **generic type erasure** ki wajah se — runtime pe `List<Post>` ka exact generic type pata nahi chalta (JVM sirf `List` dekhta hai, `Post` "erase" ho jaata hai). Arrays apna component type runtime pe retain karte hain, isliye `Array<Post>::class.java` se Gson ko pata chal jaata hai kis type mein parse karna hai. Baad mein `.toList()` se List bana lete hain.

**Q: Retrofit mein yeh problem kyun nahi aati, wahan seedha `List<Post>` return type likh dete hain?**
> Retrofit reflection se **function ka return type** directly read karta hai (jo compile-time pe available hota hai — method signature mein generic info reflection ke through mil jaata hai), isliye `suspend fun getPosts(): List<Post>` likhne se kaam ho jaata hai — manual `Array<T>::class.java` trick nahi chahiye.

### Gotchas
```kotlin
// GALAT — layoutInflater.inflate() ka 2-param version, root non-null hone ki wajah se
// yeh AUTOMATICALLY view ko attach kar deta hai aur return value naya row nahi, root khud hota hai!
val row = layoutInflater.inflate(R.layout.item_post_row, binding.containerResults)
binding.containerResults.addView(row)  // CRASH: "child already has a parent"
//                                          (kyunki row == containerResults, khud ko khud mein add karne ki koshish)

// SAHI — 3-param version, attachToRoot = false
// view banta hai lekin attach nahi hota, return value actual naya inflated row hota hai
val row = layoutInflater.inflate(R.layout.item_post_row, binding.containerResults, false)
binding.containerResults.addView(row)  // ab sahi se add hoga
```
> **Real mistake jo hui thi:** `inflate(resource, root)` (2-param) — jab `root` non-null ho, `attachToRoot` internally `true` ban jaata hai, aur return value **root khud** hota hai (naya inflated view nahi). Isse `addView()` dobara call karne pe crash hota hai. **Rule: RecyclerView/manual inflate mein hamesha `false` explicitly pass karo jab tak khud attach na karna ho.**

---

## 3. JSON File Parsing — Concept (Full demo skip kiya)

### Kya Hai
JSON ko `assets/` folder mein alag `.json` file mein rakhna (hardcoded string ki jagah), aur file se read karke parse karna. Real app mein mock data / config isi tarah store hota hai.

> **Note:** Iska poora demo nahi banaya (Array Parsing se logic 100% duplicate hota) — sirf concept yahan record kar rahe hain.

### Code Pattern
```kotlin
private fun loadJsonFromAssets(fileName: String): String {
    return assets.open(fileName)          // InputStream khulta hai
        .bufferedReader()                  // Reader banta hai
        .use { it.readText() }             // poora text padho, phir auto-close
}

// Use:
val jsonString = loadJsonFromAssets("posts.json")
val posts = Gson().fromJson(jsonString, Array<Post>::class.java).toList()
```

### Common Interview Questions
**Q: `.use { }` kya karta hai?**
> Kotlin ka `Closeable` extension function — block khatam hote hi (ya exception aane pe bhi) stream **automatically close** ho jaata hai. Manually `close()` call nahi karna padta, memory leak nahi hota. Java ke `try-with-resources` jaisa hai.

**Q: assets folder kab use karte hain?**
> Jab app ke saath koi static/raw file bundle karni ho — mock JSON data, config files, fonts, etc. `context.assets.open(fileName)` se access karte hain.

---

## Interview Mein Bolna — JSON Parsing Summary

> *"Gson JSON string ko reflection use karke Kotlin object mein convert karta hai — JSON keys data class properties se naam ke basis pe match hoti hain. Object ke liye `Post::class.java` use karte hain, Array/List ke liye `Array<Post>::class.java` — kyunki generic type erasure ki wajah se `List<Post>::class.java` directly kaam nahi karta, Array apna type runtime pe retain karta hai. Data source hardcoded string, ya assets file se aa sakta hai — dono cases mein final parsing step same rehta hai. Retrofit ke andar yeh saara kaam GsonConverterFactory automatically karta hai."*
