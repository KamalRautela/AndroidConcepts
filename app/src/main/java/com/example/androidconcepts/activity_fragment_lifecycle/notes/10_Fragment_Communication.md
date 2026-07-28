# Fragment Communication

## Kya Hai
Fragments directly ek doosre se communicate nahi karte — Activity ya shared mechanism use karte hain. Teen modern patterns hain: Interface (Fragment → Activity), Fragment Result API (Fragment → Fragment, one-time), Shared ViewModel (Fragment ↔ Fragment, reactive).

---

## Kaise Kaam Karta Hai

```
Fragment → Activity:   Interface
Fragment → Fragment:   Fragment Result API  (one-time event)
Fragment ↔ Fragment:   Shared ViewModel    (continuous/reactive)
```

---

## Important Methods / Code

### 1. Fragment → Activity — Interface

```kotlin
// Fragment mein interface define karo
class DemoFragment : Fragment() {

    interface FragmentLogger {
        fun onFragmentLog(message: String)
    }

    private var logger: FragmentLogger? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logger = context as? FragmentLogger  // safe cast
    }

    override fun onDetach() {
        super.onDetach()
        logger = null
    }
}

// Activity implement kare
class MainActivity : AppCompatActivity(), DemoFragment.FragmentLogger {
    override fun onFragmentLog(message: String) {
        // message receive karo
    }
}
```

### 2. Fragment Result API — One-time Event

```kotlin
// Sender Fragment (Fragment A)
val bundle = Bundle().apply {
    putString("message", "Hello from A!")
}
parentFragmentManager.setFragmentResult("comm_key", bundle)

// Receiver Fragment (Fragment B) — onViewCreated mein
parentFragmentManager.setFragmentResultListener("comm_key", viewLifecycleOwner) { _, bundle ->
    val message = bundle.getString("message")
}
```

### 3. Shared ViewModel — Reactive

**StateFlow ke saath (Modern — Recommended)**

```kotlin
// ViewModel
class SharedViewModel : ViewModel() {
    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message.asStateFlow()

    fun sendMessage(text: String) {
        _message.value = text
    }
}

// Fragment A — Writer
class FragmentA : Fragment() {
    private val viewModel: SharedViewModel by activityViewModels()

    // button click pe
    viewModel.sendMessage("Hello!")
}

// Fragment B — Observer
class FragmentB : Fragment() {
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(...) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.message.collect { msg ->
                binding.tvReceived.text = msg
            }
        }
    }
}
```

**LiveData ke saath (Old — Still Works)**

```kotlin
// ViewModel
class SharedViewModel : ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun sendMessage(text: String) {
        _message.value = text
    }
}

// Fragment B — Observer
class FragmentB : Fragment() {
    private val viewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(...) {
        viewModel.message.observe(viewLifecycleOwner) { msg ->
            binding.tvReceived.text = msg
        }
    }
}
```

**LiveData vs StateFlow**

| | LiveData | StateFlow |
|---|---|---|
| Lifecycle aware | Built-in | Manual (`viewLifecycleOwner`) |
| Initial value | Optional | Required (`""`) |
| Android dependency | Yes (AndroidX) | No (pure Kotlin) |
| Modern preference | Old way | New way (2021+) |

> **Interview mein bolna:** "LiveData Android-specific hai, sirf UI layer ke liye. StateFlow pure Kotlin hai — ViewModel mein bhi use ho sakta hai bina Android dependency ke. Naye projects mein StateFlow prefer karte hain."

---

## Kab Kya Use Karein

| Pattern | Kab Use Karein |
|---|---|
| Interface | Fragment → Activity ko kuch batana ho |
| Fragment Result API | Dialog ya Fragment se ek baar result bhejno |
| Shared ViewModel | Continuous data — dono fragments live update chahte hain |

---

## Common Interview Questions

**Q: Fragment directly doosre Fragment ko call kyun nahi karta?**
> Direct call karne se tight coupling hoti hai — Fragment B ka reference Fragment A ke paas hoga. Agar B replace ho jaaye toh crash. Interface ya ViewModel se loose coupling rehti hai.

**Q: `activityViewModels()` aur `viewModels()` mein fark?**
> `viewModels()` — Fragment ka apna ViewModel, sirf us Fragment ke liye. `activityViewModels()` — Activity scope ka ViewModel, saare Fragments share karte hain. Shared data ke liye `activityViewModels()` use karo.

**Q: Fragment Result API mein listener `onViewCreated()` mein kyun?**
> `viewLifecycleOwner` use karte hain — jab View destroy ho toh listener automatically remove ho jaata hai. Memory leak nahi hota.

**Q: Result API aur Shared ViewModel mein kab kya?**
> Result API one-time event ke liye — jaise dialog ka result. ViewModel continuous/reactive data ke liye — jaise search query jo dono fragments use karte hain.

---

## Gotchas — Common Mistakes

**1. Interface mein hard cast karna**
```kotlin
// GALAT — crash agar Activity implement na kare
logger = context as FragmentLogger

// SAHI — safe cast, null return karega
logger = context as? FragmentLogger
```

**2. Fragment Result listener onResume mein lagana**
```kotlin
// GALAT — multiple baar register ho sakta hai
override fun onResume() {
    parentFragmentManager.setFragmentResultListener(...)
}

// SAHI — onViewCreated mein, viewLifecycleOwner ke saath
override fun onViewCreated(...) {
    parentFragmentManager.setFragmentResultListener("key", viewLifecycleOwner) { ... }
}
```

**3. Shared ViewModel mein viewModels() use karna**
```kotlin
// GALAT — har Fragment ka alag instance banega, data share nahi hoga
private val viewModel: SharedViewModel by viewModels()

// SAHI — Activity scope se same instance milega
private val viewModel: SharedViewModel by activityViewModels()
```
