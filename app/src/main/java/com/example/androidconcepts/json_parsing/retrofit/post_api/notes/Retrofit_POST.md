# Retrofit POST — Notes

`@Body` se naya resource create karna — GET wala **MVVM + Repository + sealed Resource** pattern reuse, bas alag Repository/ViewModel (`PostApiRepository`, `PostApiViewModel`) is baar.

---

## Kya Hai

POST request se **naya resource create** karte hain. GET se fark:
- GET mein **body nahi hoti**, sirf URL (path/query params)
- POST mein **`@Body`** se poora object JSON banke request body mein jaata hai

---

## Request Model vs Response Model — Important Fark

| | Request (`NewPost`) | Response (`Post`) |
|---|---|---|
| Fields | `userId, title, body` | `userId, id, title, body` |
| `id` | ❌ Nahi hai | ✅ Hai — **server generate karta hai** |

**Kyun alag model?** Jab naya post create kar rahe ho, aapko `id` pata hi nahi (woh abhi exist nahi karta) — server naya `id` assign karega aur response mein wapas bhejega. Isliye request bhejne wala model (`NewPost`) aur response wala model (`Post`) **jaan-bujh ke alag** hain.

---

## Architecture

```
View (RetrofitPostActivity) → ViewModel (PostApiViewModel) → Repository (PostApiRepository) → RetrofitInstance.apiService
```

> GET wali `GetApiRepository`/`GetApiViewModel` ko touch nahi kiya — **alag Repository aur ViewModel** banaye (`PostApiRepository`, `PostApiViewModel`), lekin **`GetApiResource` sealed class reuse** kiya (naya banane ki zaroorat nahi — Loading/Success/Error pattern universal hai).

---

## Code — Poori Chain

### 1. `NewPost.kt` — Request Model
```kotlin
data class NewPost(
    val userId: Int,
    val title: String,
    val body: String
)
```

### 2. `ApiService.kt` — naya function
```kotlin
@POST("posts")
suspend fun createPost(@Body newPost: NewPost): Post
```
- `@POST("posts")` — same endpoint jaisa GET mein tha, lekin method `POST`
- `@Body newPost: NewPost` — poora object JSON banke request body mein jaata hai
- Return type `Post` hai (response model, `id` ke saath)

### 3. `PostApiRepository.kt`
```kotlin
class PostApiRepository(
    private val apiService: ApiService = RetrofitInstance.apiService
) {
    suspend fun createPost(newPost: NewPost): Post {
        return apiService.createPost(newPost)
    }
}
```

### 4. `PostApiViewModel.kt`
```kotlin
class PostApiViewModel(
    private val repository: PostApiRepository = PostApiRepository()
) : ViewModel() {
    private val _createPostData = MutableLiveData<GetApiResource<Post>>()
    val createPostData: LiveData<GetApiResource<Post>> = _createPostData

    fun createPost(title: String, body: String) {
        _createPostData.value = GetApiResource.Loading
        viewModelScope.launch {
            try {
                val newPost = NewPost(userId = 1, title = title, body = body)
                _createPostData.value = GetApiResource.Success(repository.createPost(newPost))
            } catch (e: Exception) {
                _createPostData.value = GetApiResource.Error(e.message ?: "Unknown Error")
            }
        }
    }
}
```
> `GetApiResource` GET topic ke `get_api` package se **import** kiya — naya sealed class nahi banaya. Loading/Success/Error pattern GET jaisa hi hai, bas is baar Repository/data alag hai.

### 5. `RetrofitPostActivity.kt` — View
```kotlin
private val viewModel: PostApiViewModel by viewModels()

private fun bindUi() {
    binding.btnCreate.setOnClickListener {
        val title = binding.etTitle.text.toString().trim()
        val body = binding.etBody.text.toString().trim()
        viewModel.createPost(title, body)
    }

    viewModel.createPostData.observe(this) { response ->
        when (response) {
            is GetApiResource.Loading -> binding.tvStatus.text = "Loading..."
            is GetApiResource.Success -> {
                binding.tvStatus.text = "✅ Created"
                binding.tvResultId.text = response.data.id.toString()
                binding.tvResultTitle.text = response.data.title
            }
            is GetApiResource.Error -> binding.tvStatus.text = "❌ ${response.message}"
        }
    }
}
```

