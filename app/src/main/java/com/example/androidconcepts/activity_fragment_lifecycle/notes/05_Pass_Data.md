# Pass Data Between Activities

## Kya Hai
Ek Activity se doosri Activity ko data bhejne ke liye Intent ka use karte hain. Simple data `putExtra()` se jaata hai. Complex objects ke liye `Parcelable` use karte hain.

---

## Kaise Kaam Karta Hai

```
Activity A
    ↓
intent.putExtra("key", value)   ← data Bundle mein pack hota hai
    ↓
startActivity(intent)
    ↓
Activity B ka onCreate()
    ↓
intent.getStringExtra("key")    ← data unpack karo
```

---

## Important Methods / Code

### Primitives bhejo

```kotlin
// Sender
val intent = Intent(this, DetailActivity::class.java).apply {
    putExtra("name", "Kamal")
    putExtra("age", 25)
    putExtra("isActive", true)
}
startActivity(intent)

// Receiver
val name = intent.getStringExtra("name")
val age = intent.getIntExtra("age", 0)
val isActive = intent.getBooleanExtra("isActive", false)
```

### Parcelable — Complex Object bhejo

```kotlin
// Model class — @Parcelize se automatically implement hota hai
@Parcelize
data class User(
    val name: String,
    val age: Int,
    val email: String
) : Parcelable

// Sender
intent.putExtra("user", User("Kamal", 25, "kamal@example.com"))

// Receiver
val user = intent.getParcelableExtra<User>("user")
// API 33+ pe:
val user = intent.getParcelableExtra("user", User::class.java)
```

### Bundle — Multiple Data ek saath pack karo

```kotlin
// Bundle mein data pack karo
val bundle = Bundle().apply {
    putString("name", "Kamal")
    putInt("age", 25)
    putBoolean("isActive", true)
}

// Intent mein bundle daal do
val intent = Intent(this, DetailActivity::class.java)
intent.putExtras(bundle)
startActivity(intent)

// Receiver mein same tarah get karo
val name = intent.getStringExtra("name")
val age = intent.getIntExtra("age", 0)
```

> **Bundle vs putExtra():** `putExtra()` internally Bundle hi use karta hai — dono same hain. Bundle explicitly tab use karo jab Fragment ko data bhejno ho (`fragment.arguments = bundle`) ya ek jagah saara data pack karna ho.

### Fragment ko data bhejo — arguments

```kotlin
// Fragment banate waqt Bundle daal do
val fragment = DetailFragment().apply {
    arguments = Bundle().apply {
        putString("name", "Kamal")
        putInt("age", 25)
    }
}

// Fragment mein receive karo
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val name = arguments?.getString("name")
    val age = arguments?.getInt("age", 0)
}
```

### List bhejo

```kotlin
// Sender
val list = arrayListOf("A", "B", "C")
intent.putStringArrayListExtra("list", list)

// Receiver
val list = intent.getStringArrayListExtra("list")
```

---

## Parcelable vs Serializable

| | `Parcelable` | `Serializable` |
|---|---|---|
| Speed | Fast (Android optimized) | Slow (Java reflection) |
| Code | `@Parcelize` annotation | `implement Serializable` |
| Platform | Android specific | Java standard |
| Use in Android | ✅ Prefer karo | ❌ Avoid karo |

> **Interview tip:** "Android mein Parcelable use karte hain kyunki ye Serializable se ~10x fast hai. `@Parcelize` annotation se boilerplate khatam ho jaata hai."

---

## Common Interview Questions

**Q: Parcelable aur Serializable mein fark?**
> Parcelable Android ka apna mechanism hai — fast hai kyunki reflection use nahi karta. Serializable Java standard hai — slow hai, Android mein avoid karte hain.

**Q: `@Parcelize` kya karta hai?**
> Kotlin plugin automatically `writeToParcel()` aur `createFromParcel()` methods generate karta hai. Manually implement nahi karna padta.

**Q: Intent se large data bhej sakte hain?**
> Nahi — Intent Bundle mein jaata hai jiska ~1MB limit hai. Large data ke liye ViewModel, Database, ya SharedPreferences use karo.

---

## Gotchas — Common Mistakes

**1. Default value bhoolna**
```kotlin
// GALAT — compile error
val age = intent.getIntExtra("age")

// SAHI
val age = intent.getIntExtra("age", 0)
```

**2. Key mismatch**
```kotlin
// Sender
putExtra("userName", "Kamal")

// Receiver — null milega
intent.getStringExtra("user_name")  // alag key
```

**3. Parcelable plugin missing**
```kotlin
// @Parcelize kaam nahi karega bina plugin ke
// build.gradle.kts mein ye hona chahiye:
plugins {
    id("kotlin-parcelize")
}
```

**4. Large object Intent mein dalna**
```kotlin
// GALAT — TransactionTooLargeException
intent.putExtra("bitmap", largeBitmap)

// SAHI — ID pass karo, data ViewModel/DB mein rakho
intent.putExtra("image_id", 101)
```
