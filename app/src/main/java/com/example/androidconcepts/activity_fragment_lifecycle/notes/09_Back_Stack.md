# Fragment Back Stack

## Kya Hai
Back Stack ek stack hai jisme Fragment transactions save hoti hain. `addToBackStack()` se transaction save hoti hai — back press pe wapas pichli state pe jaate hain. Bina `addToBackStack()` ke back press pe Activity band ho jaati hai.

---

## Kaise Kaam Karta Hai

```
Push A → Push B → Push C
        ↓
Back Stack: [A, B, C]   ← C top pe hai, screen pe dikh raha hai
        ↓
Back press (popBackStack)
        ↓
Back Stack: [A, B]      ← B wapas dikhta hai
```

### Named Back Stack

```kotlin
// naam se push karo
commit { replace(container, FragmentA()); addToBackStack("a") }
commit { replace(container, FragmentB()); addToBackStack("b") }
commit { replace(container, FragmentC()); addToBackStack("c") }

// Stack: [a, b, c]
```

---

## Important Methods / Code

### Push — addToBackStack()

```kotlin
supportFragmentManager.commit {
    replace(R.id.container, FragmentB())
    addToBackStack("b")    // naam — null bhi chalega
}
```

### Pop — Ek wapas

```kotlin
supportFragmentManager.popBackStack()
// Stack: [a, b, c] → [a, b]
```

### Pop — Kisi specific tak (exclusive)

```kotlin
// "b" tak jaao, "b" bachega
supportFragmentManager.popBackStack("b", 0)
// Stack: [a, b, c] → [a, b]
```

### Pop — Kisi specific tak (inclusive)

```kotlin
// "b" tak jaao, "b" bhi hata do
supportFragmentManager.popBackStack("b", FragmentManager.POP_BACK_STACK_INCLUSIVE)
// Stack: [a, b, c] → [a]
```

### Clear All

```kotlin
supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
// Stack: [a, b, c] → []
```

### Back Stack Count Monitor karo

```kotlin
supportFragmentManager.addOnBackStackChangedListener {
    val count = supportFragmentManager.backStackEntryCount
    // count update pe UI update karo
}
```

### Entry names read karo

```kotlin
val count = supportFragmentManager.backStackEntryCount
for (i in 0 until count) {
    val name = supportFragmentManager.getBackStackEntryAt(i).name
}
// [a, b, c] → "a → b → c"
```

---

## popBackStack() Variants

| Method | Effect |
|---|---|
| `popBackStack()` | Ek entry pop |
| `popBackStack("b", 0)` | "b" tak pop, "b" bachega |
| `popBackStack("b", INCLUSIVE)` | "b" tak pop, "b" bhi hata |
| `popBackStack(null, INCLUSIVE)` | Poora stack clear |

---

## Common Interview Questions

**Q: addToBackStack(null) aur addToBackStack("name") mein fark?**
> `null` dene pe entry banega lekin naam se target nahi kar sakte. Name dene pe `popBackStack("name", flag)` se directly us entry tak pop kar sakte hain.

**Q: `popBackStack()` aur `popBackStack("name", 0)` mein fark?**
> `popBackStack()` sirf ek entry pop karta hai. `popBackStack("name", 0)` naam wali entry tak saari entries pop karta hai — naam wali entry bachti hai (exclusive). `INCLUSIVE` flag lagao toh naam wali bhi pop ho jaati hai.

**Q: Fragment back stack aur Activity back stack mein fark?**
> Activity back stack OS manage karta hai — back press pe Activities pop hoti hain. Fragment back stack `FragmentManager` manage karta hai — back press pe Fragment transactions pop hoti hain. Fragment back stack empty ho jaaye toh Activity back stack pe jaata hai.

---

## Gotchas — Common Mistakes

**1. addToBackStack bina naam ke, phir naam se pop karna**
```kotlin
// GALAT — naam nahi diya, naam se pop nahi kar sakte
commit { replace(container, FragmentA()); addToBackStack(null) }
supportFragmentManager.popBackStack("a", 0)  // kaam nahi karega

// SAHI — naam do
commit { replace(container, FragmentA()); addToBackStack("a") }
supportFragmentManager.popBackStack("a", 0)
```

**2. add() ke saath back stack — purana Fragment destroy nahi hota**
```kotlin
// add() use kiya — FragmentA alive hai back stack mein
commit { add(container, FragmentB()); addToBackStack(null) }
// Back press pe FragmentB pop, FragmentA wapas visible

// replace() use kiya — FragmentA destroy hua, recreate hoga
commit { replace(container, FragmentB()); addToBackStack(null) }
// Back press pe FragmentB pop, FragmentA ka onCreateView dobara call
```

**3. Back stack count se UI update — listener bhoolna**
```kotlin
// GALAT — ek baar read kiya, update nahi hoga
val count = supportFragmentManager.backStackEntryCount

// SAHI — listener lagao
supportFragmentManager.addOnBackStackChangedListener {
    val count = supportFragmentManager.backStackEntryCount
    tvCount.text = "$count entries"
}
```
