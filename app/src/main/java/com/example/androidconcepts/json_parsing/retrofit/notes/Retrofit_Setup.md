# Retrofit Setup — Notes

Retrofit + OkHttp + Gson ka foundation setup — poore Retrofit + Coroutines section ka base.

---

## Kya Hai

**Retrofit** — Square ka type-safe HTTP client library, jo **OkHttp ke upar** built hai. Retrofit khud networking nahi karta — aap ek `interface` (annotations ke saath) define karte ho, Retrofit runtime pe uska implementation banata hai jo actual network calls OkHttp se karwata hai.

```
Aapka Code → Retrofit (declarative layer) → OkHttp (actual networking) → Internet
```

---

## Internally Kaam Kaise Karta Hai — Dynamic Proxy

`retrofit.create(ApiService::class.java)` call karne pe, Retrofit **runtime pe ek "fake" implementation** banata hai (Java `Proxy` class se — **dynamic proxy** pattern). `ApiService` sirf ek interface hai, koi real code nahi.

Jab bhi `api.getPost()` jaisa function call hota hai:
1. Ek **central "catcher" function** (Retrofit ka banaya) intercept karta hai
2. Reflection se function ka **annotation padhta hai** (`@GET("posts/1")`)
3. Us annotation ke hisaab se **actual HTTP request** banata hai
4. OkHttp se bhejta hai, response aane pe Gson se parse karke return karta hai

**Analogy:** Interface ek "blank order form" hai. `retrofit.create()` ek "universal waiter" hire karta hai jo kisi bhi form ka label (annotation) padh ke sahi kaam kar sakta hai — bina yeh jaane ki form mein kya likha hai, pehle se.

---

## Code — `RetrofitInstance.kt`

```kotlin
object RetrofitInstance {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
```

### Line-by-Line

- **`object RetrofitInstance`** — Kotlin singleton. Retrofit instance banana expensive hai, isliye poori app mein **ek hi baar** banta hai.
- **`HttpLoggingInterceptor().apply { level = BODY }`** — Logcat mein har request/response (URL, headers, body) print karta hai — debugging ke liye. `Level.BODY` sabse detailed hai (options: `NONE`, `BASIC`, `HEADERS`, `BODY`).
- **`OkHttpClient.Builder().addInterceptor(...).build()`** — Builder pattern; interceptor OkHttpClient level pe add hota hai, isliye **saari requests pe automatically** apply hota hai.
- **`Retrofit.Builder().baseUrl(...).client(...).addConverterFactory(...)`**:
  - `.baseUrl()` — common URL prefix, **hamesha `/` pe end** hona chahiye
  - `.client(okHttpClient)` — humara logging-wala OkHttpClient use karo
  - `.addConverterFactory(GsonConverterFactory.create())` — JSON response automatically Gson se Kotlin object mein convert ho
- **`val apiService: ApiService by lazy { retrofit.create(...) }`** — `by lazy` ka matlab: **pehli baar use hone pe hi banega**, uske baad cache ho jaata hai (dobara nahi banega).

---

## HTTP Status Codes

Server response ke saath ek **status code** aata hai jo batata hai request ka result kya raha.

| Range | Matlab | Common Examples |
|---|---|---|
| **2xx** | Success | `200 OK`, `201 Created`, `204 No Content` |
| **3xx** | Redirect | `301 Moved`, `304 Not Modified` |
| **4xx** | Client error (request mein galti) | `400 Bad Request`, `401 Unauthorized`, `404 Not Found` |
| **5xx** | Server error (server side galti) | `500 Internal Server Error`, `503 Service Unavailable` |

### Retrofit Mein Yeh Kaise Surface Hote Hain

Humare `ApiService` functions **seedha data type return** karte hain (`suspend fun getPostById(id: Int): Post` — `Post` directly, `Response<Post>` nahi). Is setup mein Retrofit **khud check karta hai** status code:

- **2xx aaye** → response body parse karke normally return karta hai (jo humara `try` block handle karta hai)
- **2xx na aaye** (jaise `404`, `500`) → Retrofit **`HttpException` throw karta hai** (ek `RuntimeException` subtype) — is exception mein status code bhi hota hai

Isi wajah se humara generic `catch (e: Exception)` in ViewModels **HTTP errors (404/500) ko bhi pakad leta hai**, sirf network failures (no internet) ko nahi:

```kotlin
try {
    _post.value = GetApiResource.Success(repository.getPostById(id))
} catch (e: Exception) {
    // yahan "no internet" (IOException) aur "404 Not Found" (HttpException) — dono aa sakte hain
    _post.value = GetApiResource.Error(e.message ?: "Unknown Error")
}
```

