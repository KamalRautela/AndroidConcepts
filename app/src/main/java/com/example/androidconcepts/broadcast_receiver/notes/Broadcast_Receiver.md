# Broadcast Receiver — Notes

Android component jo system-wide "events" sunta hai — kisi bhi app se bheja gaya broadcast (custom ho ya system ka). Demo: Static Receiver (custom explicit broadcast) aur Dynamic Receiver (real system broadcast — Airplane Mode).

---

## Kya Hai

`BroadcastReceiver` ek aisa component hai jo **events (broadcasts) sunta hai** — chahe woh Android system bheje (jaise battery low, airplane mode change) ya koi app khud bheje. Do tarah se register hota hai:

| | Static (Manifest) | Dynamic (Code) |
|---|---|---|
| Kaha register hota hai | `AndroidManifest.xml` mein `<receiver>` tag | Activity/Service ke code mein `registerReceiver()` |
| Kab tak zinda | Poori app ki lifetime (process na ho tab bhi system ko pata hai) | Sirf jab tak register-wala component (Activity) zinda hai |
| Android 8+ (API 26) restriction | **Zyada tar system broadcasts nahi sun sakta** (sirf exempted list) | Koi restriction nahi, sab kuch sun sakta hai |
| Kab use karo | Sirf exempted system broadcasts (`BOOT_COMPLETED`) ya apni khud ki app ke custom broadcasts | Real-time UI update chahiye ho jab tak screen khuli hai (connectivity, airplane mode, etc.) |

---

## Demo 1 — Static Receiver (Custom Explicit Broadcast)

### Code

**`StaticReceiver.kt`**
```kotlin
class StaticReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "${intent?.action}")
    }
    companion object {
        private const val TAG = "STATIC_BROADCAST_RECEIVER"
    }
}
```

**`StaticBroadcastReceiverActivity.kt`** — button dabane par bhejta hai
```kotlin
binding.btnSendPing.setOnClickListener {
    val intent = Intent("com.example.androidconcepts.SEND_PING")
        .setClass(this, StaticReceiver::class.java)
    sendBroadcast(intent)
}
```

**`AndroidManifest.xml`**
```xml
<receiver
    android:name=".broadcast_receiver.static_broadcast_receiver.StaticReceiver"
    android:exported="false">
    <intent-filter>
        <action android:name="com.example.androidconcepts.SEND_PING" />
    </intent-filter>
</receiver>
```

### Key Concepts

| Concept | Detail |
|---|---|
| **Explicit intent** (`.setClass(...)`) | Sirf action string se nahi, **target receiver bata ke** bhejte hain. Lint warning se bachta hai ("matches non-exported component") |
| `android:exported="false"` | Sirf **apni app** ke andar se broadcasts accept karega, bahar ki app se nahi |
| Manifest-declared | Receiver object kabhi manually banana nahi padta — jaise hi matching broadcast aata hai, Android khud `onReceive()` call karta hai, chahe koi Activity khuli ho ya na ho |

### Experiment — Android 8+ System Broadcast Restriction

Humne **jaanbujh ke** `StaticReceiver` ko `android.net.conn.CONNECTIVITY_CHANGE` (system broadcast) sunne ke liye Manifest mein register kiya — **result: kuch nahi mila**, Logcat khaali raha.

**Wajah:** Android 7+ (`targetSdk` 24+) se, static (Manifest-declared) receivers **zyada tar implicit system broadcasts nahi receive kar sakte**. Android Studio ka Lint bhi yeh warning deta hai: *"Declaring a broadcastreceiver for CONNECTIVITY_CHANGE is deprecated for apps targeting N and higher."*

**Exempted broadcasts** (jo static receiver abhi bhi sun sakta hai) — official list se kuch important:
- `BOOT_COMPLETED` / `LOCKED_BOOT_COMPLETED` — phone restart
- `MY_PACKAGE_REPLACED` — apni app update hui
- `SMS_RECEIVED` — SMS aaya
- `TIMEZONE_CHANGED`, `LOCALE_CHANGED`, `TIME_SET`
- `USB_DEVICE_ATTACHED` / `DETACHED`

`CONNECTIVITY_CHANGE` aur `AIRPLANE_MODE_CHANGED` is list mein **nahi** hain — isliye static receiver inhe miss karta hai.

### Ek Aur Galti Jo Humne Ki (Aur Seekha)

Humne galti se button se khud `sendBroadcast(Intent("android.net.conn.CONNECTIVITY_CHANGE"))` call kiya — **crash** aaya:
```
SecurityException: Permission Denial: not allowed to send broadcast android.net.conn.CONNECTIVITY_CHANGE
```
**Wajah:** Yeh ek **"protected broadcast"** hai — sirf Android system khud bhej sakta hai, koi bhi third-party app usse manually trigger nahi kar sakti (chahe kitni bhi permission ho). Isiliye final demo mein button hata diya — sirf **real** WiFi/Data toggle se test karte hain.

---

## Demo 2 — Dynamic Receiver (Real System Broadcast + Extras)

### Code

**`DynamicBroadcastReceiver.kt`**
```kotlin
class DynamicBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val isAirplaneModeOn = intent?.getBooleanExtra("state", false)
        Log.d(TAG, "Airplane mode ON: $isAirplaneModeOn")
    }
    companion object {
        private const val TAG = "DYNAMIC BROADCAST RECEIVER"
    }
}
```

