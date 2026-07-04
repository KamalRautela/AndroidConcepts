# AndroidConcepts — Project Structure Notes

---

## 1. `settings.gradle.kts`

Gradle sabse pehle yahi file padhta hai — **project ka entry point**.

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "AndroidConcepts"
include(":app")
```

- `pluginManagement` — Gradle plugins kahan se download karein
- `dependencyResolutionManagement` — Libraries kahan se download karein (`google()` + `mavenCentral()`)
- `FAIL_ON_PROJECT_REPOS` — Sirf yahan defined repos use ho, modules apne nahi bana sakte
- `rootProject.name` — Project ka naam
- `include(":app")` — Ek module hai `app` naam ka

---

## 2. `gradle.properties`

**Gradle ke global settings** — poore project pe apply hoti hai.

```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
kotlin.code.style=official
```

- `-Xmx2048m` — Gradle daemon ko 2GB RAM milegi (bade projects mein `4096m` karte hain)
- `-Dfile.encoding=UTF-8` — Special characters sahi handle hon
- `kotlin.code.style=official` — Kotlin official code style follow karo

---

## 3. `gradle/libs.versions.toml`

**Version Catalog** — ek jagah saari dependencies ke versions manage karo.

```toml
[versions]
agp = "9.2.1"
kotlin = "2.1.20"
recyclerView = "1.4.0"
...

[libraries]
androidx-recyclerview = { group = "androidx.recyclerview", name = "recyclerview", version.ref = "recyclerView" }
...

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
```

- `[versions]` — Sirf version numbers yahan
- `[libraries]` — Actual dependencies — `group` + `name` + `version.ref`
- `[plugins]` — Gradle plugins
- Code mein `libs.androidx.recyclerview` likhte ho — yahan se resolve hota hai
- **Fayda:** Version ek jagah change karo — saari jagah update

---

## 4. `build.gradle.kts` (Root)

**Root level** file — poore project ka.

```kotlin
plugins {
    alias(libs.plugins.android.application) apply false
}
```

- Plugins yahan **register** hoti hain (download hongi) lekin **apply nahi** hoti
- `apply false` — Actual apply `app/build.gradle.kts` mein hogi
- **Kyun?** Multi-module projects mein version ek jagah rakho, clash nahi hoga

---

## 5. `app/build.gradle.kts`

**App module ka main build file** — sabse important gradle file.

```kotlin
plugins {
    alias(libs.plugins.android.application)  // apply ho raha hai
}

android {
    namespace = "com.example.androidconcepts"
    compileSdk 36       // latest Android pe compile karo
    minSdk = 24         // Android 7.0 se neeche support nahi
    targetSdk = 36      // latest features target
    versionCode = 1     // internal version (Play Store)
    versionName = "1.0" // user ko dikhne wala version

    buildFeatures {
        viewBinding = true  // XML views ko binding se access karo
    }
}

dependencies {
    implementation(...)         // app ke andar use hogi
    testImplementation(...)     // sirf unit tests
    androidTestImplementation(...)  // sirf instrumented tests
}
```

---

## 6. `AndroidManifest.xml`

**App ka ID card** — Android OS ko batata hai app ke baare mein sab kuch.

```xml
<application
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:theme="@style/Theme.AndroidConcepts">

    <!-- LAUNCHER — pehle yahi khulta hai -->
    <activity android:name=".SplashActivity" android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>

    <!-- Normal activity -->
    <activity android:name=".TopicOptionsActivity" android:exported="false" />
</application>
```

- `android:icon` — App ka icon
- `android:label` — App ka naam
- `android:theme` — App ka theme
- `MAIN` + `LAUNCHER` — Home screen icon click pe yahi pehle khulta hai
- `exported="true"` — Dusre apps bhi open kar sakte hain
- `exported="false"` — Sirf apna app open kar sakta hai
- **Important:** Nai Activity banao toh yahan register karni padti hai — warna app crash

---

## 7. `res/values/colors.xml`

**App ke saare colors ek jagah define hote hain.**

```xml
<resources>
    <color name="black_000000">#FF000000</color>
    <color name="white_FFFFFF">#FFFFFFFF</color>
    <color name="blue_2196F3">#2196F3</color>
    <color name="purple_9C27B0">#9C27B0</color>
    <color name="orange_FF9800">#FF9800</color>
