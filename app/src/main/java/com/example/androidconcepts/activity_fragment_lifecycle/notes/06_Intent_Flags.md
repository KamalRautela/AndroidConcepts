# Intent Flags

## Kya Hai
Intent Flags back stack ka behaviour control karte hain — naya Activity kaise add hoga, purani Activities kya hongi. `addFlags()` se set karte hain.

---

## Kaise Kaam Karta Hai

```
Normal startActivity() — har baar new instance stack pe push hota hai:
[A] → [A, B] → [A, B, C] → back → [A, B] → back → [A]

Flags se ye behaviour change hota hai:
- Stack clear karo
- Duplicate instance rokho
- Kisi Activity pe wapas jaao
```

---

## 5 Important Flags

### 1. FLAG_ACTIVITY_NO_HISTORY
Activity back stack mein save nahi hoti. Back press pe seedha pichli Activity.

```kotlin
intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
```
```
[A] → [A, B(no_history)] → [A, B, C] → back → [A]  // B skip
```
**Use case:** Splash screen, OTP screen

---

### 2. FLAG_ACTIVITY_SINGLE_TOP
Agar Activity stack ke **top pe** already hai — new instance nahi banega, `onNewIntent()` call hoga.

```kotlin
intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
```
```
[A, B] → B ko dobara open karo → [A, B]  // same B, onNewIntent() called
[A, B] → A ko open karo → [A, B, A]       // A top pe nahi tha
```
**Use case:** Notification tap pe same screen dobara open na ho

---

### 3. FLAG_ACTIVITY_CLEAR_TOP
Target Activity ke upar ki saari Activities destroy ho jaati hain.

```kotlin
intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
```
```
[A, B, C, D] → A open karo with CLEAR_TOP → [A]  // B, C, D destroy
```
**Use case:** Home button — seedha home pe jaao

---

### 4. FLAG_ACTIVITY_CLEAR_TASK + FLAG_ACTIVITY_NEW_TASK
Poora back stack clear ho jaata hai, sirf naya Activity rehta hai. Dono flags saath lagte hain.

```kotlin
intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
```
```
[A, B, C] → Login ke baad Dashboard open karo → [Dashboard]
Back press pe app band ho jaati hai
```
**Use case:** Logout → Login screen, ya Login → Dashboard

---

### 5. FLAG_ACTIVITY_REORDER_TO_FRONT
Agar Activity already stack mein hai — usse top pe le aao, new instance nahi banega.

```kotlin
intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
```
```
[A, B, C] → A open karo → [B, C, A]  // A top pe aa gaya
```
**Use case:** Bottom Navigation — same tab dobara tap karo

---

## Quick Reference Table

| Flag | Effect | Use Case |
|---|---|---|
| `NO_HISTORY` | Stack mein save nahi | Splash, OTP |
| `SINGLE_TOP` | Top pe ho toh reuse | Notification |
| `CLEAR_TOP` | Upar wali destroy | Home button |
| `CLEAR_TASK + NEW_TASK` | Poora stack clear | Logout |
| `REORDER_TO_FRONT` | Stack mein se top pe | Bottom Nav |

---

## Common Interview Questions

**Q: `SINGLE_TOP` aur `REORDER_TO_FRONT` mein fark?**
> `SINGLE_TOP` sirf tab kaam karta hai jab Activity stack ke top pe ho. `REORDER_TO_FRONT` stack mein kahin bhi ho — top pe le aata hai.

**Q: `CLEAR_TOP` aur `CLEAR_TASK` mein fark?**
> `CLEAR_TOP` target Activity ke upar wali destroy karta hai — target bachti hai. `CLEAR_TASK` poora stack destroy karta hai — sirf naya Activity rehta hai.

**Q: `onNewIntent()` kab call hota hai?**
> Jab `SINGLE_TOP` ya `singleTop` launch mode se Activity already top pe ho aur dobara launch ho. New instance nahi banta — same Activity ko naya Intent milta hai.

---

## Launch Modes vs Intent Flags

Launch Modes aur Intent Flags ka effect similar hota hai — lekin dono alag hain.

| | Launch Modes | Intent Flags |
|---|---|---|
| Kahan set hota hai | `AndroidManifest.xml` | Code mein `addFlags()` |
| Kab apply hota hai | Hamesha — har launch pe | Sirf us ek specific launch pe |
| Scope | Activity ki permanent setting | One-time behaviour |

### 4 Launch Modes

**standard** — Default. Har baar new instance.
```
[A, B] → B open karo → [A, B, B]
```

**singleTop** — Top pe ho toh reuse, warna new instance.
```
[A, B] → B open karo → [A, B]         // top pe tha — onNewIntent()
[A, B] → A open karo → [A, B, A]      // top pe nahi tha
```

**singleTask** — Poori app mein ek hi instance. Upar wali Activities destroy.
```
[A, B, C] → A open karo → [A]         // B, C destroy ho gayi
```
Use case: Main/Home Activity, Google Maps

**singleInstance** — singleTask jaisa + apna alag task mein rehta hai.

```xml
<!-- Manifest mein set karo -->
<activity
    android:name=".MainActivity"
    android:launchMode="singleTask" />
```

### Launch Mode vs Flag — Kab Kya Use Karein

| Situation | Use |
|---|---|
| Activity ki permanent behaviour set karni hai | Launch Mode |
| Sirf ek specific case mein behaviour change karni hai | Intent Flag |
| Notification se aao toh duplicate na bane (hamesha) | `singleTop` launch mode |
| Logout pe ek baar stack clear karna hai | `CLEAR_TASK` flag |

---

## Gotchas — Common Mistakes

**1. CLEAR_TASK akela use karna**
```kotlin
// GALAT — kaam nahi karega
intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

// SAHI — NEW_TASK saath zaroori hai
intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
```

**2. onNewIntent() mein setIntent() bhoolna**
```kotlin
// SAHI — warna getIntent() purana Intent return karega
override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)  // update karo
    // ab getIntent() naya Intent dega
}
```

**3. NO_HISTORY aur important screen**
```kotlin
// GALAT use case — user wapas jaana chahta hai
// Payment screen pe NO_HISTORY mat lagao
// Sirf one-time screens pe lagao (Splash, OTP)
```
