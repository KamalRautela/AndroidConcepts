# MVVM + Jetpack — Consolidated Notes

Saare MVVM sub-topics ek jagah — ViewModel, LiveData, StateFlow, MVVM Pattern, Shared ViewModel. Har section: Kya Hai → Code (jo actually project mein bana hai) → Interview Q&A → Gotchas.

---

## 1. ViewModel Basics

### Kya Hai
`ViewModel` ek Jetpack class hai jo UI-related data ko **screen rotation / configuration change** ke through survive karati hai. Activity destroy-recreate hoti hai, lekin ViewModel alive rehta hai jab tak Activity/Fragment **finish** na ho (back press ya `finish()`).

### Code (BasicViewModel — Counter Demo)
```kotlin
class BasicViewModel : ViewModel() {
    private val _counter = MutableLiveData(0)
    val counter: LiveData<Int> = _counter

    fun incrementCounter() { _counter.value = (_counter.value ?: 0) + 1 }
    fun decrementCounter() { _counter.value = (_counter.value ?: 0) - 1 }
    fun resetCounter() { _counter.value = 0 }
}

// Activity
class BasicViewModelActivity : AppCompatActivity() {
    private val viewModel: BasicViewModel by viewModels()
}
```
- `by viewModels()` — Activity/Fragment ke apne scope ka ViewModel deta hai
- Rotation pe Activity naya banega, but `viewModels()` wahi purana instance return karega

### Kab Kya Use Karein
| Delegate | Scope |
|---|---|
| `by viewModels()` | Sirf current Activity/Fragment |
| `by activityViewModels()` | Poori Activity — saare Fragments share karte hain (Section 6 dekho) |

### Common Interview Questions
**Q: ViewModel rotation pe survive kaise karta hai?**
> ViewModel Activity ke lifecycle se bound nahi, `ViewModelStore` se bound hai jo configuration change ke through preserve hota hai. Activity destroy-recreate hoti hai, ViewModelStore nahi.

**Q: ViewModel mein Context/View reference kyun nahi rakhna chahiye?**
> Memory leak hota hai — ViewModel Activity se zyada time tak alive reh sakta hai (rotation ke dauraan), agar Activity ka reference hold kiya toh garbage collect nahi hoga.

### Gotchas
```kotlin
// GALAT — ViewModel mein Context store karna
class BadViewModel(val context: Context) : ViewModel()

// SAHI — agar Context chahiye hi ho toh AndroidViewModel use karo (Application context safe hai)
class GoodViewModel(app: Application) : AndroidViewModel(app)
```

---

## 2. viewModelScope

### Kya Hai
`viewModelScope` — ViewModel ke saath bound ek `CoroutineScope`. ViewModel `onCleared()` hone pe (back press / Activity finish) is scope ke saare coroutines **automatically cancel** ho jaate hain — manually cancel karne ki zaroorat nahi.

### Code (ViewModelScopeViewModel — Timer Demo)
```kotlin
class ViewModelScopeViewModel : ViewModel() {
    private val _seconds = MutableLiveData(0)
    val seconds: LiveData<Int> = _seconds
    private var job: Job? = null

    fun start() {
        job = viewModelScope.launch {
            while (true) {
                delay(1000)
                _seconds.value = (_seconds.value ?: 0) + 1
            }
        }
    }
    fun stop() { job?.cancel() }
    fun reset() { job?.cancel(); _seconds.value = 0 }
}
```

### Common Interview Questions
**Q: viewModelScope use karne ka fayda?**
> Coroutine automatically ViewModel ke lifecycle se tied ho jaata hai. Manual cancel nahi karna padta, memory leak ka risk nahi.

**Q: `job?.cancel()` explicitly kyun likha hai agar viewModelScope auto-cancel karta hai?**
> Auto-cancel sirf ViewModel destroy hone pe hota hai. Yahan "Stop" button se **user manually** timer rokna chahta hai bina ViewModel destroy kiye — isliye `Job` reference rakh ke khud cancel karna padta hai.

---

## 3. LiveData

### Kya Hai
`LiveData` — **lifecycle-aware** observable data holder. Sirf tab emit karta hai jab observer active ho (STARTED/RESUMED state mein). Background mein update nahi hota, memory leak nahi hota.

