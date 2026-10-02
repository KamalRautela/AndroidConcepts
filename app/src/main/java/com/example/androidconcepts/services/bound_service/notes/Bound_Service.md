# Bound Service — Notes

Activity jo Service ke saath "connect" hoti hai aur uske methods seedha call kar sakti hai — client-server jaisa. Demo: Bind/Unbind/Increment/Get Count buttons, ek counter Service.

---

## Kya Hai

Started Service mein Activity sirf **"start karo"** ya **"band karo"** bol sakti thi — Service ke andar kya chal raha hai, uska koi access nahi tha. **Bound Service** mein Activity Service se **bind (connect)** hoti hai, aur uske baad Service ke **normal functions seedha call** kar sakti hai, jaise kisi bhi normal class ka method.

## Core Concepts

| Concept | Kaam |
|---|---|
| `Binder` | Service khud apne andar banati hai — ek "handle" jo Service ka reference deta hai |
| `onBind()` | Ab `null` nahi, **asli Binder object** return karta hai |
| `ServiceConnection` | Activity ke andar — `onServiceConnected` (Binder milta hai), `onServiceDisconnected` (connection achanak toota) |
| `bindService(intent, connection, BIND_AUTO_CREATE)` | `BIND_AUTO_CREATE` — agar Service chal nahi rahi, khud bana ke bind kar do |
| `unbindService(connection)` | Connection todta hai. **Saare** clients unbind ho jaayein tabhi Service `onDestroy` |

## Lifecycle

```
Activity: bindService(intent, connection, BIND_AUTO_CREATE)
Service:  onCreate()              ← sirf pehli baar
          onBind()                ← Binder return, sirf pehle bind pe
Activity: onServiceConnected()    ← Binder mila, Service object store
          (Activity jitni baar chahe Service ke methods seedha call karti hai)
Activity: unbindService(connection)
Service:  onUnbind()              ← AAKHRI client ke unbind hone par
Service:  onDestroy()
```

---

## Code — Poori Chain

### 1. `BoundService.kt`
```kotlin
class BoundService : Service() {

    inner class LocalBinder : Binder() {
        fun getService() = this@BoundService
    }

    private val binder = LocalBinder()

    override fun onBind(intent: Intent?): IBinder = binder

    private var count = 0

    fun incrementCount() {
        count++
    }

    fun getCount() = count

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    companion object {
        private const val TAG = "BOUND SERVICE"
    }
}
```

### 2. `BoundServiceActivity.kt` — `ServiceConnection` + buttons
```kotlin
private var boundService: BoundService? = null
private var isBound = false

private val connection = object : ServiceConnection {
    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        val binder = p1 as BoundService.LocalBinder
        boundService = binder.getService()
        isBound = true
        binding.tvStatus.text = "Connected"
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        boundService = null
        isBound = false
        binding.tvStatus.text = "Disconnected"
    }
}

binding.btnBind.setOnClickListener {
    bindService(Intent(this, BoundService::class.java), connection, Context.BIND_AUTO_CREATE)
}

binding.btnIncrement.setOnClickListener {
    if (isBound) boundService?.incrementCount() else binding.tvStatus.text = "Pehle Bind karo!"
}

binding.btnGetCount.setOnClickListener {
    if (isBound) binding.tvStatus.text = "Count: ${boundService?.getCount()}"
    else binding.tvStatus.text = "Pehle Bind karo!"
}

binding.btnUnbind.setOnClickListener {
    if (isBound) {
        unbindService(connection)
        isBound = false
    }
}

override fun onDestroy() {
    if (isBound) {
        unbindService(connection)
        isBound = false
    }
    super.onDestroy()
}
```

### 3. `AndroidManifest.xml`
```xml
<service
    android:name=".services.bound_service.BoundService"
    android:exported="false" />
```

---

## Key Concepts

| Concept | Detail |
|---|---|
| `inner class LocalBinder : Binder()` | `inner` isliye kyunki isko outer Service class ka reference chahiye (`this@BoundService`) |
| `onServiceConnected` mein cast | `binderObj` generic `IBinder?` type mein aata hai, `as BoundService.LocalBinder` se cast karke asli type milta hai |
| `isBound` flag | Crash se bachata hai — bina Bind kiye Increment/GetCount dabane pe `boundService` null hoga, flag se graceful message dikha dete hain |
| `onDestroy()` mein safety unbind | Agar Activity bound state mein hi destroy ho jaaye (rotate, back press) bina Unbind dabaye, `ServiceConnection` leak ho sakta hai — isliye safety-net hamesha rakhte hain |
| `onUnbind()` | Naya lifecycle method (Started/Foreground mein nahi tha) — sirf **aakhri** client ke unbind hone par chalta hai |

---

## Teeno Services Ka Lifecycle — Ek Nazar Mein

| | Trigger | Beech ka kaam | Band hone ka trigger |
|---|---|---|---|
| **Started** | `startService()` | Coroutine, khud khatam | `stopSelf()` khud se |
| **Foreground** | `startForegroundService()` | Coroutine + notification | `stopForeground` + `stopSelf()` |
| **Bound** | `bindService()` | Activity seedha methods call karti hai | **Saare** clients `unbind` karein tabhi |

Teeno mein `onCreate()` hamesha sirf ek baar, `onDestroy()` hamesha aakhri mein — beech ka hissa Activity ke relationship ke hisaab se alag hai.

---

## Common Interview Questions

**Q: Bound Service, Started Service se kaise alag hai?**
> Started Service mein Activity sirf start/stop bol sakti hai, Service ke andar access nahi hota. Bound Service mein Activity `bindService()` se connect hoti hai aur Service ke public methods seedha call kar sakti hai — ek `Binder` object ke through jo Service ka reference deta hai.

**Q: `Binder` kya kaam karta hai?**
> Service khud ek chhota `Binder` object banati hai (inner class), jiska kaam sirf itna hai — "poori Service ka reference de do". Activity ko yeh Binder `onBind()` se milta hai, aur usse Service ka asli object nikaal ke uske methods call kar sakti hai.

**Q: `ServiceConnection` ka kaam kya hai?**
> Activity ki taraf se ek interface implement karna padta hai jo batata hai connection ban gaya (`onServiceConnected`, Binder milta hai) ya toot gaya (`onServiceDisconnected`, jaise Service crash ho jaaye).

**Q: Bound Service kab band hoti hai?**
> Jab **saare** bound clients `unbindService()` kar dein. Ek Service ko multiple Activities/Fragments bind kar sakte hain — jab tak koi bhi bound hai, Service zinda rehti hai.

**Q: `onDestroy()` mein unbind check kyun zaroori hai?**
> Agar Activity bound state mein destroy ho jaaye bina Unbind dabaye (rotate, back press), `ServiceConnection` ka reference latka reh jaata hai — Android "leaked" warning deta hai. `onDestroy()` mein safety-net rakhna best practice hai.

---

## Interview Mein Bolna

> *"Bound Service Activity ko Service ke methods seedha call karne deti hai, client-server jaisa relationship. Service ek `Binder` object banati hai jo khud ka reference deta hai, aur `onBind()` se yeh Binder Activity ko milta hai. Activity apni taraf se `ServiceConnection` implement karti hai jo connect/disconnect track karta hai. Jab tak koi bhi client bound hai, Service zinda rehti hai — sab unbind hote hi `onDestroy` chalta hai. Memory leak se bachne ke liye `onDestroy()` mein hamesha safety-unbind rakhte hain."*
