# Retrofit GET — Notes

Real network call — `@Path` (single object) + `@Query` (list) — poori **MVVM + Repository + sealed Resource** chain ke saath. Yeh architecture ab GET/POST/PUT/DELETE sabme reuse hoga.

---

## Kya Hai

GET request se data **fetch** karte hain. Do variants cover kiye:
- **`@Path`** — URL ke andar placeholder replace karta hai, specific resource identify karta hai (`posts/{id}` → `posts/1`)
- **`@Query`** — URL ke end mein `?key=value` add karta hai, filtering ke liye (`posts?userId=1`)

---

## Architecture — Poori Chain

```
View (Activity) → ViewModel → Repository → RetrofitInstance.apiService
```

> **Kyun yeh chain?** MVVM section mein decide kiya tha ki Repository Pattern Retrofit ke saath hi implement hoga (real DataSource ke saath) — yeh wahi jagah hai. ViewModel ko pata nahi data kahan se aa raha (Retrofit/Room/cache), sirf Repository se maangta hai.

---

## Code — Poori Chain, Ek-Ek Layer

### 1. `Post.kt` — Response Model
```kotlin
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)
```
> Naya model banaya (purane `json_local_parsing.Post` se alag) kyunki woh extra fields (`rating`, `isPublished`) rakhta tha jo real API mein exist hi nahi karte.

### 2. `ApiService.kt` — Annotations
```kotlin
interface ApiService {
    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id: Int): Post

    @GET("posts")
    suspend fun getPostsByUserId(@Query("userId") userId: Int): List<Post>
}
```

### 3. `GetApiRepository.kt` — Repository
```kotlin
class GetApiRepository(
    private val apiService: ApiService = RetrofitInstance.apiService
) {
    suspend fun getPostById(id: Int): Post = apiService.getPostById(id)
    suspend fun getPostsByUserId(userId: Int): List<Post> = apiService.getPostsByUserId(userId)
}
```

### 4. `GetApiResource.kt` — Sealed Class (Loading/Success/Error)
```kotlin
sealed class GetApiResource<out T> {
    object Loading : GetApiResource<Nothing>()
    data class Success<T>(val data: T) : GetApiResource<T>()
    data class Error(val message: String) : GetApiResource<Nothing>()
}
```

#### Kaise Use Hota Hai — Real Example Se Trace Karo

Bhool jao `out`/`Nothing` jaise words — pehle dekho yeh cheez **real code mein kaise kaam karti hai**. `Loading`, `Success`, `Error` — teeno **ek hi family ke members hain** (jaise teen bhai-behen ek surname share karte hain).

`loadPostById(1)` call hone pe step-by-step kya hota hai:

```kotlin
fun loadPostById(id: Int) {
    _post.value = GetApiResource.Loading                                    // Step 1
    viewModelScope.launch {
        try {
            _post.value = GetApiResource.Success(repository.getPostById(id)) // Step 2
        } catch (e: Exception) {
            _post.value = GetApiResource.Error(e.message ?: "Unknown Error") // Step 3 (agar fail ho)
        }
    }
}
```

1. **`_post.value = GetApiResource.Loading`** — `_post` mein "Loading" family-member store hua → screen pe "Loading..." dikhta hai
2. **Network call complete hui, Post mila** — `Success(post)` mein woh `Post` object **daal diya gaya** (jaise dabbe mein cheez rakhi) → `_post` ab "Success (jisme Post hai)" hai
3. **Agar fail hui (no internet)** — `Error("message")` mein error text daala → `_post` ab "Error (jisme message hai)" hai

`_post` variable **kabhi bhi in teeno mein se koi bhi ban sakta hai** — traffic light jaisa, kabhi Green kabhi Red kabhi Yellow.

**Activity mein pata kaise chalta hai abhi kaunsa hai:**
```kotlin
when (response) {
    is GetApiResource.Loading -> binding.tvStatus.text = "Loading......"
    is GetApiResource.Success -> binding.tvTitle.text = response.data.title   // .data = jo Post andar rakha tha
    is GetApiResource.Error -> binding.tvStatus.text = response.message       // .message = jo text andar rakha tha
}
```
`when` check karta hai "abhi `response` teeno mein se kaunsa hai", aur uske hisaab se andar rakha data (`.data` ya `.message`) nikal leta hai.

