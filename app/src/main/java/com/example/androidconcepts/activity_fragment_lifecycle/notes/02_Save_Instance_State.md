# Save Instance State

## Kya Hai
Screen rotate hone pe Android Activity destroy karke recreate karta hai. Is recreate mein UI ka saara data (typed text, counter value) chala jaata hai. `onSaveInstanceState` se data Bundle mein save karo — recreate ke baad restore ho jaata hai.

---

## Kaise Kaam Karta Hai

```
User screen rotate karta hai
          ↓
    onPause()
    onStop()
    onSaveInstanceState()   ← data Bundle mein save karo
    onDestroy()
          ↓
    [Activity recreate]
          ↓
    onCreate(savedInstanceState)  ← Bundle wapas milti hai
    onStart()
    onResume()
```

> **Back press pe nahi hota** — back press pe Activity intentionally close hoti hai, data bachane ki zaroorat nahi.

---

## Important Methods / Code

### Data Save Karo

```kotlin
companion object {
    private const val KEY_COUNT = "key_count"
}

override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    outState.putInt(KEY_COUNT, counter)   // Bundle mein save
}
```

### Data Restore Karo — onCreate() mein

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // savedInstanceState null hogi fresh launch pe
    // rotation ke baad data hoga
    counter = savedInstanceState?.getInt(KEY_COUNT) ?: 0
}
```

### onRestoreInstanceState() — Alternative

```kotlin
// sirf recreate pe call hota hai — null check nahi chahiye
override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    counter = savedInstanceState.getInt(KEY_COUNT)  // directly, no ?.
}
```

---

## onCreate() vs onRestoreInstanceState()

| | `onCreate()` | `onRestoreInstanceState()` |
|---|---|---|
| Kab call hota hai | Hamesha — fresh + recreate | Sirf recreate pe |
| Bundle | Null ho sakti hai (`?.` zaroori) | Guaranteed non-null |
| Preference | 99% cases yahi use karo | Rarely needed |

---

## ViewModel vs onSaveInstanceState

| | `onSaveInstanceState` | `ViewModel` |
|---|---|---|
| Rotation survive | ✅ | ✅ |
| Process kill survive | ✅ | ❌ |
| Data type | Primitives, Parcelable | Anything |
| Data limit | ~1MB (Bundle limit) | No limit |
| Use case | Small UI state (scroll, counter) | Large data, API response |

> **Interview tip:** "ViewModel rotation survive karta hai but process kill nahi. `onSaveInstanceState` dono survive karta hai but sirf small data ke liye."

---

## Common Interview Questions

**Q: onSaveInstanceState kab call hota hai?**
> Configuration change se pehle — screen rotation, language change, dark mode toggle. Back press pe call nahi hota.

**Q: ViewModel aur onSaveInstanceState mein se kaunsa use karein?**
> Dono saath use karte hain. ViewModel mein main data rakho (API response, list). `onSaveInstanceState` mein sirf UI state rakho (scroll position, selected tab index) jo process kill ke baad bhi chahiye.

**Q: Bundle mein kya save kar sakte hain?**
> Primitives (Int, String, Boolean), Parcelable objects, aur arrays. Large objects (Bitmap) avoid karo — ~1MB limit hai.

---

## Gotchas — Common Mistakes

**1. `?.` bhool jaana onCreate mein**
```kotlin
// GALAT — fresh launch pe crash
counter = savedInstanceState!!.getInt(KEY_COUNT)

// SAHI
counter = savedInstanceState?.getInt(KEY_COUNT) ?: 0
```

**2. Back press pe data expect karna**
```kotlin
// GALAT soch — back press pe onSaveInstanceState nahi aata
// User wapas aaye toh Activity fresh banti hai
```

**3. Large data Bundle mein dalna**
```kotlin
// GALAT — TransactionTooLargeException aa sakta hai
outState.putParcelable("bitmap", largeBitmap)

// SAHI — ViewModel mein rakho, sirf ID save karo
outState.putInt("image_id", imageId)
```