---

## Key Concepts

| Concept | Detail |
|---|---|
| `@Body` | Poora Kotlin object JSON banke request body mein jaata hai. GET mein body nahi hoti — sirf POST/PUT/PATCH mein. |
| Request vs Response model | Request (`NewPost`) mein `id` nahi (server assign karega); Response (`Post`) mein `id` hai. |
| `is GetApiResource.X ->` | Type check (Java `instanceof` jaisa) — `response` ka runtime type check karta hai. Pass hone pe **smart cast** milta hai (`.data`/`.message` bina manual cast ke access hote hain). |
| Alag Repository/ViewModel, same Resource | `GetApiResource` sealed class ek baar banayi, poore Retrofit section mein reuse hoti hai — har naye feature (GET/POST/PUT/DELETE) ke liye naya banane ki zaroorat nahi. |

---

## Common Interview Questions

**Q: `@Body` kya karta hai, GET mein kyun use nahi hota?**
> `@Body` poora object serialize karke (Gson se JSON banake) HTTP request ki **body** mein bhejta hai. GET requests conceptually sirf "data mango" karte hain — inke paas body nahi hoti (HTTP spec ke hisaab se), isliye GET mein `@Body` use nahi hota, sirf `@Path`/`@Query` se URL banta hai.

**Q: Request model mein `id` kyun nahi rakhte POST ke liye?**
> Naya resource create ho raha hai — uska `id` abhi exist hi nahi karta, **server generate karta hai** create hone ke baad. Request model mein `id` bhejna galat/meaningless hoga. Response model mein server jo `id` assign karega, woh wapas milta hai.

**Q: Ek hi `GetApiResource` sealed class GET aur POST dono mein kyun use ki, alag kyun nahi banayi?**
> `Resource<T>` (Loading/Success/Error) ek **generic, reusable pattern** hai — kisi bhi API call ke result ko represent kar sakta hai, chahe woh GET ho, POST ho, ya kuch aur. Isko har feature ke liye dobara banana **code duplication** hoga. Ek jagah define karke poore app mein reuse karna best practice hai.

**Q: `is` keyword kyun use karte hain `when` mein, `==` kyun nahi?**
> `is` **type check** karta hai (yeh object kis class ka hai) aur **smart cast** deta hai — pass hone ke baad us type ke properties bina manual cast ke access ho jaate hain. `Success`/`Error` `data class` hain jinka data har baar alag hota hai, isliye value-equality (`==`) se compare karna galat/unnecessary hai — humein sirf "shape" (type) match karni hai.

---

## Gotchas — Real Mistakes Jo Is Session Mein Hue

**1. `PostApiRepository` mein default value bhool jaana**
```kotlin
// GALAT — PostApiRepository() bina argument ke banate hi "no value passed for parameter" error
class PostApiRepository(private val apiService: ApiService)

// SAHI
class PostApiRepository(private val apiService: ApiService = RetrofitInstance.apiService)
```
> Bina default value ke, jahan bhi `PostApiRepository()` banana ho (jaise ViewModel mein), har baar explicitly `RetrofitInstance.apiService` pass karna padta — default value se yeh automatic ho jaata hai.

---

## Interview Mein Bolna

> *"POST request mein @Body annotation se poora object JSON banke request body mein jaata hai — GET mein sirf URL params hote hain, body nahi. Request model aur response model jaan-bujh ke alag rakhte hain kyunki request mein id nahi bhejte (server generate karega), response mein wapas milta hai. Architecture GET jaisa hi hai — View se ViewModel, ViewModel se Repository, Repository se Retrofit — bas is baar naya Repository/ViewModel banaya feature ke hisaab se, lekin sealed class Resource<T> wahi reuse ki jo pehle bani thi, kyunki Loading/Success/Error ka pattern har API call ke liye same hota hai."*
