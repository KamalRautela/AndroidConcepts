# SharedPreferences — Notes

Simple key-value local storage — settings, login state, chhote flags. MVVM + Repository pattern, jaisa Room/Retrofit mein tha.

---

## Kya Hai

**SharedPreferences** — Android ka built-in **key-value storage**, XML file mein device pe store hota hai. Chhote data ke liye (bade/structured data ke liye Room use karte hain).

```kotlin
val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
prefs.edit().putString("username", "Kamal").apply()
val username = prefs.getString("username", "")
```

---

## Architecture

```
View (SharedPreferenceActivity) → ViewModel (SharedPrefsViewModel — AndroidViewModel) → Repository (SharedPrefsRepository) → SharedPreferences
```

> **`AndroidViewModel` kyun, `ViewModel` nahi?** — `getSharedPreferences()` ko Context chahiye (Room jaisa hi reason). `AndroidViewModel(application)` automatically Application object deta hai.

> **Koi `suspend fun` kyun nahi?** — SharedPreferences read/write itna fast hai (chhoti file, in-memory cache) ki coroutine mein wrap karne ki zaroorat nahi maani jaati — Room (disk DB) aur Retrofit (network) dono **dheeme** the isliye suspend zaroori tha.

---

## Code — Poori Chain

### 1. `SharedPrefsRepository.kt`
```kotlin
class SharedPrefsRepository(
    private val prefs: SharedPreferences
) {
    fun saveUsername(username: String, isLoggedIn: Boolean) {
        prefs.edit {
            putString("username", username)
                .putBoolean("isLoggedIn", isLoggedIn)
        }
    }

    fun getUsername(): String = prefs.getString("username", "") ?: ""
    fun isLoggedIn(): Boolean = prefs.getBoolean("isLoggedIn", false)

    fun clear() {
        prefs.edit { clear() }
    }
}
```
> **`prefs.edit { }`** — `androidx.core.content.edit` KTX extension. `.edit().putString(...).apply()` likhne ki jagah, block ke andar `put...` calls likho — end mein **automatically `.apply()`** ho jaata hai.

### 2. `SharedPrefsViewModel.kt`
```kotlin
class SharedPrefsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SharedPrefsRepository

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    init {
        val prefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        repository = SharedPrefsRepository(prefs)
        loadData()
    }

    private fun loadData() {
        _username.value = repository.getUsername()
        _isLoggedIn.value = repository.isLoggedIn()
    }

    fun save(username: String, isLoggedIn: Boolean) {
        repository.saveUsername(username, isLoggedIn)
        loadData()
    }

    fun clear() {
        repository.clear()
        loadData()
    }
}
```

### 3. `SharedPreferenceActivity.kt` — View
```kotlin
private val viewModel: SharedPrefsViewModel by viewModels()

private fun bindUi() {
    binding.btnSave.setOnClickListener {
        val username = binding.etUsername.text.toString()
        viewModel.save(username, binding.cbRememberMe.isChecked)
    }

    binding.btnClear.setOnClickListener {
        viewModel.clear()
        binding.etUsername.text?.clear()
    }

    viewModel.username.observe(this) {
        binding.tvSavedUsername.text = it
    }

    viewModel.isLoggedIn.observe(this) {
        binding.tvIsLoggedIn.text = it.toString()
        binding.cbRememberMe.isChecked = it
    }
}
```

---

## `apply()` vs `commit()`

| | `apply()` | `commit()` |
|---|---|---|
| Kaam | Async (background) | Sync (blocking) |
| Return | Kuch nahi (`Unit`) | `Boolean` (success/fail) |
| Kab use karein | **Almost hamesha** | Sirf jab turant confirm karna ho save hua ki nahi |

---

## 3 Real-World Patterns — Dkscore Production App Se Comparison

`D:\Dkscore\DkscoreAstroApp` (real production app) mein SharedPreferences ke **do generations** dikhe:

### Pattern A — Static Utility Class (Legacy, `MyPrefrences.java`)
```java
public class MyPrefrences {
    static SharedPreferences prefs = App.prefs;
    public static void setAccessToken(String value) {
        prefs.edit().putString(KEY.accessToken, value).apply();
    }
    public static String getAccessToken() {
        return prefs.getString(KEY.accessToken, "");
    }
}
```
- Static methods — kahin se bhi call karo, DI ki zaroorat nahi
- **Problem:** Testing mushkil hai (static methods mock nahi ho sakte), Java-era pattern

