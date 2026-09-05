# DataStore — Notes

SharedPreferences ka modern, Flow-based replacement. Same "Remember Username" feature dobara banaya, taaki fark seedha mehsoos ho.

---

## Kya Hai

**DataStore** — Google ka naya key-value storage solution, jo SharedPreferences ki jagah recommend hota hai. Fully **coroutine/Flow-based** hai. Do types: **Preferences DataStore** (key-value, jo humne use kiya) aur **Proto DataStore** (typed objects, complex data ke liye).

## SharedPreferences Se Fark

| Feature | SharedPreferences | DataStore |
|---|---|---|
| API style | Sync/semi-async (`apply`) | **Fully async** — `suspend`/`Flow` |
| Reactive | Nahi | **Haan** — Flow se automatic updates |
| Type safety | Kam (runtime crash possible) | Zyada (typed keys — `stringPreferencesKey`, `booleanPreferencesKey`) |
| Google recommendation | Legacy | **Modern — yeh use karo** |

---

## Architecture

```
View (DataStoreActivity) → ViewModel (DatastoreViewModel — AndroidViewModel) → Repository (DatastoreRepository) → DataStore<Preferences>
```

---

## Code — Poori Chain

### 1. `DatastoreRepository.kt`
```kotlin
val Context.datastore by preferencesDataStore(name = "user_prefs")

class DatastoreRepository(private val datastore: DataStore<Preferences>) {
    companion object {
        val USERNAME_KEY = stringPreferencesKey("user_name")
        val IS_LOGGED_IN_KEY = booleanPreferencesKey("isLoggedIn")
    }

    val userName: Flow<String> = datastore.data.map { it[USERNAME_KEY] ?: "" }
    val isLoggedIn: Flow<Boolean> = datastore.data.map { it[IS_LOGGED_IN_KEY] ?: false }

    suspend fun saveData(userName: String, isLoggedIn: Boolean) {
        datastore.edit {
            it[USERNAME_KEY] = userName
            it[IS_LOGGED_IN_KEY] = isLoggedIn
        }
    }

    suspend fun clear() {
        datastore.edit { it.clear() }
    }
}
```

### 2. `DatastoreViewModel.kt`
```kotlin
class DatastoreViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DatastoreRepository(application.datastore)

    val userName: LiveData<String> = repository.userName.asLiveData()
    val isLoggedIn: LiveData<Boolean> = repository.isLoggedIn.asLiveData()

    fun saveData(userName: String, isLoggedIn: Boolean) {
        viewModelScope.launch {
            repository.saveData(userName, isLoggedIn)
        }
    }

    fun clear() {
        viewModelScope.launch { repository.clear() }
    }
}
```

### 3. `DataStoreActivity.kt` — View
```kotlin
private val viewModel: DatastoreViewModel by viewModels()

private fun bindUi() {
    binding.btnSave.setOnClickListener {
        val username = binding.etUsername.text.toString()
        viewModel.saveData(username, binding.cbRememberMe.isChecked)
    }

    binding.btnClear.setOnClickListener {
        viewModel.clear()
        binding.etUsername.text?.clear()
    }

    viewModel.userName.observe(this) {
        binding.tvSavedUsername.text = it
    }

    viewModel.isLoggedIn.observe(this) {
        binding.tvIsLoggedIn.text = it.toString()
        binding.cbRememberMe.isChecked = it
    }
}
```

---

## Key Concepts

| Concept | Detail |
|---|---|
| `by preferencesDataStore(name = "...")` | Property delegate, `Context` pe extension property banata hai — top-level likha jaata hai, class ke bahar |
| `stringPreferencesKey("...")` / `booleanPreferencesKey("...")` | Type-safe key — har data type ka apna function |
| `dataStore.edit { }` | `suspend fun` hai (SharedPreferences ke `edit{}` se bada fark — woh sync-ish tha, yeh coroutine maangta hai) |
| `dataStore.data.map { }` | **`Flow`** return karta hai — Room ke `Flow<List<Todo>>` jaisa concept, data change hote hi automatically naya value |

---

## Architecture Cleanup — Repository Ko Context Nahi, `DataStore<Preferences>` Do

**Pehle (kam clean):**
```kotlin
class DatastoreRepository(val context: Context) {
    val userName: Flow<String> = context.datastore.data.map { ... }
}
```

**Better (Room jaisa consistent):**
```kotlin
class DatastoreRepository(private val dataStore: DataStore<Preferences>) {
    val userName: Flow<String> = dataStore.data.map { ... }
}
// ViewModel mein:
private val repository = DatastoreRepository(application.datastore)
```

> **Principle:** Kisi bhi class ko sirf utna hi do jitna uska kaam karne ke liye zaroori hai — poora `Context` (jo camera, notifications, sab kuch access kar sakta hai) nahi, sirf jo **chhota specific tool** chahiye (`DataStore<Preferences>`). Bilkul waisa hi jaise Room mein humne `Context`/`Database` ki jagah sirf `TodoDao` Repository ko diya tha.

---

## Common Interview Questions

**Q: DataStore SharedPreferences se better kyun hai?**
> DataStore fully coroutine/Flow-based hai — kabhi main thread block nahi hoti (SharedPreferences ka `commit()` block kar sakta hai). Reactive hai — data change hote hi automatically naya value emit hota hai bina manual check kiye. Type-safe keys hain — galat type maangne pe compile-time hi pata chal jaata hai.

**Q: Preferences DataStore aur Proto DataStore mein fark?**
> Preferences DataStore — simple key-value (SharedPreferences jaisa), koi schema define nahi karna. Proto DataStore — typed, structured objects (Protocol Buffers se), schema pehle se define karna padta hai — complex/structured data ke liye better hai.

**Q: Repository ko `Context` dena chahiye ya `DataStore<Preferences>` seedha?**
> Best practice — sirf woh diya jaaye jo class ko actually chahiye. `Context` bahut powerful/broad hai; agar sirf DataStore access karna hai, seedha `DataStore<Preferences>` pass karo. Isse Repository ko sirf apne kaam ka access milta hai, extra permissions/capabilities nahi — testability aur safety dono better hoti hai.

---

## Interview Mein Bolna

> *"DataStore SharedPreferences ka modern replacement hai — fully coroutine aur Flow-based. Save/clear suspend functions hain, read Flow return karta hai jo reactive hai — data change hote hi UI automatically update hoti hai. Preferences DataStore key-value ke liye hai, Proto DataStore typed structured objects ke liye. Architecture mein Repository ko poora Context dene ki jagah sirf DataStore<Preferences> object dena better practice hai — 'least privilege' principle, jaisa Room mein Repository ko poora Database na deke sirf DAO diya tha."*
