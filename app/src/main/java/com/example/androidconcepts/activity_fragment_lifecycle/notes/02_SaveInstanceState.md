# onSaveInstanceState + onRestoreInstanceState

## Kyun Chahiye?

Screen rotate hone pe Android Activity **destroy karke recreate** karta hai.
Agar koi data (user ka typed text, scroll position) tha — vo chala jaata hai.
`onSaveInstanceState` se recreate se pehle data save karo.

---

## onSaveInstanceState — Data Save Karo

```kotlin
companion object {
    private const val KEY = "KEY_NAME"
}

override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putString(KEY, "Kamal")  // Bundle mein save
}
```

**Kab call hota hai:** Activity destroy hone se pehle — screen rotation pe
**Kab nahi hota:** Back press pe — intentionally close kar rahe ho

---

## Data Restore Karo — onCreate() mein

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val name = savedInstanceState?.getString(KEY)  // null check zaroori
    // fresh launch pe null hogi
    // rotation ke baad data hoga
}
```

---

## onCreate() vs onRestoreInstanceState()

| | `onCreate()` | `onRestoreInstanceState()` |
|---|---|---|
| Kab call hota hai | Hamesha — fresh + recreate | Sirf recreate pe |
| Bundle | Null ho sakti hai | Guaranteed non-null |
| Kab use karein | 99% cases — yahi use karo | Rarely |

```kotlin
// onRestoreInstanceState — null check nahi chahiye
override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    val name = savedInstanceState.getString(KEY)  // directly
}
```

---

## Interview Mein Bolna
> *"`onSaveInstanceState` screen rotation jaisi configuration change se pehle call hota hai — Bundle mein data save karo. `onCreate()` mein `savedInstanceState?.getString(KEY)` se restore karo — `?.` isliye kyunki fresh launch pe null hoti hai. Back press pe call nahi hota kyunki tab intentionally close kar rahe hain."*