#### Definition Likhne Ka Golden Rule

```kotlin
sealed class GetApiResource<out T> {
    object Loading : GetApiResource<Nothing>()
    data class Success<T>(val data: T) : GetApiResource<T>()
    data class Error(val message: String) : GetApiResource<Nothing>()
}
```

Har member ke liye ek simple rule follow hua:
- **`Loading`** — koi data nahi → **`object`** likho (singleton, `()` bhi nahi lagta)
- **`Success<T>(val data: T)`** — data hai jiska type baad mein tay hoga → **`data class`** + apna `<T>`
- **`Error(val message: String)`** — data hai lekin type hamesha fix (`String`) → **`data class`**, generic `<T>` ki zaroorat nahi

> **Golden Rule:** Data nahi → `object ... : Resource<Nothing>()`. Data hai → `data class ...<T>(val x: T) : Resource<T>()`. Yeh follow karoge toh code kaam karega, `out`/`Nothing` ka deep reasoning turant samajhna zaroori nahi.

**`when` exhaustive kyun ban jaata hai (fayda):**
```kotlin
when (state) {
    is GetApiResource.Loading -> ...
    is GetApiResource.Success -> ...
    // Error case bhool gaye toh COMPILE ERROR — sealed class ki wajah se compiler ko saare cases pata hain
}
```
Normal (non-sealed) class ke saath yeh possible nahi — koi bhi naya subtype bana sakta hai, compiler kabhi confirm nahi kar sakta "sab cases cover hue ya nahi".

#### Advanced (Optional) — `out T` aur `Nothing` Kyun Chahiye

Yeh part sirf tab padhna jab "Golden Rule" se aage jaake **poori depth** samajhni ho — practical use ke liye upar ka portion kaafi hai.

- **`out T`** = covariant — is class ko sirf `T` **return/dena** hai, input nahi lena. Isse Kotlin allow karta hai: agar `Cat` `Animal` ka subtype hai, toh `GetApiResource<Cat>` ko `GetApiResource<Animal>` ki jagah use kar sakte ho.
- **`Nothing`** = Kotlin ka special type jiska koi instance kabhi nahi banta, aur jo **har type ka subtype** hota hai.
- In dono ko milake — `GetApiResource<Nothing>` (jaise `Loading`) **automatically** `GetApiResource<Post>` ya `GetApiResource<List<Post>>` ki jagah fit ho jaata hai, bina explicitly type specify kiye:
  ```kotlin
  val postBox: GetApiResource<Post> = GetApiResource.Loading  // Loading is GetApiResource<Nothing>, phir bhi fit ho gaya
  ```

### 5. `GetApiViewModel.kt` — ViewModel
```kotlin
class GetApiViewModel(
    private val repository: GetApiRepository = GetApiRepository()
) : ViewModel() {
    private val _post = MutableLiveData<GetApiResource<Post>>()
    val post: LiveData<GetApiResource<Post>> = _post

    private val _postList = MutableLiveData<GetApiResource<List<Post>>>()
    val postList: LiveData<GetApiResource<List<Post>>> = _postList

    fun loadPostById(id: Int) {
        _post.value = GetApiResource.Loading
        viewModelScope.launch {
            try {
                _post.value = GetApiResource.Success(repository.getPostById(id))
            } catch (e: Exception) {
                _post.value = GetApiResource.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun loadPostListByUserId(userId: Int) {
        _postList.value = GetApiResource.Loading
        viewModelScope.launch {
            try {
                _postList.value = GetApiResource.Success(repository.getPostsByUserId(userId))
            } catch (e: Exception) {
                _postList.value = GetApiResource.Error(e.message ?: "Unknown Error")
            }
        }
    }
}
```

