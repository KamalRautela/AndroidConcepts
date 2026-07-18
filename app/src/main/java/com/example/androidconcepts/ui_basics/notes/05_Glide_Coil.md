# Image Loading — Glide & Coil

## Kyun chahiye image loading library?

URL se image load karna manually karo toh:
- Background thread mein download karna padega (main thread block nahi karna)
- Caching manually handle karna padega
- Memory management khud karna padega
- Error/placeholder states karna padega

Glide aur Coil ye sab automatically karte hain.

---

## Glide

Google ka maintained library. Java-based, industry standard — bahut projects mein milega.

**Dependency:**
```toml
# libs.versions.toml
[versions]
glide = "4.16.0"

[libraries]
glide = { group = "com.github.bumptech.glide", name = "glide", version.ref = "glide" }
```
```kotlin
// build.gradle.kts
implementation(libs.glide)
```

**Basic use:**
```kotlin
Glide.with(this)          // context
    .load(imageUrl)       // URL, drawable, File, resource ID — sab chalta hai
    .placeholder(R.drawable.ic_loading)     // load hone tak
    .error(R.drawable.ic_error)             // fail hone pe
    .into(binding.imgView)                  // target ImageView
```

**Transforms:**
```kotlin
Glide.with(this).load(url).circleCrop().into(imgView)      // circular crop
Glide.with(this).load(url).centerCrop().into(imgView)      // fill + crop
Glide.with(this).load(url).fitCenter().into(imgView)       // fit without crop
```

---

## Coil (Coroutine Image Loader)

Kotlin-first library. Coroutines pe built — modern Kotlin projects mein prefer karo.

**Dependency:**
```toml
# libs.versions.toml
[versions]
coil = "2.7.0"

[libraries]
coil = { group = "io.coil-kt", name = "coil", version.ref = "coil" }
```
```kotlin
// build.gradle.kts
implementation(libs.coil)
```

**Basic use:**
```kotlin
import coil.load

binding.imgView.load(imageUrl) {
    placeholder(R.drawable.ic_loading)
    error(R.drawable.ic_error)
}
```

**Coil mein transforms:**
```kotlin
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation

binding.imgView.load(url) {
    transformations(CircleCropTransformation())
    transformations(RoundedCornersTransformation(16f))
}
```

---

## Glide vs Coil Comparison

| Feature | Glide | Coil |
|---|---|---|
| Language | Java-based | Kotlin-first |
| API style | `.with().load().into()` | `imageView.load()` extension |
| Coroutines | Nahi (thread-based) | Haan — built-in |
| Size | Bigger | Lighter |
| Maturity | Battle-tested (2014+) | Modern (2020+) |
| Best for | Legacy/Java projects | New Kotlin projects |

**Rule of thumb:** New project → Coil. Existing Java/Glide project → Glide rakho.

---

## Placeholder vs Error

```kotlin
// Glide
Glide.with(this)
    .load(url)
    .placeholder(R.drawable.ic_loading)   // image load hone tak dikhta hai
    .error(R.drawable.ic_broken)          // URL invalid ya load fail ho toh
    .into(imgView)

// Coil
imgView.load(url) {
    placeholder(R.drawable.ic_loading)
    error(R.drawable.ic_broken)
}
```

---

## Common Patterns

**Local resource load karna:**
```kotlin
Glide.with(this).load(R.drawable.my_image).into(imgView)
imgView.load(R.drawable.my_image)  // Coil
```

**Picasso se comparison (older library):**
```kotlin
// Picasso (older, avoid in new projects)
Picasso.get().load(url).into(imgView)
```
Picasso ab mostly Glide aur Coil ne replace kar diya hai.

---

## Internet Permission

`AndroidManifest.xml` mein zaroor add karo:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
Bina iske koi bhi URL load nahi hoga.

---

## Interview Mein Bolna

> *"Image loading ke liye Glide ya Coil use karte hain — ye background thread pe download karte hain, cache manage karte hain, aur placeholder/error states handle karte hain. Glide Java-based hai aur industry standard — purane projects mein milega. Coil Kotlin-first hai, coroutines pe built hai, aur new projects ke liye prefer kiya jaata hai. Syntax mein difference hai: Glide mein `Glide.with(context).load(url).into(imageView)` aur Coil mein `imageView.load(url)` — extension function hai. Transforms jaise circleCrop dono mein available hain."*
