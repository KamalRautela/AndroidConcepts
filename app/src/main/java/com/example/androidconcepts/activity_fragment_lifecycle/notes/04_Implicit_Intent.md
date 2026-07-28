# Implicit Intent

## Kya Hai
Implicit Intent mein exact class specify nahi karte — sirf **action** batate hain. Android system decide karta hai kaun sa app handle karega. Doosri apps ke saath interact karne ke liye use hota hai — browser open karo, camera app se photo lo, share karo.

---

## Kaise Kaam Karta Hai

```
startActivity(intent) call karo
        ↓
Android OS saari apps check karta hai
        ↓
Kaun handle kar sakta hai ye action?
        ↓
Ek app mili → directly open
Multiple apps mili → Chooser dialog dikhta hai
Koi nahi mili → ActivityNotFoundException crash
```

---

## Important Methods / Code

### URL open karo — Browser

```kotlin
val intent = Intent(Intent.ACTION_VIEW).apply {
    data = Uri.parse("https://google.com")
}
startActivity(intent)
```

### Phone call karo

```kotlin
val intent = Intent(Intent.ACTION_DIAL).apply {
    data = Uri.parse("tel:+919876543210")
}
startActivity(intent)
```

### Share karo

```kotlin
val intent = Intent(Intent.ACTION_SEND).apply {
    type = "text/plain"
    putExtra(Intent.EXTRA_TEXT, "Share karo ye text")
}
startActivity(Intent.createChooser(intent, "Share via"))
```

### Email bhejo

```kotlin
val intent = Intent(Intent.ACTION_SENDTO).apply {
    data = Uri.parse("mailto:kamal@example.com")
    putExtra(Intent.EXTRA_SUBJECT, "Subject")
    putExtra(Intent.EXTRA_TEXT, "Email body")
}
startActivity(intent)
```

### Crash se bachao — resolveActivity() check

```kotlin
if (intent.resolveActivity(packageManager) != null) {
    startActivity(intent)
} else {
    Toast.makeText(this, "Koi app nahi mili", Toast.LENGTH_SHORT).show()
}
```

---

## Explicit vs Implicit

| | Explicit | Implicit |
|---|---|---|
| Target | Exact class naam | Action (kya karna hai) |
| Use case | Same app ke andar | Doosri apps ke saath |
| Example | `Intent(this, DetailActivity::class.java)` | `Intent(ACTION_VIEW)` |
| Chooser | Nahi | Haan (multiple apps mili toh) |

---

## Common Interview Questions

**Q: Implicit Intent kaise kaam karta hai?**
> Action specify karte hain — jaise `ACTION_VIEW`, `ACTION_SEND`. Android system installed apps ka IntentFilter check karta hai. Jo match kare wo handle karta hai. Multiple match hoon toh chooser dialog dikhta hai.

**Q: IntentFilter kya hota hai?**
> App apne Manifest mein declare karta hai ki wo kaun se Intents handle kar sakta hai. Jaise browser declare karta hai ki wo `ACTION_VIEW` with `http://` handle kar sakta hai.

**Q: `ACTION_DIAL` aur `ACTION_CALL` mein fark?**
> `ACTION_DIAL` — dialer open karta hai number ke saath, user call manually karta hai. `ACTION_CALL` — seedha call kar deta hai, `CALL_PHONE` permission chahiye.

---

## Gotchas — Common Mistakes

**1. resolveActivity() check na karna**
```kotlin
// GALAT — crash hoga agar koi app nahi mili
startActivity(intent)

// SAHI
if (intent.resolveActivity(packageManager) != null) {
    startActivity(intent)
}
```

**2. Share mein createChooser() na lagana**
```kotlin
// GALAT — pehli baar chooser dikhega, baad mein default app use hogi
startActivity(shareIntent)

// SAHI — hamesha chooser dikhega
startActivity(Intent.createChooser(shareIntent, "Share via"))
```

**3. ACTION_CALL bina permission ke**
```kotlin
// GALAT — SecurityException aayega
Intent(Intent.ACTION_CALL, Uri.parse("tel:123"))

// SAHI — permission lo ya ACTION_DIAL use karo
Intent(Intent.ACTION_DIAL, Uri.parse("tel:123"))
```
