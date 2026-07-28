# Permissions

## Kya Hai
Android mein sensitive features use karne ke liye permissions chahiye. Teen types hain — Normal (auto-grant), Dangerous (runtime dialog), Special (Settings screen). Dangerous permissions runtime pe user se maangni padti hain.

---

## Kaise Kaam Karta Hai

```
Button click → checkSelfPermission()
                    ↓
            GRANTED → seedha kaam karo
                    ↓
            DENIED → launcher.launch()
                    ↓
            System dialog dikhta hai
                    ↓
        Allow → isGranted = true → kaam karo
        Deny  → isGranted = false → handleDenied()
                    ↓
            shouldShowRequestPermissionRationale()
                    ↓
            true  → Rationale dikhao, dobara maango
            false → Settings pe bhejo (permanently denied)
```

---

## 3 Permission Types

| Type | Grant Kaise | Example |
|---|---|---|
| **Normal** | Install pe auto-grant | INTERNET, VIBRATE |
| **Dangerous** | Runtime — user dialog | CAMERA, LOCATION, CONTACTS |
| **Special** | Settings screen pe manually | SYSTEM_ALERT_WINDOW, MANAGE_ALL_FILES |

> Sabko `AndroidManifest.xml` mein declare karna zaroori hai — chahe Normal ho ya Dangerous.

```xml
<uses-permission android:name="android.permission.INTERNET" />       <!-- Normal -->
<uses-permission android:name="android.permission.CAMERA" />         <!-- Dangerous -->
```

---

## Important Methods / Code

### ActivityResultLauncher — Modern Way

```kotlin
// Step 1: Launcher register karo (onCreate se pehle)
private val permissionLauncher =
    registerForActivityResult(RequestPermission()) { isGranted ->
        if (isGranted) {
            openCamera()
        } else {
            handleDenied()
        }
    }

// Step 2: Check karke launch karo
private fun checkAndRequest() {
    when {
        checkSelfPermission(Manifest.permission.CAMERA) == PERMISSION_GRANTED -> {
            openCamera()
        }
        shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
            showRationale()
        }
        else -> {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}
```

### Denied Handling

```kotlin
private fun handleDenied() {
    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
        // temporarily denied — rationale dikhao
        showRationaleDialog()
    } else {
        // permanently denied — Settings pe bhejo
        showSettingsDialog()
    }
}

private fun showSettingsDialog() {
    AlertDialog.Builder(this)
        .setTitle("Permission Required")
        .setMessage("Settings mein jaake manually enable karo")
        .setPositiveButton("Settings") { _, _ ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
        }
        .show()
}
```

### Multiple Permissions

```kotlin
private val multiLauncher =
    registerForActivityResult(RequestMultiplePermissions()) { results ->
        val cameraGranted = results[Manifest.permission.CAMERA] == true
        val micGranted = results[Manifest.permission.RECORD_AUDIO] == true
    }

multiLauncher.launch(arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.RECORD_AUDIO
))
```

---

## shouldShowRequestPermissionRationale() — Truth Table

| State | Return Value | Kya Karo |
|---|---|---|
| Pehli baar request | `false` | Seedha request karo |
| Ek baar deny kiya | `true` | Rationale dikhao |
| "Don't ask again" | `false` | Settings pe bhejo |
| Already granted | `false` | Kuch mat karo |

> Pehli baar aur permanently denied dono mein `false` aata hai — isliye **launcher ka result aane ke baad** call karo, pehle nahi.

---

## Common Interview Questions

**Q: Normal aur Dangerous permission mein fark?**
> Normal permission automatically install pe grant ho jaati hai — user se nahi poocha jaata. Dangerous permission runtime pe user se dialog ke zariye maangni padti hai — camera, location, contacts.

**Q: `shouldShowRequestPermissionRationale()` kab `true` return karta hai?**
> Sirf jab user ne pehle ek baar deny kiya ho. Pehli baar request pe false, permanently denied pe bhi false. Isliye launcher result mein call karo — tab distinguish kar sakte hain temporarily vs permanently denied.

**Q: `registerForActivityResult` vs `requestPermissions` mein fark?**
> `requestPermissions` deprecated hai. `registerForActivityResult` modern way hai — lifecycle aware hai, activity recreate pe bhi kaam karta hai, memory leak nahi hota.

---

## Gotchas — Common Mistakes

**1. Manifest mein declare bhoolna**
```kotlin
// Code mein launch kiya lekin Manifest mein nahi hai
permissionLauncher.launch(Manifest.permission.CAMERA)
// → SecurityException crash

// SAHI — pehle Manifest mein daalo
// <uses-permission android:name="android.permission.CAMERA" />
```

**2. Permanently denied aur temporarily denied same treat karna**
```kotlin
// GALAT — permanently denied pe bhi dialog dikhayenge, kaam nahi karega
private fun handleDenied() {
    showRationaleDialog()  // koi fark nahi kiya
}

// SAHI — shouldShowRationale() se distinguish karo
private fun handleDenied() {
    if (shouldShowRequestPermissionRationale(CAMERA)) {
        showRationaleDialog()
    } else {
        showSettingsDialog()
    }
}
```

**3. Launcher ko onCreate ke andar register karna**
```kotlin
// GALAT — crash hoga
override fun onCreate(...) {
    val launcher = registerForActivityResult(...) { }  // nahi chalega
}

// SAHI — class level pe declare karo
private val launcher = registerForActivityResult(...) { }

override fun onCreate(...) {
    launcher.launch(...)  // yahan use karo
}
```
