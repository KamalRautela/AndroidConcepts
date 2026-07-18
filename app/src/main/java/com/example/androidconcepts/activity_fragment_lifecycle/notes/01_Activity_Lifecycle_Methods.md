# Activity kya hai + Lifecycle Methods

## Activity kya hai?
Activity Android app ki ek single screen hoti hai jiske saath user interact karta hai.
Jaise login screen ek Activity, home screen ek alag Activity.
Ek app mein multiple activities ho sakti hain. Har Activity ka apna lifecycle hota hai —
Android system usse create karta hai, pause karta hai, aur destroy karta hai.

---

## Lifecycle Methods — Full Sequence

```
onCreate() → onStart() → onResume()
                              ↓
                     [Activity Running]
                              ↓
                         onPause()
                              ↓
                          onStop()
                         ↙        ↘
               onRestart()      onDestroy()
                    ↓
                onStart()
```

---

## Har Method — Kya Hota Hai

| Method | Kab Call Hota Hai | Kya Karo Yahan |
|---|---|---|
| `onCreate()` | Pehli baar create hoti hai — sirf ek baar | ViewBinding, ViewModel, RecyclerView setup |
| `onStart()` | Activity visible hoti hai screen pe | — |
| `onResume()` | Foreground mein, user interact kar sakta hai | Camera, sensors start karo |
| `onPause()` | Partially visible — Dialog/BottomSheet upar aa gayi | Camera, animations pause karo — fast rakho |
| `onStop()` | Completely background mein | Heavy resources release karo, data save karo |
| `onRestart()` | onStop ke baad wapas foreground pe aaye | — |
| `onDestroy()` | Activity band ho rahi hai | Cleanup |

---

## onPause() vs onStop()

| | `onPause()` | `onStop()` |
|---|---|---|
| Visibility | Partially visible | Completely hidden |
| Example | Dialog/BottomSheet aa gayi | Home button press kiya |
| Next | onResume() (agar wapas aao) | onRestart() → onStart() (agar wapas aao) |

---

## onDestroy() — 2 Cases

1. **User ne close kiya** — back button press, `finish()` call
2. **Configuration change** — screen rotate hone pe Activity destroy + recreate hoti hai

```kotlin
override fun onDestroy() {
    super.onDestroy()
    if (isFinishing) {
        // user ne close kiya
    } else {
        // configuration change
    }
}
```

---

## Activity States

| State | Kab | Lifecycle Method |
|---|---|---|
| **Active / Running** | User screen pe hai, interact kar raha hai | `onResume()` ke baad |
| **Paused** | Partially visible, focus nahi | `onPause()` ke baad |
| **Stopped** | Completely invisible | `onStop()` ke baad |
| **Destroyed** | Activity khatam | `onDestroy()` ke baad |

---

## super Call Ka Order

**Creation methods** — `super` pehle, phir apna kaam:
```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)  // ← pehle
    Log.d(tag, "onCreate Called")
}
// onStart(), onResume(), onRestart() — same pattern
```

**Destruction methods** — pehle apna kaam, phir `super`:
```kotlin
override fun onPause() {
    Log.d(tag, "onPause Called")  // ← pehle cleanup
    super.onPause()
}
// onStop(), onDestroy() — same pattern
```

**Kyun?** Creation mein Android pehle setup kare, tab tera kaam kaam karega. Destruction mein pehle apna cleanup karo, phir Android destroy kare.

---

## Interview Mein Bolna
> *"Activity Android ki ek single screen hoti hai. Iska lifecycle onCreate se shuru hota hai jahan ViewBinding aur ViewModel setup karta hoon, onResume mein user interact kar sakta hai. onPause tab aata hai jab koi Dialog aa jaaye — partially visible. onStop tab jab completely background mein jaaye. onDestroy 2 cases mein — user ne close kiya ya screen rotate hua."*