### Code (LiveDataViewModel — Celsius → Fahrenheit)
```kotlin
class LiveDataViewModel : ViewModel() {
    private val _celsius = MutableLiveData("")
    val celsius: LiveData<String> = _celsius

    val fahrenheit: LiveData<String> = _celsius.map {
        val c = it.toDoubleOrNull()
        if (c != null) "${(c * 9.0/5.0) + 32}" else ""
    }

    fun setCelsius(value: String) { _celsius.value = value }
}

// Activity
viewModel.celsius.observe(this) { binding.tvCelsius.text = it }
viewModel.fahrenheit.observe(this) { binding.tvFahrenheit.text = it }
```
> Note: `_celsius.map { }` — modern **KTX extension function** hai (`androidx.lifecycle.map`). Purana tarika `Transformations.map(_celsius) { }` tha — dono kaam karte hain, lekin KTX extension zyada idiomatic Kotlin hai.

### Common Interview Questions
**Q: LiveData lifecycle-aware kaise hai?**
> `observe(lifecycleOwner, observer)` — LiveData khud Activity/Fragment ka lifecycle track karta hai. STARTED/RESUMED mein hi data deta hai, STOPPED mein automatically rukk jaata hai, DESTROYED pe observer khud remove ho jaata hai.

**Q: Fragment mein `observe(this)` kyun nahi, `viewLifecycleOwner` kyun?**
> Fragment ka apna lifecycle View se lamba hota hai (Fragment backstack mein reh sakta hai jab View destroy ho chuki ho). `viewLifecycleOwner` use karne se View destroy hote hi observer remove ho jaata hai — warna memory leak / crash ho sakta hai.

---

## 4. StateFlow

### Kya Hai
`StateFlow` — Kotlin Coroutines ka **pure Kotlin** observable state holder (Android dependency nahi). Hamesha ek current value hold karta hai, naya collector join karte hi latest value milti hai.

### Code (StateFlowViewModel — Username Validation)
```kotlin
class StateFlowViewModel : ViewModel() {
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    val isValid: StateFlow<Boolean> = _userName
        .map { it.length in 3..20 && it.matches(Regex("[a-zA-Z0-9_]+")) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun setUserName(value: String) { _userName.value = value.trim() }
}

// Activity — collect karne ke liye repeatOnLifecycle chahiye (LiveData jaisa built-in lifecycle-aware nahi)
lifecycleScope.launch {
    repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.userName.collect { binding.tvCharCount.text = it.length.toString() }
    }
}
```

### LiveData vs StateFlow — Comparison
| Feature | LiveData | StateFlow |
|---|---|---|
| Initial Value | Optional (null ho sakta hai) | Required — hamesha value hoti hai |
| Lifecycle Aware | Built-in | Manual — `repeatOnLifecycle` chahiye |
| Android Dependency | Yes (AndroidX) | No — pure Kotlin |
| ViewModel mein | Theek hai | Better — no Android dependency, testable |
| Collect karna | `observe(owner) { }` | `repeatOnLifecycle + collect { }` |

### Common Interview Questions
**Q: StateFlow ViewModel ke liye better kyun bola jaata hai?**
> Pure Kotlin hai, Android framework pe depend nahi karta — isliye plain JUnit test mein bhi easily test ho sakta hai bina Android dependency mock kiye.

**Q: `stateIn(viewModelScope, SharingStarted.Eagerly, false)` — yeh teeno params kya karte hain?**
> `viewModelScope` — konsa scope collect kare. `SharingStarted.Eagerly` — turant collection start karo (collector wait nahi karna). `false` — initial/default value jab tak pehla real value na aaye.

---

## 5. MVVM Pattern — Data Flow (Profile Card Demo)

### Kya Hai
Real MVVM flow — ek **Model** class (pure data), **ViewModel** (business logic + state), **View** (sirf render + user actions forward). Isse UI aur logic decouple ho jaate hain.

### Layers Ka Role
| Layer | File | Role |
|---|---|---|
| **Model** | `User.kt` | Plain `data class` — koi Android/UI import nahi, sirf data |
| **ViewModel** | `ProfileViewModel.kt` | Model manage karta hai, `LiveData<User>` expose karta hai |
| **View** | `MvvmPatternsActivity.kt` | Sirf observe + render, business logic nahi rakhta |