### 6. `RetrofitGetActivity.kt` — View
```kotlin
private val viewModel: GetApiViewModel by viewModels()

private fun bindUi() {
    binding.btnGetById.setOnClickListener { viewModel.loadPostById(1) }
    binding.btnGetByUser.setOnClickListener { viewModel.loadPostListByUserId(1) }

    viewModel.post.observe(this) { response ->
        when (response) {
            is GetApiResource.Loading -> binding.tvStatus.text = "Loading......"
            is GetApiResource.Success -> {
                binding.tvStatus.text = "Success"
                binding.tvTitle.text = response.data.title
                binding.tvUserId.text = response.data.userId.toString()
            }
            is GetApiResource.Error -> binding.tvStatus.text = response.message
        }
    }

    viewModel.postList.observe(this) { response ->
        when (response) {
            is GetApiResource.Loading -> binding.tvStatus.text = "Loading......"
            is GetApiResource.Success -> {
                binding.tvStatus.text = "Success"
                binding.containerResults.removeAllViews()
                response.data.forEach { post ->
                    val row = layoutInflater.inflate(R.layout.item_post_row, binding.containerResults, false)
                    row.findViewById<TextView>(R.id.tvItemTitle).text = post.title
                    row.findViewById<TextView>(R.id.tvItemId).text = post.id.toString()
                    binding.containerResults.addView(row)
                }
            }
            is GetApiResource.Error -> binding.tvStatus.text = response.message
        }
    }
}
```

#### List Rows Dynamically Add Karna — Line-by-Line

```kotlin
binding.containerResults.removeAllViews()
response.data.forEach { post ->
    val row = layoutInflater.inflate(R.layout.item_post_row, binding.containerResults, false)
    row.findViewById<TextView>(R.id.tvItemTitle).text = post.title
    row.findViewById<TextView>(R.id.tvItemId).text = post.id.toString()
    binding.containerResults.addView(row)
}
```

1. **`removeAllViews()`** — `containerResults` (khaali `LinearLayout`) se **purani rows hata deta hai**. Zaroori hai warna button dobara click karne pe rows duplicate ho jaayengi (purani ke upar naya set add ho jaayega).
2. **`response.data.forEach { post -> ... }`** — `response.data` yahan `List<Post>` hai (kyunki is context mein `Success<T>` ka `T = List<Post>`). `forEach` har `Post` ke liye `{ }` wala code **ek baar** chalata hai.
3. **`layoutInflater.inflate(R.layout.item_post_row, binding.containerResults, false)`** — `item_post_row.xml` ko **memory mein ek real View banata hai** (screen pe abhi nahi dikhata). `false` zaroori hai — warna `row` khud `containerResults` ban jaata (Array Parsing wala bug), aur `addView()` pe crash hota.
4. **`row.findViewById<TextView>(...).text = ...`** — us naye row ke andar ke `tvItemTitle`/`tvItemId` TextViews access karke unme is post ka data set karte hain.
5. **`addView(row)`** — ab `row` ko **actually screen pe dikhata hai**, `containerResults` ke andar add karke.

> **Summary:** Purani list clean karo → har Post ke liye ek row memory mein banao → data bharo → container mein add karo (screen pe dikhao) → repeat next post ke liye.

---

## Key Concepts

| Concept | Detail |
|---|---|
| `@Path("id")` | `{id}` placeholder replace karta hai URL mein — mandatory, resource identify karta hai |
| `@Query("userId")` | `?userId=1` add karta hai — optional, filter karta hai |
| `sealed class GetApiResource<out T>` | 3 fixed states (`Loading`/`Success`/`Error`), har state apna data carry kar sakta hai, `when` exhaustive check milta hai |
| `object Loading` | Koi data nahi, singleton — ek hi instance reuse hota hai |
| `data class Success<T>` / `Error` | Har baar alag data (`data`/`message`), isliye naya instance banta hai (`()` ke saath) |
| `GetApiResource<Nothing>` + `out T` | `Nothing` har type ka subtype hai — covariance (`out`) ki wajah se `Loading`/`Error` kisi bhi `GetApiResource<X>` ki jagah fit ho jaate hain |
| `viewModelScope.launch { try/catch }` | Coroutine ViewModel ke lifecycle se bound, network error se app crash nahi hoti |

