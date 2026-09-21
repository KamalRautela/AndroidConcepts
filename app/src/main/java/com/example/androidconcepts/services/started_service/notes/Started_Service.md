# Started Service — Notes

Bina UI ke background mein kaam karne wala Android component. Demo: Start/Stop buttons, service 10 fake tasks (har second ek) chalati hai, output Logcat mein (tag `STARTED SERVICE`).

---

## Kya Hai

**Service** — ek component jo bina UI ke background mein kaam karta hai. **Started Service** `startService()` se start hoti hai aur tab tak chalti rehti hai jab tak `stopSelf()` / `stopService()` na aaye. Activity se koi connection nahi rehta.

**Sabse badi galatfehmi:** Service apne aap alag thread pe **nahi** chalti. Woh **main thread** pe chalti hai. Heavy kaam ke liye khud coroutine/thread use karna padta hai.

## Teen Types (Overview)

| Type | Start kaise | Kab use |
|---|---|---|
| Started | `startService()` | One-time kaam (par aajkal kam use, neeche dekho) |
| Foreground | `startForegroundService()` + notification | User ko dikhne wala lamba kaam (music, navigation) |
| Bound | `bindService()` | Activity ko service se baat karni ho (client-server jaisa) |

---

## Lifecycle

```
Started:  onCreate() → onStartCommand() → (kaam chalta hai) → onDestroy()
```

- `onCreate()` — **sirf pehli baar**, jab service ban rahi ho
- `onStartCommand()` — **har `startService()` call pe** (isliye 2 baar Start dabane pe `onCreate` ek baar, `onStartCommand` do baar)
- `onDestroy()` — service band hone pe (cleanup yahan, jaise coroutine cancel)

---

## Code — Poori Chain

### 1. `StartedService.kt`
```kotlin
class StartedService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate called")
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand called startId = $startId")
        serviceScope.launch {
            for (i in 1..10) {
                delay(1000)
                Log.d(TAG, "Task $i/10 done")
            }
            stopSelf(startId)
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        serviceScope.cancel()
        Log.d(TAG, "onDestroy Called")
        super.onDestroy()
    }

    companion object {
        private const val TAG = "STARTED SERVICE"
    }
}
```

### 2. `StartedServiceActivity.kt` — Start/Stop
```kotlin
private fun bindUi() {
    val serviceIntent = Intent(this@StartedServiceActivity, StartedService::class.java)

    binding.btnStart.setOnClickListener { startService(serviceIntent) }
    binding.btnStop.setOnClickListener { stopService(serviceIntent) }
}
```

### 3. `AndroidManifest.xml` — register zaroori
```xml
<service
    android:name=".services.started_service.StartedService"
    android:exported="false" />
```
Bina register kiye `startService()` error nahi deta, service bas start nahi hoti (Logcat mein sirf warning).

---

## Key Concepts

| Concept | Detail |
|---|---|
| `serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)` | Service ke paas `viewModelScope` jaisa built-in scope nahi hota, isliye khud banate hain. `Default` = background thread, taaki main thread block na ho |
| `serviceScope.cancel()` in `onDestroy` | Service marne pe coroutine bhi rukna chahiye, warna leak (kaam chalta rahega) |
| `onBind()` returns `null` | Started Service mein koi bind nahi karta |
| `Intent(this, StartedService::class.java)` | Explicit intent, bas target Service hai |
| `stopSelf()` vs `stopService()` | `stopSelf()` service ke andar se, `stopService()` bahar (Activity) se |

---

## Bug Jo Humne Pakda: `stopSelf()` vs `stopSelf(startId)`

Start ko 2 baar jaldi dabane pe `onStartCommand` do baar chalta hai (`startId = 1, 2`), aur dono ke coroutines saath chalte hain.

**Galat (`stopSelf()`):**
```
Task 1/5 ... Task 4/5   ← har task DO baar
Task 5/5 done           ← sirf EK baar!
onDestroy
```
Pehle coroutine ka `stopSelf()` poori service band kar deta hai, `onDestroy` mein `cancel()` doosre coroutine ko uske `Task 5/5` se pehle kaat deta hai. Kaam adhoora.

**Sahi (`stopSelf(startId)`):**
```
Task 5/5 done
Task 5/5 done   ← dono ka 5/5 aaya
onDestroy       ← ek hi baar, dono ke baad
```
`stopSelf(startId)` service tab hi band karta hai jab woh `startId` **sabse latest start ki ho**. Pehle wale ka `stopSelf(1)` ignore hota hai (latest `2` hai), doosre wale ka `stopSelf(2)` service band karta hai.

---

## `onStartCommand` Ka Return Value