### Code
```kotlin
// User.kt — Model
data class User(
    val id: Int,
    val name: String,
    val age: Int,
    val email: String
)

// ProfileViewModel.kt — ViewModel
class ProfileViewModel : ViewModel() {
    private val _userLiveData = MutableLiveData<User>()
    val userLiveData: LiveData<User> = _userLiveData

    fun loadUser() {
        viewModelScope.launch {
            delay(1000) // fake network/db delay
            _userLiveData.value = User(1, "Kamal", 25, "kamalrautela@gmail.com")
        }
    }

    fun incrementAge() {
        _userLiveData.value = _userLiveData.value?.copy(age = _userLiveData.value!!.age + 1)
    }
}

// MvvmPatternsActivity.kt — View
private fun bindUi() {
    binding.tvName.text = "-"; binding.tvEmail.text = "-"; binding.tvAge.text = "0"

    binding.btnLoad.setOnClickListener { viewModel.loadUser() }

    binding.btnIncreaseAge.setOnClickListener {
        if (binding.tvAge.text != "0") viewModel.incrementAge()
    }

    viewModel.userLiveData.observe(this) {
        binding.tvName.text = it.name
        binding.tvAge.text = it.age.toString()
        binding.tvEmail.text = it.email
    }
}
```
- `incrementAge()` mein `.copy()` — data class immutable rehti hai, naya object banta hai purane ke upar based
- Guard `if (binding.tvAge.text != "0")` — user load hone se pehle age increment na ho, isliye check

### Common Interview Questions
**Q: Model ko Android/UI se independent kyun rakhte hain?**
> Testability ke liye — Model aur ViewModel dono plain Kotlin/JUnit test se test ho sakte hain, emulator ya Android framework ki zaroorat nahi.

**Q: MVVM mein "unidirectional data flow" ka matlab?**
> View sirf action ViewModel ko bhejta hai (button click), aur ViewModel se sirf data receive karta hai (observe). Data hamesha ek direction mein flow karta hai — View kabhi Model ko directly modify nahi karta.

---

## 6. Shared ViewModel

### Kya Hai
`activityViewModels()` — Activity scope ka ViewModel deta hai, jisse **saare Fragments same instance share** karte hain. Iske against `viewModels()` har Fragment ko apna alag instance deta hai.

> Yeh concept `fragment_communication` package mein already demonstrate ho chuka hai (Android Basics ke waqt bana tha) — dobara alag se banane ki zaroorat nahi padi.

### Code
```kotlin
// SharedViewModel.kt
class SharedViewModel : ViewModel() {
    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    fun sendMessage(text: String) { _message.value = text }
}

// SenderFragment — Writer
class SenderFragment : Fragment() {
    private val viewModel: SharedViewModel by activityViewModels()

    // button click pe
    viewModel.sendMessage(msg)
}

// ReceiverFragment — Observer
class ReceiverFragment : Fragment() {
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(...) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.message.collect { msg -> binding.tvViewModelMsg.text = msg }
        }
    }
}
```

### Kab Kya Use Karein
| Pattern | Kab |
|---|---|
| Interface (Fragment→Activity) | Fragment Activity ko kuch batana chahta hai |
| Fragment Result API | One-time event (jaise Dialog ka result) |
| Shared ViewModel | Continuous/reactive data — dono Fragments live update chahte hain |

### Common Interview Questions
**Q: `viewModels()` aur `activityViewModels()` mein fark?**
> `viewModels()` — Fragment ka apna alag instance, doosre Fragment ko pata nahi chalega. `activityViewModels()` — Activity scope ka same instance, saare Fragments share karte hain.

### Gotchas
```kotlin
// GALAT — Shared ViewModel ke liye viewModels() use karna
private val viewModel: SharedViewModel by viewModels()  // har Fragment ka alag instance, data share nahi hoga

// SAHI
private val viewModel: SharedViewModel by activityViewModels()
```

---

## 7. Repository Pattern — Deferred

Repository Pattern (`ViewModel → Repository → DataSource`) is section mein cover **nahi** kiya — kyunki real matlab tabhi banta hai jab Repository **do real sources combine kare** (Retrofit network + Room local cache).

**Decision:** Repository Pattern ab **Retrofit + Room section** mein implement hoga, jahan woh:
- Network call (Retrofit) + local DB (Room) dono ko abstract karega
- "Single source of truth" ka real example banega
- Testing ke liye fake Repository inject karne ka use-case bhi natural banega

---

## Interview Mein Bolna — MVVM Summary

> *"MVVM mein Model plain data hai, koi Android dependency nahi. ViewModel business logic rakhta hai aur LiveData/StateFlow ke through state expose karta hai — Activity/Fragment ke lifecycle se independent, rotation survive karta hai. View sirf observe karta hai aur user actions ViewModel ko forward karta hai. Isse UI aur logic decouple ho jaate hain, testing aasan hoti hai bina Android framework ke. Shared state ke liye activityViewModels() use karte hain jisse multiple Fragments same ViewModel instance share kar saken."*
