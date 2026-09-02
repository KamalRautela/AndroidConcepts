# Retrofit PUT — Notes

Existing resource ko **poora replace/update** karna — GET/POST wala MVVM + Repository + sealed Resource pattern reuse, is baar `@Path` aur `@Body` **dono ek saath**.

---

## Kya Hai

PUT request se **existing resource ko poora update** karte hain — sabhi fields ka naya set bhejte hain (chahe koi field change na bhi hui ho).

## POST vs PUT vs PATCH

| Method | Kaam | URL | Body |
|---|---|---|---|
| POST | Naya resource create | `posts` (id nahi) | Naye data ka poora set |
| **PUT** | Existing resource poora replace | `posts/{id}` | Poora naya data (saare fields) |
| PATCH | Existing resource ka sirf kuch fields update | `posts/{id}` | Sirf jo fields change karni hain |

---

## Architecture

```
View (RetrofitPutApiActivity) → ViewModel (PutApiViewModel) → Repository (PutApiRepository) → RetrofitInstance.apiService
```

> Naya `PutApiRepository`/`PutApiViewModel` banaya (POST jaisa pattern), lekin `GetApiResource` sealed class **reuse** kiya — naya sealed class nahi banaya.

---

## Code — Poori Chain

### 1. `ApiService.kt` — naya function
```kotlin
@PUT("posts/{id}")
suspend fun updatePost(@Path("id") id: Int, @Body post: Post): Post
```
- **`@Path` aur `@Body` dono ek saath** — pehli baar. `@Path("id")` batata hai **kaunsa** resource, `@Body post: Post` mein **poora naya data**.
- Request model **`Post` hi reuse** kiya (POST mein `NewPost` alag banaya tha bina `id`ke) — kyunki PUT mein `id` pehle se pata hai (path mein hai), toh request body mein bhi `id` bhejna valid hai.

### 2. `PutApiRepository.kt`
```kotlin
class PutApiRepository(
    private val apiService: ApiService = RetrofitInstance.apiService
) {
    suspend fun updatePost(id: Int, post: Post): Post {
        return apiService.updatePost(id = id, post = post)
    }
}
```

### 3. `PutApiViewModel.kt`
```kotlin
class PutApiViewModel(
    private val repository: PutApiRepository = PutApiRepository()
) : ViewModel() {
    private val _updatePostLiveData = MutableLiveData<GetApiResource<Post>>()
    val updatePostLiveData: LiveData<GetApiResource<Post>> = _updatePostLiveData

    fun updatePost(id: Int, title: String, body: String) {
        _updatePostLiveData.value = GetApiResource.Loading
        viewModelScope.launch {
            try {
                val updatedPost = Post(userId = 1, id = id, title = title, body = body)
                _updatePostLiveData.value = GetApiResource.Success(repository.updatePost(id, updatedPost))
            } catch (e: Exception) {
                _updatePostLiveData.value = GetApiResource.Error(e.message ?: "Unknown error")
            }
        }
    }
}
```

### 4. `RetrofitPutApiActivity.kt` — View
```kotlin
private val viewModel: PutApiViewModel by viewModels()

private fun bindUi() {
    binding.btnUpdate.setOnClickListener {
        val id = binding.etId.text.toString().toIntOrNull() ?: 0
        val title = binding.etTitle.text.toString()
        val body = binding.etBody.text.toString()
        viewModel.updatePost(id = id, title = title, body = body)
    }

    viewModel.updatePostLiveData.observe(this) { response ->
        when (response) {
            is GetApiResource.Loading -> binding.tvStatus.text = "Updating"
            is GetApiResource.Success -> {
                binding.tvStatus.text = "✅ Updated"
                binding.tvResultId.text = response.data.id.toString()
                binding.tvResultTitle.text = response.data.title
            }
            is GetApiResource.Error -> binding.tvStatus.text = response.message
        }
    }
}
```

---

