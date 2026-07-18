# Activity Launch Modes

## Kya Hai?
Launch mode define karta hai — Activity ka new instance banega ya existing use hoga jab open karo.
Set karte hain `AndroidManifest.xml` mein.

```xml
<activity
    android:name=".MainActivity"
    android:launchMode="singleTop" />
```

---

## 4 Types

### 1. standard (Default)
Har baar new instance create hota hai.
```
A → B → B → B  (3 baar B open = 3 instances)
```

### 2. singleTop
Agar Activity already stack ke **top pe** hai — new instance nahi, `onNewIntent()` call hota hai.
```
A → B → B open karo = same B (top pe tha)
A → B → C → B open karo = new B (top pe C tha)
```

### 3. singleTask
Poori app mein **ek hi instance**. Agar exist karta hai — uske upar wali activities destroy ho jaati hain.
```
A → B → C → A open karo = B, C destroy → A pe aao
```
Use case: Google Maps, Main Activity

### 4. singleInstance
singleTask jaisa + apna **alag task** mein rehta hai. Dusri activities share nahi hoti.

---

## Comparison Table

| Mode | New Instance | Stack Behavior |
|---|---|---|
| `standard` | Hamesha | Multiple instances |
| `singleTop` | Top pe na ho tab | onNewIntent() top pe ho tab |
| `singleTask` | Ek hi | Upar wali activities destroy |
| `singleInstance` | Ek hi | Alag task mein |

---

## Interview Mein Bolna
> *"Launch modes 4 hote hain — `standard` default hai jahan har baar new instance banta hai. `singleTop` mein agar Activity stack ke top pe already hai toh new instance nahi banta, `onNewIntent()` call hota hai. `singleTask` mein poori app mein ek hi instance hota hai — agar bottom mein hai aur call karo toh upar wali activities destroy ho jaati hain. `singleInstance` alag task mein hota hai."*
