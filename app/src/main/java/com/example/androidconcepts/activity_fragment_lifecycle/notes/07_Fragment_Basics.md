# Fragment Basics

## Kya Hai
Fragment Activity ka ek reusable UI portion hota hai. Ek Activity mein multiple Fragments ho sakte hain. Fragment ka apna lifecycle hota hai lekin Activity ke lifecycle se tied hota hai — Activity destroy ho toh Fragment bhi destroy.

---

## Kaise Kaam Karta Hai

### Lifecycle — Full Sequence

```
onAttach()        ← Activity se connect
onCreate()        ← Fragment initialize
onCreateView()    ← Layout inflate karo, View return karo
onViewCreated()   ← View ready, setup karo yahan
onStart()
onResume()
    ← [Fragment visible + interactive]
onPause()
onStop()
onDestroyView()   ← View destroy — _binding = null karo
onDestroy()
onDetach()        ← Activity se disconnect
```

### Activity vs Fragment Lifecycle

```
Activity: onCreate → onStart → onResume → onPause → onStop → onDestroy
Fragment:  onAttach → onCreate → onCreateView → onViewCreated
                   → onStart → onResume → onPause → onStop
                   → onDestroyView → onDestroy → onDetach
```

### Back Stack mein hone pe

```
Fragment add karo (addToBackStack)
      ↓
User back press kare
      ↓
onPause → onStop → onDestroyView   ← View destroy hoti hai
      ↓
Fragment object alive rehta hai!    ← Fragment destroy nahi hota
      ↓
Wapas aao
      ↓
onCreateView → onViewCreated → onStart → onResume
```

> **Ye isliye important hai** — Fragment object alive tha lekin View destroy ho gayi thi. Agar `_binding` null nahi kiya toh destroyed View memory mein hold rehti hai = **Memory Leak**.

---

## Important Methods / Code

### ViewBinding with Memory Leak Prevention

```kotlin
class DemoFragment : Fragment() {

    private var _binding: FragmentDemoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDemoBinding.inflate(inflater, container, false)
        return binding.root   // sirf inflate karo, setup mat karo
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // yahan setup karo — click listeners, observers
        binding.btnSubmit.setOnClickListener { }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null    // memory leak rokne ke liye
    }
}
```

### Fragment add karo — Activity mein

```kotlin
supportFragmentManager.commit {
    add(R.id.fragmentContainer, DemoFragment())
    addToBackStack(null)
}
```

### Fragment ko Static XML se add karo

```xml
<androidx.fragment.app.FragmentContainerView
    android:id="@+id/fragmentContainer"
    android:name="com.example.DemoFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

---

## Common Interview Questions

**Q: Fragment aur Activity mein fark?**
> Activity ek full screen hoti hai — OS directly manage karta hai. Fragment Activity ka ek portion hai — reusable, multiple fragments ek Activity mein ho sakte hain. Fragment independently exist nahi kar sakta — Activity chahiye.

**Q: `onCreateView()` aur `onViewCreated()` mein kya fark hai?**
> `onCreateView()` mein sirf layout inflate karo aur View return karo. `onViewCreated()` mein View ready hoti hai — yahan click listeners, RecyclerView, ViewModel observe karo. `onViewCreated()` mein kaam karna safer hai.

**Q: `_binding = null` `onDestroyView()` mein kyun karte hain?**
> Fragment back stack mein hone pe View destroy hoti hai lekin Fragment object alive rehta hai. Binding null nahi kiya toh destroyed View ka reference hold rehta hai — memory leak hota hai. Activity mein ye problem nahi hoti kyunki Activity aur uski View saath destroy hoti hain.

**Q: `<fragment>` tag vs `FragmentContainerView` mein fark?**
> `FragmentContainerView` prefer karo — `<fragment>` tag deprecated hai. `FragmentContainerView` animations aur transactions properly handle karta hai.

---

## Gotchas — Common Mistakes

**1. onCreateView() mein Views setup karna**
```kotlin
// GALAT — onCreateView mein binding use karna unsafe hai
override fun onCreateView(...): View {
    _binding = FragmentDemoBinding.inflate(inflater, container, false)
    binding.btnSubmit.setOnClickListener { }  // avoid karo
    return binding.root
}

// SAHI — onViewCreated mein karo
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    binding.btnSubmit.setOnClickListener { }
}
```

**2. _binding null check bhoolna**
```kotlin
// GALAT — onDestroyView ke baad crash
override fun onDestroyView() {
    super.onDestroyView()
    // _binding = null bhool gaye
}
// Baad mein koi background callback binding use kare → NullPointerException

// SAHI
override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
}
```

**3. `requireContext()` vs `context`**
```kotlin
// context null ho sakta hai — safe nahi
val ctx = context

// requireContext() — null hoga toh IllegalStateException, crash se better
val ctx = requireContext()
```