</resources>
```

- Format: `#AARRGGBB` ya `#RRGGBB`
  - `AA` = Alpha (transparency) — `FF` = fully opaque, `00` = fully transparent
  - `RR` = Red, `GG` = Green, `BB` = Blue
- XML mein use: `android:textColor="@color/black_000000"`
- Kotlin mein use: `ContextCompat.getColor(this, R.color.black_000000)`
- Naming convention: `colorname_hexcode` — naam se hi color pata chale

---

## 8. `res/values/strings.xml`

**App ke saare text strings ek jagah define hote hain.**

```xml
<resources>
    <string name="app_name">AndroidConcepts</string>
    <string name="welcome_to_my_android_concepts">Welcome To\nMy Android Concepts</string>
</resources>
```

- `app_name` — App ka naam (Manifest mein `@string/app_name` se use hota hai)
- `\n` — New line
- XML mein use: `android:text="@string/welcome_to_my_android_concepts"`
- Kotlin mein use: `getString(R.string.app_name)`
- **Kyun?** Localization ke liye — `values-hi/strings.xml` banao toh Hindi automatically support hogi

---

## 9. `res/values/dimen.xml`

**App ke saare dimensions ek jagah define hain.**

**2 units hain:**

| Unit | Kab use karein | Kyun |
|---|---|---|
| `sp` | Text size | User ke font setting ke saath scale hota hai |
| `dp` | Padding, margin, view size | Alag screen densities pe same dikhta hai |

```xml
<dimen name="text_size_16">16sp</dimen>   <!-- text ke liye -->
<dimen name="spacing_8">8dp</dimen>        <!-- spacing ke liye -->
```

- `text_size_2` to `text_size_100` — 2sp se 100sp (even numbers)
- `spacing_2` to `spacing_100` — 2dp se 100dp (even numbers)
- XML mein: `android:textSize="@dimen/text_size_16"`
- **Rule:** Text = `sp`, Baaki sab = `dp`

---

## 10. `res/values/themes.xml` + `styles.xml`

**`themes.xml` — App ka overall theme:**
```xml
<style name="Base.Theme.AndroidConcepts" parent="Theme.Material3.DayNight.NoActionBar">
```
- `Material3` — Google ka latest design system
- `DayNight` — Light + Dark mode automatically support
- `NoActionBar` — Top pe default toolbar nahi hogi
- Manifest mein `android:theme="@style/Theme.AndroidConcepts"` se apply hota hai

**`styles.xml` — Reusable text styles:**
```xml
<style name="Text16BoldStyle">
    <item name="android:fontFamily">@font/opensans_bold</item>
    <item name="android:textColor">@color/white_FFFFFF</item>
    <item name="android:textSize">@dimen/text_size_16</item>
</style>

<!-- Inheritance — parent se sab inherit, sirf size override -->
<style name="Text20BoldStyle" parent="Text16BoldStyle">
    <item name="android:textSize">@dimen/text_size_20</item>
</style>
```
- XML mein use: `style="@style/Text16BoldStyle"`
- **Fayda:** Font/color ek jagah change karo — saari jagah apply

---

## 11-13. Drawable XML Files

Code se UI shapes banate hain — image nahi chahiye.

**Common elements:**
- `<gradient>` — Color gradient (`angle`, `startColor`, `centerColor`, `endColor`)
- `<corners>` — Rounded corners (`android:radius`)
- `<stroke>` — Border (`width`, `color`)

| File | Use | Special |
|---|---|---|
| `splash_background.xml` | Splash background | Blue→Purple→Teal gradient, angle 45° |
| `orange_rounded_background.xml` | Button background | Rounded corners + orange border |
| `purple_background.xml` | Purple background | Simple gradient, angle 135° |

```xml
<!-- orange_rounded_background.xml -->
<shape>
    <gradient angle="135" startColor="#FF9800" centerColor="#FFC107" endColor="#FFEB3B" />
    <corners android:radius="@dimen/spacing_12" />
    <stroke width="@dimen/spacing_2" color="@color/orange_FF9800" />
</shape>
```

