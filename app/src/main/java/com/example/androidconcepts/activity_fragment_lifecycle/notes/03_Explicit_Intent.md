# Explicit Intent

## Kya Hai
Explicit Intent ek specific Activity ko directly open karta hai — naam se. Hum exact class specify karte hain jise open karna hai. Same app ke andar navigation ke liye use hota hai.

---

## Kaise Kaam Karta Hai

```
User button dabata hai
        ↓
Intent banao (current context + target class)
        ↓
startActivity(intent)
        ↓
Android OS target Activity ko launch karta hai
        ↓
Target Activity ka onCreate() call hota hai
```

---

## Important Methods / Code

### Basic — Activity open karo

```kotlin
val intent = Intent(this, DetailActivity::class.java)
startActivity(intent)
```

### Data saath bhejo — putExtra()

```kotlin
val intent = Intent(this, DetailActivity::class.java)
intent.putExtra("user_name", "Kamal")
intent.putExtra("user_age", 25)
startActivity(intent)
```

### Data receive karo — doosri Activity mein

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val name = intent.getStringExtra("user_name")
    val age = intent.getIntExtra("user_age", 0)  // 0 = default value
}
```

### Result wapas lo — ActivityResultLauncher

```kotlin
// Launcher register karo
private val launcher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    if (result.resultCode == RESULT_OK) {
        val data = result.data?.getStringExtra("result_key")
    }
}

// Launch karo
launcher.launch(Intent(this, FormActivity::class.java))

// FormActivity mein result bhejo
val resultIntent = Intent().apply {
    putExtra("result_key", "form filled")
}
setResult(RESULT_OK, resultIntent)
finish()
```

---

## Common Interview Questions

**Q: Explicit aur Implicit Intent mein fark?**
> Explicit mein exact class specify karte hain — same app ke andar. Implicit mein action specify karte hain — Android system decide karta hai kaun handle karega (doosri apps bhi).

**Q: `startActivity` aur `startActivityForResult` mein fark?**
> `startActivityForResult` deprecated hai. Modern way `ActivityResultLauncher` hai — lifecycle aware hai, memory leak nahi hota.

**Q: Intent kya hota hai?**
> Intent ek message hota hai jo Android OS ko batata hai kya karna hai — kaunsi Activity open karo, kaunsa Action perform karo. Data bhi saath carry kar sakta hai.

---

## Gotchas — Common Mistakes

**1. Default value bhoolna getIntExtra mein**
```kotlin
// GALAT — agar key nahi mili toh 0 return hoga by default
// lekin explicit likhna better practice hai
val age = intent.getIntExtra("age")  // compile error

// SAHI
val age = intent.getIntExtra("age", 0)  // default value zaroori
```

**2. Keys mismatch hona**
```kotlin
// Sender
intent.putExtra("user_Name", "Kamal")  // capital N

// Receiver
val name = intent.getStringExtra("user_name")  // null milega
```

**3. Activity Manifest mein register na karna**
```xml
<!-- Ye bhool jaate hain — app crash karega -->
<activity android:name=".DetailActivity" />
```