Jab system service ko **maar** de (low memory, process kill) tab kya ho:

| Return value | Kya hota hai |
|---|---|
| `START_NOT_STICKY` | Dobara start **nahi** karta (one-time kaam jise user khud dobara trigger karega) |
| `START_STICKY` | Dobara start karta hai, lekin `intent = null` ke saath (music player jaisa kaam) |
| `START_REDELIVER_INTENT` | Dobara start karta hai, **aakhri intent ke saath** (jaise download jise URL chahiye) |

**Zaroori:** Restart ka matlab **naye sire se shuru**. Service marne pe saari state (variables, coroutines) khatam. Progress save karna (DB/DataStore) apni zimmedari hai. Isi liye aisa guaranteed kaam aajkal WorkManager se hota hai.

### Recents se swipe ≠ service kill
Humne test kiya: Task 5 pe app recents se swipe kiya, phir bhi Task 6 se 10 chale, `onDestroy` aaya, aur uske baad process end hua. Swipe sirf Activity/task hatata hai, process aur service zinda rehte hain (`stopWithTask` ka default `false` hai). Kuch OEM phones (Xiaomi, Oppo) swipe pe process maar dete hain, isliye behavior alag alag hota hai. Service tab marti hai jab **system process maare** (low memory) ya user **Force Stop** kare. Android Studio ka Stop button bhi Force Stop hai, uske baad `STICKY` bhi restart nahi hoti. Reliable test: home dabao, phir `adb shell am kill com.example.androidconcepts`.

---

## Aajkal Kya Use Hota Hai

Android 8+ ne background execution limits lagayi (Doze, App Standby), Android 12+ mein background se Foreground Service start karna bhi restricted hai. Isliye plain Started Service naye code mein lagbhag nahi likhte.

| Kaam | Kya use karein |
|---|---|
| Deferrable + guaranteed (sync, upload, periodic refresh). App kill/reboot ke baad bhi | **WorkManager** |
| User ko dikhne wala lamba kaam (music, navigation, active download) | **Foreground Service** |
| Screen ke zinda rehte tak async kaam | **Coroutines** (`viewModelScope`) |
| Exact time alarm | **AlarmManager** |
| Activity ko service se baat karni ho | **Bound Service** |

---

## Common Interview Questions

**Q: Service alag thread pe chalti hai?**
> Nahi. Service main thread pe chalti hai. Heavy kaam ke liye coroutine ya thread khud banana padta hai (jaise humne `serviceScope` banaya `Dispatchers.Default` ke saath).

**Q: `onCreate` aur `onStartCommand` mein kya fark hai?**
> `onCreate` sirf ek baar chalta hai jab service ban rahi ho. `onStartCommand` har `startService()` call pe chalta hai. Isliye 2 baar start karo toh `onCreate` ek baar, `onStartCommand` do baar aata hai.

**Q: `stopSelf()` aur `stopSelf(startId)` mein fark?**
> `stopSelf()` service ko turant band kar deta hai chahe aur start request pending ho. `stopSelf(startId)` tab hi band karta hai jab woh latest request ho, isliye multiple `startService()` calls mein kaam adhoora nahi katta.

**Q: `START_STICKY` aur `START_NOT_STICKY` mein fark?**
> System service ko kill kare toh `STICKY` dobara start karta hai (intent null ke saath), `NOT_STICKY` nahi karta. `REDELIVER_INTENT` dobara start karta hai aakhri intent ke saath. Teeno mein service ki purani state khatam hoti hai, progress khud save karna padta hai.

**Q: Service aur WorkManager mein kab kya?**
> Guaranteed, deferrable kaam (jo app kill ya reboot ke baad bhi hona chahiye) ke liye WorkManager. User ko dikhne wale lambe, abhi chalne wale kaam ke liye Foreground Service. In-app async ke liye Coroutines.

---

## Interview Mein Bolna

> *"Service ek component hai jo bina UI ke background kaam karta hai, lekin apne aap alag thread pe nahi chalta, main thread pe hi chalta hai, isliye kaam coroutine mein karte hain. Started Service `startService()` se start hoti hai aur `stopSelf()` ya `stopService()` tak chalti hai. Multiple start requests mein `stopSelf(startId)` use karte hain taaki latest request ke khatam hone pe hi service band ho. `onStartCommand` ka return value (`START_STICKY`, `START_NOT_STICKY`, `START_REDELIVER_INTENT`) batata hai ki system kill kare toh service dobara start ho ya nahi. Aajkal background kaam ke liye zyadatar WorkManager use hota hai, aur user ko dikhne wale lambe kaam ke liye Foreground Service."*