- XML mein use: `android:background="@drawable/orange_rounded_background"`
- `angle` — 0°=left→right, 90°=bottom→top, 45°/135°=diagonal

---

## 14. `res/layout/activity_splash.xml`

**Splash screen layout — ek TextView screen ke center mein.**

```xml
<ConstraintLayout android:background="@drawable/splash_background">

    <AppCompatTextView
        android:id="@+id/textView"
        style="@style/Text16BoldStyle"
        android:text="@string/welcome_to_my_android_concepts"
        android:gravity="center"
        android:maxLines="2"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</ConstraintLayout>
```

- `android:id` — ViewBinding se `binding.textView` se access hoga
- `style` — font + color + size ek saath
- `android:gravity="center"` — text andar se center
- `maxLines="2"` — max 2 lines dikhao
- **Center karne ka tarika:** Top+Bottom+Start+End — charon taraf parent se constraint lao

---

## 15. `res/layout/item_concept.xml`

**RecyclerView ka ek item — ek Button.**

```xml
<ConstraintLayout layout_width="match_parent" layout_height="wrap_content">

    <AppCompatButton
        android:id="@+id/buttonConcept"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:background="@drawable/orange_rounded_background"
        tools:text="COUNTER APP" />

</ConstraintLayout>
```

- `layout_height="wrap_content"` — Root sirf button jitna bada ho
- `layout_width="0dp"` — ConstraintLayout mein `0dp` = match constraints = available space lo
- `tools:text` — Sirf design time pe dikhta hai, runtime pe nahi — real text Adapter set karega
- **`0dp` rule:** ConstraintLayout mein `match_parent` kaam nahi karta, `0dp` + constraints use karo

---

## 16. `res/layout/activity_concept_options.xml`

**Topics list screen — full screen RecyclerView Grid.**

```xml
<ConstraintLayout android:background="@drawable/purple_background">

    <RecyclerView
        android:id="@+id/recyclerViewProjects"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:padding="@dimen/spacing_16"
        app:layoutManager="GridLayoutManager"
        app:spanCount="2"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        tools:itemCount="22"
        tools:listitem="@layout/item_concept" />

</ConstraintLayout>
```

- `0dp` width + height + charon constraints = poori screen fill
- `app:layoutManager` + `app:spanCount="2"` — 2 column grid XML mein hi set
- `tools:itemCount` + `tools:listitem` — Sirf Android Studio preview ke liye, runtime pe nahi

---

## 16. `res/layout/activity_concept_options.xml`

**Full screen RecyclerView — purple background pe grid layout.**

```xml
<ConstraintLayout android:background="@drawable/purple_background">

    <RecyclerView
        android:id="@+id/recyclerViewProjects"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:padding="@dimen/spacing_16"
        app:layoutManager="...GridLayoutManager"
        app:spanCount="2"
        tools:itemCount="22"
        tools:listitem="@layout/item_concept"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

</ConstraintLayout>
```

- `0dp` width + height + charon constraints = full screen fill
- `android:padding` — Content ke around 16dp space
- `app:layoutManager` — XML mein hi set (runtime wala override kar deta hai)
- `app:spanCount="2"` — 2 columns
- `tools:itemCount` + `tools:listitem` — Sirf Android Studio preview ke liye, runtime pe nahi

---

## 17. `common/CONCEPTS.kt`

**Saare topics ki enum — abhi empty, topics add honge.**

```kotlin
enum class TOPICS(val topicId: Int, val topicNameResId: Int) {
    UI_BASICS(1, R.string.topic_ui_basics),
    ACTIVITY_LIFECYCLE(2, R.string.topic_activity_lifecycle),
    // ...
}
```

- `topicId` — Har topic ka unique ID (navigation ke liye)
- `topicNameResId` — `strings.xml` ka reference (button ka text)
- `TOPICS.entries` — Enum ke saare values automatically milte hain
- `TopicAdapter` mein: `root.context.getText(item.topicNameResId)` se button text set hoga

---

## 18. `common/RecyclerViewUtils.kt`

**RecyclerView extension function — items ke beech smart spacing.**