---

## Common Interview Questions

**Q: `@Path` aur `@Query` mein kab kaunsa use karein?**
> `@Path` — resource ko identify karne ke liye, URL ka hi part hota hai, mandatory (jaise `posts/5`). `@Query` — filter/sort/pagination jaise optional parameters ke liye, `?key=value` format mein.

**Q: Sealed class kyun use ki, plain `String` status kyun nahi?**
> Sealed class **type-safe** hai — har state (`Success`, `Error`) apna specific data carry kar sakta hai (result ya error message). `when` compiler-enforced exhaustive hota hai — koi case miss ho toh compile error, runtime crash nahi. Plain String se yeh guarantee nahi milti.

**Q: `Resource<T>` mein `Loading`/`Error` ko `Nothing` kyun diya, `T` kyun nahi?**
> `Loading` aur `Error` ke paas koi `T`-type ka data hai hi nahi. `Nothing` Kotlin ka special type hai jo **har type ka subtype** hota hai — `out T` (covariance) ki wajah se `GetApiResource<Nothing>` automatically kisi bhi `GetApiResource<Post>` ya `GetApiResource<List<Post>>` ki jagah fit ho jaata hai.

**Q: Repository mein `ApiService` ko default value kyun diya constructor mein?**
> Manual Dependency Injection pattern — production mein `GetApiRepository()` bina argument banao toh real `RetrofitInstance.apiService` use hota hai; testing mein `GetApiRepository(fakeApiService)` pass karke bina real network ke test kar sakte hain.

---

## Gotchas — Real Mistakes Jo Is Project Mein Hue

**1. `MutableLiveData` banate waqt `()` bhool jaana**
```kotlin
// GALAT — "does not have a companion object" error
private val _post = MutableLiveData<GetApiResource<Post>>

// SAHI — constructor call, () zaroori hai
private val _post = MutableLiveData<GetApiResource<Post>>()
```

**2. `viewModelScope` resolve na hona**
> Wajah: class `ViewModel()` extend nahi kar rahi thi. `viewModelScope` sirf `ViewModel` subclasses pe available extension property hai.
```kotlin
// GALAT
class GetApiViewModel(...) { ... }

// SAHI
class GetApiViewModel(...) : ViewModel() { ... }
```

**3. Sealed class ke `Error` mein unnecessary generic**
```kotlin
// GALAT — <T> bekaar hai, Error ke paas T-type data hai hi nahi
data class Error<T>(val message: String) : GetApiResource<T>()

// SAHI — Loading jaisa hi, Nothing use karo
data class Error(val message: String) : GetApiResource<Nothing>()
```

**4. ViewModel function ka naam Activity mein call karte waqt mismatch**
```kotlin
// ViewModel mein: fun loadPostListByUserId(userId: Int)
// Activity mein galat call:
viewModel.loadPostListById(1)   // naam match nahi — compile error

// SAHI — dono jagah same naam
viewModel.loadPostListByUserId(1)
```

**5. `postList` LiveData observe karna bhool jaana**
> `post.observe()` likha, `postList.observe()` add karna bhool gaye — button click hone pe data ViewModel mein aata tha lekin Activity kabhi dekhti hi nahi thi, list hamesha khaali dikhti.

---

## Interview Mein Bolna

> *"GET request mein @Path se specific resource identify karte hain, @Query se filtering. Real network call MVVM flow follow karta hai — View button click ko ViewModel forward karta hai, ViewModel viewModelScope mein Repository call karta hai, Repository Retrofit se data laata hai. Result ek sealed class Resource<T> mein wrap hota hai jisme Loading/Success/Error teen states hain — isse UI ko exhaustive when se har state handle karna padta hai, koi case miss nahi hota. Try-catch Repository call ke around ViewModel mein hota hai, isliye network failure se app crash nahi hoti, status LiveData mein Error state mein error dikh jaata hai."*