### Pattern B — Hilt-Injected Class (Modern, `AppCache.kt`)
```kotlin
class AppCache @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPref: SharedPreferences = try {
        val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        EncryptedSharedPreferences.create(context, PREF_NAME, masterKey, ...)
    } catch (e: Exception) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)  // fallback
    }
    ...
}
```

**Naye concepts jo humare demo mein nahi the:**
1. **`EncryptedSharedPreferences`** — data **disk pe encrypted** store hota hai (AES256), sensitive data (tokens, personal info) ke liye. Fail ho jaaye toh plain SharedPreferences pe **fallback**.
2. **Complex objects → Gson JSON** — poore objects/lists ko `Gson().toJson()` se String banake store karte hain, `TypeToken` se wapas parse karte hain (List ke liye — yehi type erasure wala concept jo humne JSON topic mein seekha tha!):
   ```kotlin
   fun saveTodayHoraInSharedPref(horas: List<HoraUiState>) {
       val json = Gson().toJson(horas)
       sharedPref.edit().putString(KEY_TODAY_HORA, json).apply()
   }
   fun getTodayHoraFromSharedPref(): MutableList<HoraUiState> {
       val json = sharedPref.getString(KEY_TODAY_HORA, null)
       val type = object : TypeToken<List<HoraUiState>>() {}.type
       return Gson().fromJson<List<HoraUiState>>(json, type).toMutableList()
   }
   ```
3. **`updateXXX(update: (T) -> T)` pattern** — `.copy()` wala concept (jo humne Room mein seekha) real use mein:
   ```kotlin
   fun updateCalculationSettingsInSharedPref(update: (CalculationSettingUiState) -> CalculationSettingUiState) {
       val current = getUserCalculationSettingsFromSharedPref()
       current?.let { saveCalculationSettingsInSharedPref(update(it)) }
   }
   ```
4. **Selective clear** — logout pe **sab kuch delete nahi karta**, kuch keys (language, location, "intro seen" flag) backup karke wapas restore karta hai — poora `clear()` se pehle important values save karke, `clear()` ke baad wapas likh deta hai.

### Pattern C — Jo Humne Banaya (Manual DI, Repository + AndroidViewModel)
Poore course (Retrofit, Room) ke pattern se **consistent**. Jab Hilt topic aayega, isko convert karna aasan hoga — bas `SharedPrefsRepository(prefs)` ki jagah `@Inject constructor` aa jaayega, poora architecture wahi rahega.

---

## Common Interview Questions

**Q: SharedPreferences kab use karte hain, Room kab?**
> SharedPreferences — chhote key-value data (settings, flags, tokens). Room — bada/structured/relational data (lists, complex objects with relationships) jisme querying bhi chahiye.

**Q: Sensitive data (jaise auth token) SharedPreferences mein plain text mein store karna safe hai?**
> Nahi — production apps `EncryptedSharedPreferences` (androidx.security.crypto) use karti hain, jo AES256 encryption ke saath data disk pe store karta hai. Agar encryption setup fail ho (rare), fallback plain SharedPreferences pe ho sakta hai.

**Q: Static utility class (jaise `MyPrefrences.java`) approach ki problem kya hai?**
> Testability — static methods ko mock/stub nahi kar sakte unit tests mein aasani se. Instance-based class (constructor injection ke saath, Hilt ya manual) testable hoti hai — fake `SharedPreferences` ya fake Repository inject kar sakte ho.

---

## Interview Mein Bolna

> *"SharedPreferences chhote key-value data ke liye hai — XML file mein store hota hai. apply() async hai isliye almost hamesha use karte hain, commit() sync/blocking hai. MVVM ke saath AndroidViewModel use karte hain kyunki Context chahiye SharedPreferences banane ke liye. Production apps mein sensitive data ke liye EncryptedSharedPreferences use karte hain, aur complex objects ko Gson se JSON string banake store karte hain. Static utility class approach purana pattern hai — testability ke liye instance-based, injectable class better hai."*