```kotlin
fun RecyclerView.setDynamicSpacing(spacePx: Int) {
    addItemDecoration(object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, ...) {
            val column = position % spanCount
            outRect.left = if (column == 0) 0 else spacePx       // left edge pe 0
            outRect.right = if (column == last) 0 else spacePx    // right edge pe 0
            outRect.top = if (position < spanCount) 0 else spacePx // first row pe 0
            outRect.bottom = spacePx
        }
    })
}
```

- `ItemDecoration` — Har item ke around spacing define karta hai
- `column = position % spanCount` — Item kaunse column mein hai
- Edges pe extra spacing nahi lagta — sirf items ke beech
- Use: `recyclerView.setDynamicSpacing(resources.getDimensionPixelSize(R.dimen.spacing_8))`

---

## 19. `common/TopicAdapter.kt`

**RecyclerView Adapter — TOPICS enum ki list show karta hai.**

```kotlin
class TopicAdapter(
    private val topics: List<TOPICS>,
    private val onOptionClicked: (Int) -> Unit  // click pe topicId wapas
) : RecyclerView.Adapter<TopicAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(...) = ProjectViewHolder(ItemConceptBinding.inflate(...))
    override fun onBindViewHolder(holder, position) = holder.bind(topics[position])
    override fun getItemCount() = topics.size

    inner class ProjectViewHolder(val binding: ItemConceptBinding) : ViewHolder(binding.root) {
        fun bind(item: TOPICS) {
            binding.buttonConcept.text = root.context.getText(item.topicNameResId)
            binding.buttonConcept.setOnClickListener { onOptionClicked(item.topicId) }
        }
    }
}
```

- 3 mandatory methods: `onCreateViewHolder`, `onBindViewHolder`, `getItemCount`
- `onOptionClicked: (Int) -> Unit` — Lambda — click pe `topicId` Activity ko bhejo
- **Flow:** Button click → `onOptionClicked(topicId)` → `navigateToTopic(topicId)`

---

## 20. `TopicOptionsActivity.kt`

**Sab kuch yahan connect hota hai — RecyclerView setup + navigation.**

```kotlin
override fun onCreate(...) {
    enableEdgeToEdge()
    binding = ActivityConceptOptionsBinding.inflate(layoutInflater)
    setContentView(binding.root)
}

private fun bindUi() = with(binding) {
    recyclerViewProjects.apply {
        topicAdapter = TopicAdapter(topics = TOPICS.entries, onOptionClicked = { navigateToTopic(it) })
        layoutManager = GridLayoutManager(this@TopicOptionsActivity, 2)
        setHasFixedSize(true)
        setDynamicSpacing(resources.getDimensionPixelSize(R.dimen.spacing_8))
        adapter = topicAdapter
    }
}

private fun navigateToTopic(topicId: Int) {
    when(topicId) { /* topics add honge yahan */ }
}
```

- `enableEdgeToEdge()` — Content status/nav bar ke neeche jaaye
- `setPadding` se system bars ka space manually account karo
- `TOPICS.entries` — enum ke saare topics automatically
- `setHasFixedSize(true)` — items ka size fixed → performance better
- `navigateToTopic` — Abhi empty, topics add honge toh navigation yahan likhenge

---

## 21. `SplashActivity.kt`

**Splash screen — 2 second dikhao, phir TopicOptionsActivity pe jao.**

```kotlin
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private fun bindAction() = with(binding) {
        root.postDelayed({
            val intent = Intent(this@SplashActivity, TopicOptionsActivity::class.java)
            val options = ActivityOptionsCompat.makeCustomAnimation(
                this@SplashActivity,
                android.R.anim.fade_in,
                android.R.anim.fade_out
            )
            startActivity(intent, options.toBundle())
            finish()
        }, 2000)
    }
}
```

- `@SuppressLint("CustomSplashScreen")` — Android 12+ splash warning suppress karo
- `postDelayed({ ... }, 2000)` — 2 second baad code chalao
- `Intent(this@SplashActivity, TopicOptionsActivity::class.java)` — Explicit intent
- `makeCustomAnimation(fade_in, fade_out)` — Fade animation transition
- `finish()` — SplashActivity close karo — back press pe wapas nahi aayega

---
