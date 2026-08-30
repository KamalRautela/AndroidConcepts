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
