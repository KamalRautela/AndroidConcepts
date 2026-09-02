# Retrofit DELETE — Notes

Resource remove karna — CRUD ka **aakhri operation**. Same MVVM + Repository + sealed Resource pattern, is baar `GetApiResource<Unit>` (koi real data nahi).

---

## Kya Hai

DELETE request se **existing resource ko remove** karte hain. Sabse simple CRUD operation — sirf **kaunsa** resource batana hai (`@Path`), kuch naya data bhejne ki zaroorat nahi.

## Poora CRUD — Ek Nazar Mein

| Method | Kaam | `@Path` | `@Body` |
|---|---|---|---|
| GET | Data fetch | ✅ (ya `@Query`) | ❌ |
| POST | Naya create | ❌ | ✅ |
| PUT | Poora replace | ✅ | ✅ |
| **DELETE** | Resource remove | ✅ | ❌ |

---

## Architecture

```
View (RetrofitDeleteApiActivity) → ViewModel (DeleteApiViewModel) → Repository (DeleteApiRepository) → RetrofitInstance.apiService
```

---

## Code — Poori Chain

### 1. `ApiService.kt` — naya function
```kotlin
@DELETE("posts/{id}")
suspend fun deletePost(@Path("id") id: Int)
```
- Sirf `@Path("id")` — koi `@Body` nahi
- **Koi return type nahi** (implicitly `Unit`) — delete hone ke baad server se koi meaningful data chahiye hi nahi

### 2. `DeleteApiRepository.kt`
```kotlin
class DeleteApiRepository(
    private val apiService: ApiService = RetrofitInstance.apiService
) {
    suspend fun deletePost(id: Int) {
        apiService.deletePost(id)
    }
}
```

### 3. `DeleteApiViewModel.kt`
```kotlin
class DeleteApiViewModel(
    private val repository: DeleteApiRepository = DeleteApiRepository()
) : ViewModel() {
    private val _deletePostData = MutableLiveData<GetApiResource<Unit>>()
    val deletePostData: LiveData<GetApiResource<Unit>> = _deletePostData

    fun deletePost(id: Int) {
        _deletePostData.value = GetApiResource.Loading
        viewModelScope.launch {
            try {
                repository.deletePost(id)
                _deletePostData.value = GetApiResource.Success(Unit)
            } catch (e: Exception) {
                _deletePostData.value = GetApiResource.Error(e.message ?: "Unknown Error")
            }
        }
    }
}
```

### 4. `RetrofitDeleteApiActivity.kt` — View
```kotlin
private val viewModel: DeleteApiViewModel by viewModels()

private fun bindUi() {
    binding.btnDelete.setOnClickListener {
        val id = binding.etId.text.toString().toIntOrNull() ?: 0
        viewModel.deletePost(id)
    }

    viewModel.deletePostData.observe(this) { response ->
        when (response) {
            is GetApiResource.Loading -> binding.tvStatus.text = "Deleting..."
            is GetApiResource.Success -> binding.tvStatus.text = "✅ Post deleted successfully"
            is GetApiResource.Error -> binding.tvStatus.text = "❌ ${response.message}"
        }
    }
}
```

---

## Key Concepts

| Concept | Detail |
|---|---|
| Sirf `@Path`, `@Body` nahi | Delete karne ke liye koi naya data nahi bhejna — sirf batana hai "kaunsa" resource |
| `suspend fun deletePost(id: Int)` — no return type | Function `Unit` return karta hai (implicitly) — meaningful response data ki zaroorat nahi |
| `GetApiResource<Unit>` | Sealed class Resource ko **bina real data ke** bhi use kar sakte hain — `Unit` Kotlin ka "no value" type hai (Java ke `void` jaisa, lekin ek actual singleton value hai) |
| `GetApiResource.Success(Unit)` | Success ka signal deta hai, andar koi useful data nahi — bas "operation complete hua" |
| Idempotent | Same resource ko 2 baar delete karo — end state same (resource gone). Dusri baar 404 aa sakta hai kyunki already exist nahi karta, lekin final result same hai. |

---

## Common Interview Questions

**Q: DELETE mein `@Body` kyun nahi use karte?**
> Delete karne ke liye koi naya data bhejne ki zaroorat nahi — bas resource identify karna hai (`@Path`). Body sirf tab chahiye jab server ko kuch naya data dena ho (POST/PUT ki tarah).

**Q: Function ka return type kyun nahi likha (`Unit` implicit chhoda)?**
> Delete operation ka result sirf "success ya fail" hota hai — koi meaningful data wapas nahi aata jo use karna ho. Kotlin mein agar return type nahi likho, woh automatically `Unit` hota hai.

**Q: `GetApiResource<Unit>` mein `Unit` kya role play karta hai?**
> `Success<T>` ko generic banaya tha taaki koi bhi type ka data carry kar sake. Jab koi real data hi nahi hai, `Unit` (Kotlin ka "single, meaningless value" type) use karte hain — sirf yeh signal dene ke liye ki "Success hua hai", bina kisi payload ke.

**Q: DELETE aur GET dono mein body nahi hoti — inmein fark kya hai?**
> GET **read-only** hai — server state change nahi hoti, sirf data fetch hota hai. DELETE server ki state **modify** karta hai (resource remove karta hai) — matlab dono "body-less" hain lekin unka effect (side-effect) alag hai.

---

## Interview Mein Bolna

> *"DELETE request resource remove karta hai — sirf @Path se resource identify karte hain, body ki zaroorat nahi kyunki koi naya data nahi bhejna. Jab response mein koi meaningful data nahi milta, function ka return type Unit rakhte hain, aur Resource wrapper mein GetApiResource.Success(Unit) se sirf 'operation complete hua' signal dete hain. Poora CRUD (GET/POST/PUT/DELETE) same MVVM + Repository + sealed Resource architecture follow karta hai — sirf annotations aur request/response shape alag hote hain har method ke liye."*

---

## Poore CRUD Ka Summary — Interview Ready

| | GET | POST | PUT | DELETE |
|---|---|---|---|---|
| Kaam | Fetch | Create | Poora Update | Remove |
| Idempotent? | ✅ | ❌ | ✅ | ✅ |
| `@Path` | ✅ (single item) | ❌ | ✅ | ✅ |
| `@Query` | ✅ (list/filter) | ❌ | ❌ | ❌ |
| `@Body` | ❌ | ✅ | ✅ | ❌ |
| Response mein `id` | Already hai | Server assign karta hai | Already hai (path mein) | N/A |