## Key Concepts

| Concept | Detail |
|---|---|
| `@Path` + `@Body` saath | PUT mein dono ek hi function mein — "kaunsa" (Path) + "kya naya data" (Body) |
| `toIntOrNull() ?: 0` | EditText se `String` aata hai, `Int` chahiye. Agar user galat text bhare (letters), `toIntOrNull()` null return karega, `?: 0` se safe fallback |
| Request model = Response model | PUT mein `Post` hi request aur response dono ke liye — POST se alag jahan `NewPost`/`Post` alag the |
| `UnknownHostException` | Real testing mein aaya — DNS resolve nahi hua (no internet). `catch (e: Exception)` ne ise pakad liya, app crash nahi hui — error handling ka real-world proof |

---

## Common Interview Questions

**Q: PUT idempotent hota hai, iska matlab kya hai?**
> Same PUT request **baar-baar bhejne se result same rehta hai** — resource ek hi final state mein pahunchta hai chahe 1 baar call karo ya 5 baar. POST idempotent nahi hai — har call se naya resource ban sakta hai (5 baar POST karo toh 5 naye resources ban sakte hain).

**Q: PUT mein `@Path` aur `@Body` dono kyun chahiye, POST mein sirf `@Body` kyun tha?**
> POST naya resource banata hai — uska koi `id` hai hi nahi abhi, isliye URL mein path parameter ki zaroorat nahi (`posts`). PUT **existing** resource update karta hai — usko **identify** karna padta hai (`@Path("id")`), aur uska **naya data** bhejna padta hai (`@Body`).

**Q: PUT mein Request model POST se alag kyun nahi banaya?**
> POST mein `id` nahi bhej sakte (resource abhi exist nahi karta, server assign karega). PUT mein `id` pehle se pata hai (URL path mein hi hai) — isliye same `Post` model (jisme `id` hai) request ke liye bhi valid hai, alag model banane ki zaroorat nahi.

---

## Gotchas — Real Mistakes Jo Is Session Mein Hue

**1. Success branch mein `tvStatus` update karna bhool jaana**
```kotlin
// GALAT — sirf result set kiya, status "Updating" hi reh gaya success ke baad bhi
is GetApiResource.Success -> {
    binding.tvResultId.text = response.data.id.toString()
    binding.tvResultTitle.text = response.data.title
}

// SAHI — status bhi update karo
is GetApiResource.Success -> {
    binding.tvStatus.text = "✅ Updated"
    binding.tvResultId.text = response.data.id.toString()
    binding.tvResultTitle.text = response.data.title
}
```

**2. `handleBackPress()` likhna bhool jaana**
> Function hi missing tha (na banaya, na `onCreate()` mein call kiya) — Toolbar pe back arrow nahi dikhta tha. Har naye Activity mein yeh checklist item yaad rakhna: ViewBinding setup → `setEdgeToEdge()` → `handleBackPress()` → `bindUi()`.

**3. `UnknownHostException` (yeh code ka bug nahi tha)**
> Testing ke waqt emulator ka internet/DNS kaam nahi kar raha tha — `catch (e: Exception)` ne isse gracefully handle kiya, `tvStatus` mein error dikha diya, app crash nahi hui. Yeh confirm karta hai error handling real-world network failures ke liye bhi kaam karta hai, sirf hypothetical nahi.

---

## Interview Mein Bolna

> *"PUT request existing resource ko poora replace karta hai — @Path se identify karte hain kaunsa resource, @Body mein poora naya data bhejte hain (saare fields, chahe kuch change na hui ho). Yeh POST se alag hai jahan sirf naya resource banta hai bina kisi id ke. PUT idempotent hota hai — same request baar-baar bhejne se result same rehta hai, POST nahi. Request aur response model yahan same rakh sakte hain kyunki id pehle se pata hota hai (path mein), POST mein alag rakhna padta hai kyunki id server assign karta hai."*