**`DynamicBroadcastReceiverActivity.kt`**
```kotlin
private val receiver = DynamicBroadcastReceiver()

override fun onStart() {
    super.onStart()
    val intentFilter = IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        registerReceiver(receiver, intentFilter, RECEIVER_EXPORTED)
    } else {
        registerReceiver(receiver, intentFilter)
    }
}

override fun onStop() {
    unregisterReceiver(receiver)
    super.onStop()
}
```

**Manifest mein koi `<receiver>` tag nahi** — yeh poora registration code mein hota hai, isliye "dynamic".

### Key Concepts

| Concept | Detail |
|---|---|
| `onStart()` mein register, `onStop()` mein unregister | Lifecycle-bound — jab screen dikhti hai tabhi sunta hai |
| `RECEIVER_EXPORTED` vs `RECEIVER_NOT_EXPORTED` (API 33+) | **System/doosri app** ka broadcast → `EXPORTED`. **Apni app** ka khud ka broadcast → `NOT_EXPORTED` |
| `private val receiver = DynamicBroadcastReceiver()` | Ek hi instance register aur unregister dono mein use hota hai (warna mismatch crash) |
| **Intent Extras** | Broadcast sirf action string nahi, extra data bhi laata hai — `intent?.getBooleanExtra("state", false)` se Airplane Mode ka naya state milta hai |
| `ConnectivityManager.CONNECTIVITY_ACTION` deprecated (API 28) | Modern alternative: `ConnectivityManager.NetworkCallback` (`registerNetworkCallback`) — battery-efficient, specific changes ke liye |

### Result

Dynamic Receiver **successfully** `AIRPLANE_MODE_CHANGED` system broadcast sunta hai (Static jo nahi sun paaya) — kyunki dynamic registration Android 8+ ki implicit-broadcast restriction se **bahar** hai.

---

## Static vs Dynamic — Side by Side

| | Static | Dynamic |
|---|---|---|
| Registration | `AndroidManifest.xml` | `registerReceiver()` code mein |
| Custom broadcast (apni app) | ✅ Kaam karta hai | ✅ Kaam karta hai |
| System broadcast (jaise Airplane Mode) | ❌ Nahi (Android 8+, sirf exempted list) | ✅ Haan |
| Activity band hone ke baad bhi sunta hai? | ✅ Haan (Manifest-level, Activity se independent) | ❌ Nahi (sirf jab tak registered component zinda hai) |
| `exported` flag ka matlab | Manifest attribute `android:exported` | Runtime flag `RECEIVER_EXPORTED`/`NOT_EXPORTED` (API 33+) |

---

## Common Interview Questions

**Q: BroadcastReceiver kya hai?**
> Ek component jo system-wide events (broadcasts) sunta hai — Android khud bheje (battery low, connectivity change) ya koi app khud bheje (custom action). Do tarah register hota hai: Manifest mein (static) ya code mein (dynamic).

**Q: Static aur Dynamic receiver mein kya farak hai?**
> Static Manifest mein declare hota hai, Activity ke bina bhi active rehta hai, lekin Android 8+ se zyada tar system broadcasts isko nahi milte. Dynamic code mein register/unregister hota hai (jaise `onStart`/`onStop`), sirf jab tak component zinda hai sunta hai, lekin koi broadcast restriction nahi.

**Q: Android 8+ mein static receivers ko system broadcasts kyun restrict kiya gaya?**
> Battery aur performance ki wajah se — pehle har implicit system broadcast (jaise connectivity change) sabhi apps ko wake kar deta tha jinhone Manifest mein receiver declare kiya tha, chahe app use mein ho ya nahi. Isse battery drain hoti thi. Ab sirf kuch zaroori broadcasts (`BOOT_COMPLETED`, `SMS_RECEIVED`) exempt hain, baaki ke liye dynamic registration chahiye.

**Q: Explicit vs implicit intent broadcast mein kya farak hai?**
> Implicit: `Intent("action_string")` — sirf action bhejte hain, Android decide karta hai kaun sunega. Explicit: `.setClass(context, Receiver::class.java)` se target receiver seedha bata dete hain. Explicit zyada secure hai aur "non-exported component" lint warning se bachata hai.

**Q: Koi app khud `CONNECTIVITY_CHANGE` jaisa system broadcast bhej sakti hai?**
> Nahi — yeh **protected broadcast** hai, sirf Android system khud bhej sakta hai. Koi bhi third-party app `sendBroadcast()` se try kare toh `SecurityException: Permission Denial` crash aata hai.

**Q: `RECEIVER_EXPORTED` aur `RECEIVER_NOT_EXPORTED` kab use karte hain (API 33+)?**
> Agar receiver sirf apni app ke andar ke broadcasts sunega, `RECEIVER_NOT_EXPORTED`. Agar system ya doosri app ka broadcast sunna hai, `RECEIVER_EXPORTED` — warna broadcast receiver tak pahunchta hi nahi.

---

## Interview Mein Bolna

> *"BroadcastReceiver Android ka woh component hai jo system-wide events sunta hai — chahe woh Android khud bheje (connectivity change, battery low) ya koi app khud. Do tarah register hota hai: static (Manifest mein) aur dynamic (code mein, `registerReceiver`/`unregisterReceiver` se). Static receiver Activity ki lifetime se independent rehta hai, lekin Android 8+ ke baad zyada tar system broadcasts static receivers ko nahi milte — battery optimization ke liye. Isliye real-time system events (jaise connectivity, airplane mode) sunne ke liye dynamic registration use karte hain, jo Activity ke `onStart`/`onStop` se bandha hota hai. API 33+ pe dynamic register karte waqt batana padta hai ki broadcast apni app se aa raha hai ya system se (`NOT_EXPORTED` vs `EXPORTED`)."*
