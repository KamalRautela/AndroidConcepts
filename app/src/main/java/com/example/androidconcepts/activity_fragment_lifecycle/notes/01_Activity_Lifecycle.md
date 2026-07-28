# Activity Lifecycle

## Kya Hai
Activity Android app ki ek single screen hoti hai. Android system Activity ko create karta hai, pause karta hai, stop karta hai, aur destroy karta hai — is process ko lifecycle kehte hain. Hume lifecycle methods mein apna kaam karna hota hai.

---

## Kaise Kaam Karta Hai

```
App open karo
      ↓
  onCreate()       ← UI setup, ViewBinding
      ↓
  onStart()        ← Activity visible
      ↓
  onResume()       ← User interact kar sakta hai
      ↓
[Activity Running]
      ↓
  onPause()        ← Focus chala gaya (dialog aa gayi)
      ↓
  onStop()         ← Completely invisible (home press)
      ↓          ↘
onRestart()     onDestroy()
      ↓              ↓
  onStart()      [Activity khatam]
```

### Scenarios

| Action | Methods |
|---|---|
| App pehli baar open | onCreate → onStart → onResume |
| Home button | onPause → onStop |
| Wapas aao | onRestart → onStart → onResume |
| Back button | onPause → onStop → onDestroy |
| Screen rotate | onPause → onStop → onDestroy → onCreate → onStart → onResume |

---

## Important Methods / Code

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // ViewBinding, ViewModel, RecyclerView setup — sirf ek baar
}

override fun onResume() {
    super.onResume()
    // Camera, sensors start karo
}

override fun onPause() {
    // Camera, sensors release karo — fast rakho
    super.onPause()
}

override fun onStop() {
    // Heavy resources release karo
    super.onStop()
}

override fun onDestroy() {
    super.onDestroy()
    if (isFinishing) {
        // user ne close kiya
    } else {
        // rotation — recreate hoga
    }
}
```

> **Super call order:**
> Creation mein (`onCreate`, `onStart`, `onResume`) — `super` pehle, phir apna kaam
> Destruction mein (`onPause`, `onStop`, `onDestroy`) — pehle apna kaam, phir `super`

---

## Common Interview Questions

**Q: onPause() aur onStop() mein fark?**
> `onPause()` tab aata hai jab Activity partially visible ho (dialog ke peeche). `onStop()` tab jab completely invisible ho (home press). `onPause()` mein bahut kam time milta hai isliye heavy kaam mat karo.

**Q: onDestroy() kab call hota hai?**
> Do cases mein — user ne back press kiya ya `finish()` call kiya (intentional), ya screen rotate hui (configuration change). `isFinishing` se distinguish kar sakte hain.

**Q: Rotation pe kya hota hai?**
> Activity destroy hokar recreate hoti hai — `onCreate()` dobara call hota hai. Data bachane ke liye `onSaveInstanceState` ya `ViewModel` use karte hain.

---

## Gotchas — Common Mistakes

**1. onPause() mein heavy kaam karna**
```kotlin
// GALAT — next Activity tab tak wait karegi
override fun onPause() {
    database.saveAllData()
    super.onPause()
}

// SAHI — onStop() mein karo
override fun onStop() {
    database.saveAllData()
    super.onStop()
}
```

**2. onCreate() mein super baad mein call karna**
```kotlin
// GALAT
override fun onCreate(savedInstanceState: Bundle?) {
    setContentView(R.layout.activity_main)  // crash
    super.onCreate(savedInstanceState)
}

// SAHI
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)      // pehle
    setContentView(R.layout.activity_main)
}
```

**3. Back press pe onSaveInstanceState expect karna**
> `onSaveInstanceState` sirf configuration change (rotation) pe call hota hai — back press pe nahi. Back press pe data permanently chala jaata hai.
