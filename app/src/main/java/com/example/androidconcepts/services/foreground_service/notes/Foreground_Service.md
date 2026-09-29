# Foreground Service — Notes

Started Service jo notification dikhati hai. Demo: Start/Stop buttons, service 10 fake tasks chalati hai, progress notification mein dikhta hai ("Downloading 4/10").

---

## Kya Hai

**Foreground Service** = Started Service + **notification zaroori**. Notification dikhne ki wajah se system ise "user ko pata hai yeh chal rahi hai" maanta hai, isliye background restrictions se bhi zyada chhoot milti hai aur asaani se kill nahi hoti. Music player, navigation, active download isi se chalte hain.

## Started Se Kya Naya

| Cheez | Started Service | Foreground Service |
|---|---|---|
| Start | `startService()` | `startForegroundService()` (API 26+; usse pehle `startService()`) |
| Service ke andar | Kuch extra nahi | 5 second ke andar `startForeground(id, notification, type)` zaroori, warna crash |
| Notification | Nahi | Zaroori, channel ke saath (API 26+) |
| Manifest | Sirf `<service>` | + `foregroundServiceType` + uski permission |
| Runtime permission | Nahi | `POST_NOTIFICATIONS` (Android 13+) |

---

## Code — Poori Chain

### 1. `ForegroundService.kt` — channel + notification + startForeground
```kotlin
class ForegroundService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        } else {
            0
        }
        ServiceCompat.startForeground(this, NOTIFICATION_ID, buildNotification(0), serviceType)

        serviceScope.launch {
            for (i in 1..TOTAL_TASKS) {
                delay(1000)
                Log.d(TAG, "Task $i/$TOTAL_TASKS done")
                getSystemService(NotificationManager::class.java)
                    .notify(NOTIFICATION_ID, buildNotification(i))
            }
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf(startId)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Foreground Demo", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun buildNotification(progress: Int): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Downloading $progress/$TOTAL_TASKS")
            .setProgress(TOTAL_TASKS, progress, false)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
    }

    companion object {
        private const val CHANNEL_ID = "FOREGROUND SERVICE CHANNEL"
        private const val NOTIFICATION_ID = 1
        private const val TOTAL_TASKS = 10
    }
}
```

### 2. `ForegroundServiceActivity.kt` — permission + start
```kotlin
private val notificationPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
) { isGranted ->
    Log.d(TAG, "Permission granted = $isGranted")
}

binding.btnStart.setOnClickListener {
    val hasPermission = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED

    if (hasPermission) {
        startForegroundServiceNow()
    } else {
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}

private fun startForegroundServiceNow() {
    val serviceIntent = Intent(this, ForegroundService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(serviceIntent)
    } else {
        startService(serviceIntent)
    }
}
```

**Zaroori design choice:** Permission check **pehle**, service start **baad mein** — sirf tab jab permission already ho. Agar permission nahi hai, **sirf popup dikhta hai, service bilkul start nahi hoti** (pehle jo galat version tha usme dono ek saath chal jaate the — service turant start ho jaati thi bina popup ka result jaane, jisse race condition banta tha aur pehli 1-2 notification updates miss ho sakti thi). Is version mein user ko permission decide karne ke baad **dubara Start dabana** padta hai.

### 3. `AndroidManifest.xml`
```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<service
    android:name=".services.foreground_service.ForegroundService"
    android:foregroundServiceType="dataSync"
    android:exported="false" />
```

---

## Key Concepts

| Concept | Detail |
|---|---|
| `NotificationChannel` | Android 8+ (API 26) mein har notification kisi channel ke andar hoti hai, warna dikhti hi nahi. `IMPORTANCE_LOW` = notification aaye lekin awaaz/vibration nahi (baar-baar progress update ke liye sahi) |
| `setProgress(max, current, false)` | Progress bar dikhata hai. `false` = determinate (asli %), `true` = ghoomta hua indeterminate bar |
| `setOnlyAlertOnce(true)` | Update hone pe dobara alert (sound/vibrate) nahi |
| Same `NOTIFICATION_ID` pe `notify()` | Nayi notification nahi banti, **purani update hoti hai** — progress bar isi se badhta hai |
| `startForeground()` 5 second ke andar | Na bulaya toh crash: `ForegroundServiceDidNotStartInTimeException` |
| `foregroundServiceType` (Manifest) aur Kotlin ka type | Dono **match** hone chahiye (`dataSync` ↔ `FOREGROUND_SERVICE_TYPE_DATA_SYNC`). Android 14+ pe zaroori |
| `stopForeground(STOP_FOREGROUND_REMOVE)` | Service ko foreground se hataata hai **aur notification bhi hata deta hai** |
| `POST_NOTIFICATIONS` | Android 13+ runtime permission. Deny ho toh service phir bhi chalti hai, bas notification nahi dikhti |

---

## minSdk Ki Wajah Se 2 API-Level Fixes Lage

Humara `minSdk = 24` hai, lekin Foreground Service ke features baad ke API levels mein aaye — dono jagah version-check zaroori tha:

```kotlin
// FOREGROUND_SERVICE_TYPE_DATA_SYNC — API 29 (Q) mein aaya
val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
} else { 0 }

// startForegroundService() — API 26 (O) mein aaya
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    startForegroundService(serviceIntent)
} else {
    startService(serviceIntent)
}
```
API 26 se pehle Foreground Service ka concept hi nahi tha (background restrictions thi hi nahi), isliye seedha `startService()` kaafi tha.

---

## Common Interview Questions

**Q: Foreground Service kya hai, Started Service se kaise alag?**
> Started Service jo notification dikhati hai. Notification hone se system ise zyada priority deta hai aur asaani se kill nahi karta. Music, navigation, active download jaise user-visible lambe kaam ke liye use hoti hai.

**Q: `startForeground()` na bulane pe kya hota hai?**
> `startForegroundService()` ke 5 second ke andar service ke `onStartCommand`/`onCreate` mein `startForeground()` bulana zaroori hai. Na bulaya toh system `ForegroundServiceDidNotStartInTimeException` de kar app crash karta hai.

**Q: `foregroundServiceType` kyun chahiye (Android 14+)?**
> System ko batana zaroori hai ki service kis kaam ke liye foreground mein hai (`dataSync`, `mediaPlayback`, `location`, etc). Har type ki apni permission hoti hai. Bina declare kiye Android 14+ pe crash hota hai.

**Q: `POST_NOTIFICATIONS` permission deny ho jaye toh service kya karegi?**
> Depends kaise likha hai. Agar permission-check aur service-start ek saath (bina wait kiye) chalaye jayein, service chalti rahegi (coroutine, logs) — sirf notification nahi dikhegi. Better design mein (jo humne banaya) permission decide hone tak service start hi nahi hoti — user ko permission dene/na dene ke baad dubara try karna padta hai, taaki race condition na ho.

---

## Interview Mein Bolna

> *"Foreground Service ek Started Service hai jo notification dikhati hai, isliye system use asaani se kill nahi karta aur background restrictions bhi kam lagti hain. `startForegroundService()` call karke service start karte hain, aur service ke andar 5 second ke andar `startForeground(id, notification, type)` bulana zaroori hai, warna crash hota hai. Android 14+ pe Manifest mein `foregroundServiceType` declare karna aur uski permission dena zaroori hai. Android 13+ pe notification dikhane ke liye `POST_NOTIFICATIONS` runtime permission chahiye — deny hone pe service chalti hai, bas notification nahi dikhti."*