**Agar specific status code chahiye ho** (jaise 401 pe logout karna hai), toh `HttpException` specifically catch kar sakte hain:
```kotlin
catch (e: HttpException) {
    val code = e.code()  // jaise 404, 401, 500
    if (code == 401) { /* logout logic */ }
}
```

### Alternative — `Response<T>` Wrapper (Manual Control)

Agar `suspend fun getPostById(id: Int): Response<Post>` likhte (seedha `Post` ki jagah `Response<Post>`), toh Retrofit **exception nahi throwता** — chahe status code kuch bhi ho, function normally return karta hai. Tab manually check karna padta:
```kotlin
val response = apiService.getPostById(id)
if (response.isSuccessful) {
    val post = response.body()
} else {
    val errorCode = response.code()
}
```
> **Fark:** Direct return type (`Post`) — exception-based error handling, simpler code. `Response<T>` — manual control, koi exception nahi, har jagah `isSuccessful` check karna padta hai. Humare project mein **direct return type** use kiya hai (simpler, try-catch ke saath consistent).

---

## Common Interview Questions

**Q: Retrofit khud HTTP calls karta hai?**
> Nahi — Retrofit sirf declarative layer hai. Actual networking **OkHttp** karta hai. Retrofit annotations padh ke OkHttp requests banata hai aur Gson se response parse karta hai.

**Q: `retrofit.create()` internally kaise kaam karta hai?**
> Java **Dynamic Proxy** pattern use hota hai. Interface ka koi real implementation nahi hota — Retrofit reflection se annotations padh ke runtime pe ek proxy object banata hai jo har method call ko intercept karke actual network request mein convert karta hai.

**Q: `RetrofitInstance` ko `object` (singleton) kyun banaya, `class` kyun nahi?**
> Retrofit instance banana (baseUrl, converter, client setup) expensive operation hai. Singleton se yeh **ek hi baar** banta hai, poori app reuse karti hai — har baar naya banane se performance aur memory dono waste hote.

**Q: `by lazy` ka fayda kya hai `apiService` ke liye?**
> `apiService` **tabhi banega jab pehli baar use hoga**, app start hote hi nahi. Aur ek baar ban gaya toh cache ho jaata hai — dobara access karne pe wahi purana object milega.

**Q: HttpLoggingInterceptor production build mein bhi rakhna chahiye?**
> Nahi — yeh sensitive data (tokens, request/response bodies) Logcat mein expose karta hai. Real projects mein ise sirf **debug build** mein enable karte hain (`BuildConfig.DEBUG` check se), release build mein nahi.

**Q: 404 ya 500 status code aaye toh app mein kya hota hai?**
> Jab `ApiService` function seedha data type return karta hai (`Response<T>` nahi), Retrofit non-2xx status code pe **`HttpException` throw karta hai**. Yeh humare `catch (e: Exception)` block mein pakda jaata hai — isliye HTTP errors (404/500) bhi network errors (no internet) ki tarah gracefully handle ho jaate hain, app crash nahi hoti.

**Q: `suspend fun getPost(): Post` aur `suspend fun getPost(): Response<Post>` mein fark?**
> Pehla — direct return type — non-2xx pe `HttpException` throw hota hai, try-catch se handle karte hain. Doosra — `Response<T>` wrapper — koi exception nahi aata, manually `response.isSuccessful` aur `response.code()` check karna padta hai. Direct type simpler hai jab sirf success/failure jaanna ho; `Response<T>` chahiye jab headers ya exact status code ki fine-grained zaroorat ho.

---

## Gotchas — Real Mistakes Jo Is Project Mein Hue

```kotlin
// GALAT — empty baseUrl, crash: IllegalArgumentException: baseUrl must be a valid URL
private val base_url = ""

// SAHI
private const val BASE_URL = "https://jsonplaceholder.typicode.com/"
```

```kotlin
// GALAT — private apiService, Activity se access nahi ho sakta ("cannot access" error)
private val apiService: ApiService by lazy { ... }

// SAHI — public, taaki Repository/ViewModel/Activity use kar sake
val apiService: ApiService by lazy { ... }
```

> **Naming convention:** `base_url` (snake_case) ki jagah `BASE_URL` (const val ke saath SCREAMING_SNAKE_CASE) — Kotlin mein compile-time constants ke liye yeh standard convention hai.

---

## Interview Mein Bolna

> *"Retrofit ek type-safe HTTP client hai jo OkHttp ke upar built hai — actual networking OkHttp karta hai, Retrofit sirf annotation-based declarative API define karne deta hai. retrofit.create() Java Dynamic Proxy pattern use karta hai — reflection se annotations padh ke runtime pe interface ka implementation banaya jaata hai. Setup mein Retrofit instance ko singleton (object) banate hain kyunki banana expensive hai, aur apiService ko by lazy se banate hain taaki pehli use tak wait kare aur phir cache ho jaaye."*
