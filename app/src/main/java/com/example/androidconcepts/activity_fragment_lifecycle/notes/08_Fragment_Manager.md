# Fragment Manager

## Kya Hai
FragmentManager Activity ke Fragments ko manage karta hai — add, replace, remove karna, back stack handle karna. `supportFragmentManager` se access karte hain Activity mein, `childFragmentManager` se Fragment ke andar.

---

## Kaise Kaam Karta Hai

```
supportFragmentManager.commit {
    add/replace/remove(container, fragment)
    addToBackStack(name)     ← optional
}
        ↓
FragmentTransaction execute hoti hai
        ↓
Container mein Fragment show hota hai
        ↓
Back stack update hoti hai (agar addToBackStack lagaya)
```

---

## Important Methods / Code

### add() — Fragment add karo

```kotlin
supportFragmentManager.commit {
    add(R.id.container, FragmentA())
    addToBackStack(null)
}
```
Purana Fragment alive rehta hai stack mein — destroy nahi hota.

### replace() — Fragment replace karo

```kotlin
supportFragmentManager.commit {
    replace(R.id.container, FragmentB())
    addToBackStack(null)
}
```
Purana Fragment destroy ho jaata hai — onDestroyView call hota hai.

### remove() — Fragment hatao

```kotlin
val fragment = supportFragmentManager.findFragmentById(R.id.container)
fragment?.let {
    supportFragmentManager.commit {
        remove(it)
    }
}
```

---

## add() vs replace()

| | `add()` | `replace()` |
|---|---|---|
| Purana Fragment | Alive rehta hai (hidden) | Destroy ho jaata hai |
| Memory | Zyada — dono load hain | Kam — sirf naya |
| Back press | Purana dikhta hai | Naya banata hai |
| Use case | Bottom Navigation tabs | Screen navigation |

```
add() ke baad stack:    [FragmentA, FragmentB]  ← A alive hai
replace() ke baad:      [FragmentB]              ← A destroy hua
```

---

## addToBackStack()

```kotlin
// Lagaya — back press pe Fragment wapas aayega
supportFragmentManager.commit {
    replace(R.id.container, FragmentB())
    addToBackStack("b")    // naam optional, null bhi chalega
}

// Nahi lagaya — back press pe Activity band hogi
supportFragmentManager.commit {
    replace(R.id.container, FragmentB())
    // addToBackStack nahi lagaya
}
```

| addToBackStack | Back Press Effect |
|---|---|
| Lagaya | Fragment pop hota hai, pichla dikhta hai |
| Nahi lagaya | Activity band ho jaati hai |

---

## supportFragmentManager vs childFragmentManager

```kotlin
// Activity mein — top level fragments ke liye
supportFragmentManager.commit { ... }

// Fragment ke andar — nested fragments ke liye
childFragmentManager.commit { ... }
```

> `parentFragmentManager` — Fragment se uske parent ka manager access karo (Activity level)

---

## Common Interview Questions

**Q: add() aur replace() mein kab kya use karein?**
> `replace()` navigation ke liye — jab ek screen se doosri pe jaao. `add()` jab dono fragments simultaneously alive rakhne ho — jaise Bottom Navigation tabs (tabs switch karte waqt state bachana chahte hain).

**Q: `commit()` aur `commitNow()` mein fark?**
> `commit()` asynchronous hai — main thread pe schedule hota hai, thodi der baad execute. `commitNow()` synchronous hai — seedha execute, back stack support nahi. Mostly `commit()` use karo.

**Q: Fragment transaction ke andar kya hota hai?**
> Transaction ek atomic operation hai — sab changes ek saath apply hote hain. `commit()` call karne pe Android main thread pe schedule karta hai.

---

## Gotchas — Common Mistakes

**1. commit() ke baad immediately findFragmentById()**
```kotlin
// GALAT — commit async hai, fragment abhi add nahi hua
supportFragmentManager.commit { add(R.id.container, FragmentA()) }
val f = supportFragmentManager.findFragmentById(R.id.container)  // null

// SAHI — commitNow() use karo agar turant chahiye
supportFragmentManager.commitNow { add(R.id.container, FragmentA()) }
val f = supportFragmentManager.findFragmentById(R.id.container)  // not null
```

**2. Activity level pe childFragmentManager use karna**
```kotlin
// GALAT — Activity mein childFragmentManager nahi hota
childFragmentManager.commit { ... }  // compile error

// SAHI
supportFragmentManager.commit { ... }
```

**3. Fragment ke andar Fragment add karne ke liye parentFragmentManager use karna**
```kotlin
// GALAT — sibling nahi banega, Activity level pe jaayega
parentFragmentManager.commit { add(R.id.inner, InnerFragment()) }

// SAHI — nested fragment ke liye
childFragmentManager.commit { add(R.id.inner, InnerFragment()) }
```
