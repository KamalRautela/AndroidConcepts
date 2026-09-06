# Navigation Component — Notes

Single-Activity architecture ke liye Fragments ke beech navigation, back stack, aur data passing — sab ek `nav_graph.xml` se declaratively manage karna. Bottom Navigation (3 tabs) + Detail screen (Safe Args) + Back Stack control, ek hi demo mein.

---

## Kya Hai

**Navigation Component** — Jetpack library jo Fragment transactions, back stack, aur deep links ko manually `FragmentManager` se manage karne ki jagah **ek visual graph (`nav_graph.xml`)** se declaratively handle karta hai. Manual `FragmentTransaction`/`addToBackStack()` likhne ki zaroorat khatam.

## Core Building Blocks

| Block | Kaam |
|---|---|
| `nav_graph.xml` | Saare destinations (Fragments) aur unke beech connections (`action`) declare karta hai |
| `NavHostFragment` | Activity ke layout mein woh container jahan destinations render hote hain |
| `NavController` | Navigation drive karta hai — `navigate()`, back stack sab isi se |
| Safe Args (plugin) | `nav_graph.xml` ke `<argument>`/`<action>` se compile-time type-safe `Directions`/`Args` classes generate karta hai |

**Ek Activity ka apna `nav_graph.xml` hota hai** — single-Activity architecture mein poori app ka ek graph bhi ho sakta hai, ya bade app mein feature-wise multiple graphs (nested navigation).

---

## Architecture (Demo)

```
NavigationActivity (NavHostFragment + BottomNavigationView)
        │
        ├── HomeFragment (start destination, bottom-nav tab)
        │       └── click → DetailFragment (Safe Args: itemId, itemTitle)
        │               └── button → SettingsFragment (popUpTo Home, inclusive — back stack clear)
        ├── ProfileFragment (bottom-nav tab)
        └── SettingsFragment (bottom-nav tab)
```

---

## Code — Poori Chain

### 1. `nav_graph.xml` — Setup
```xml
<navigation
    android:id="@+id/nav_graph"
    app:startDestination="@id/homeFragment">

    <fragment
        android:id="@+id/homeFragment"
        android:name="com.example.androidconcepts.navigation.HomeFragment"
        android:label="Home"
        tools:layout="@layout/fragment_home">
        <action
            android:id="@+id/action_home_fragment_to_detail_fragment"
            app:destination="@id/detailFragment" />
    </fragment>

    <fragment
        android:id="@+id/profileFragment"
        android:name="com.example.androidconcepts.navigation.ProfileFragment"
        android:label="Profile"
        tools:layout="@layout/fragment_profile" />

    <fragment
        android:id="@+id/settingsFragment"
        android:name="com.example.androidconcepts.navigation.SettingsFragment"
        android:label="Settings"
        tools:layout="@layout/fragment_settings" />

    <fragment
        android:id="@+id/detailFragment"
        android:name="com.example.androidconcepts.navigation.DetailFragment"
        android:label="Detail"
        tools:layout="@layout/fragment_detail">

        <argument android:name="itemId" app:argType="integer" />
        <argument android:name="itemTitle" app:argType="string" />

        <action
            android:id="@+id/action_detail_fragment_to_settings_fragment"
            app:destination="@id/settingsFragment"
            app:popUpTo="@id/homeFragment"
            app:popUpToInclusive="true" />
    </fragment>

</navigation>
```

### 2. `activity_navigation.xml` — NavHost + BottomNavigationView
```xml
<androidx.fragment.app.FragmentContainerView
    android:id="@+id/navHostFragment"
    android:name="androidx.navigation.fragment.NavHostFragment"
    app:defaultNavHost="true"
    app:navGraph="@navigation/nav_graph" />

<com.google.android.material.bottomnavigation.BottomNavigationView
    android:id="@+id/bottomNav"
    app:menu="@menu/nav_bottom_menu" />
```

> `nav_bottom_menu.xml` ke `<item>` IDs (`homeFragment`, `profileFragment`, `settingsFragment`) **jaan-bujh kar** nav_graph ke fragment IDs jaise hi rakhe — `@+id/name` same naam se do jagah likhne pe Android compiler dono ko **same resource ID** resolve karta hai, isi se `setupWithNavController` automatically tab ↔ destination map kar leta hai.

### 3. `NavigationActivity.kt` — Wiring
```kotlin
private fun bindUi() {
    val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
    val navController = navHostFragment.navController

    binding.bottomNav.setupWithNavController(navController)
}
```

### 4. `HomeFragment.kt` — Navigate + Pass Data (Safe Args)
```kotlin
binding.itemKotlin.setOnClickListener {
    val action = HomeFragmentDirections.actionHomeFragmentToDetailFragment(itemId = 1, itemTitle = "Kotlin")
    findNavController().navigate(action)
}
```

### 5. `DetailFragment.kt` — Receive Data + Back Stack Navigate
```kotlin
private val args: DetailFragmentArgs by navArgs()

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.tvId.text = args.itemId.toString()
    binding.tvTitle.text = args.itemTitle

    binding.btnGoSettings.setOnClickListener {
        val action = DetailFragmentDirections.actionDetailFragmentToSettingsFragment()
        findNavController().navigate(action)
    }
}
```

---

## Key Concepts

| Concept | Detail |
|---|---|
| `findNavController()` (Fragment ke andar) | Extension function — Fragment ke View hierarchy se `NavController` khud dhoondh leta hai |
| `findFragmentById(...).navController` (Activity ke andar) | Fragment ke andar shortcut nahi hai, isliye `NavHostFragment` fetch karke uska `navController` nikalna padta hai |
| Safe Args `Directions` class | Har action se generate hoti hai (bhejne wale Fragment ke liye) — `actionXToY(args)` → `NavDirections` |
| Safe Args `Args` class | Har argument-wale destination se generate hoti hai (receive karne wale Fragment ke liye) — `by navArgs()` |
| `app:popUpTo` + `app:popUpToInclusive="true"` | Navigate karte waqt back stack se destinations hata do — `inclusive` bola gaya destination bhi hatega |
| `setupWithNavController(navController)` | Ek line — `BottomNavigationView` ke tab clicks automatically NavController se jud jaate hain (manual click listener nahi likhna) |

---

## Data Pass/Receive Ke Alternative Tareeke (Safe Args Ke Alawa)

| Tareeka | Kab Use Karein |
|---|---|
| **Bundle directly** (`bundleOf`, `arguments?.getInt(...)`) | Safe Args plugin ke bina — kaam karta hai, lekin type-safe nahi (string keys, typo risk) |
| **Shared ViewModel** (`by navGraphViewModels(R.id.nav_graph)` ya `by activityViewModels()`) | Jab data **dono directions** mein chahiye ho, ya complex object ho (list, non-Parcelable) |
| **Fragment Result API** (`setFragmentResult`/`setFragmentResultListener`) | Jab **child se parent ko wapas** data bhejna ho (Safe Args sirf forward/parent→child jaata hai) |

Hamare demo mein **Safe Args** use kiya — data simple (`itemId`, `itemTitle`) hai aur sirf forward direction (Home → Detail) mein jaana tha.

---

## Common Interview Questions

**Q: Navigation Component kyun use karte hain, manual FragmentTransaction kyun nahi?**
> Manual `FragmentManager.beginTransaction()` mein back stack, deep links, aur animations sab manually manage karne padte hain — error-prone. Navigation Component ek declarative graph se yeh sab handle karta hai, aur Safe Args se type-safe data passing bhi milti hai.

**Q: Safe Args kaise type-safe hai?**
> Nav graph ke `<argument>` se compile-time pe ek asli Kotlin class (`XArgs`) generate hoti hai jismein real typed properties hote hain (`val itemId: Int`) — Bundle ki tarah generic string-keyed nahi. Agar galat property access karo, compile error aata hai; runtime crash nahi.

**Q: `popUpTo` aur `popUpToInclusive` mein fark?**
> `popUpTo="@id/X"` — back stack se navigate karte waqt X tak (X ko chhodke) sab kuch hata do. `popUpToInclusive="true"` add karne se X **khud bhi** hat jaata hai. Login flow ke baad back press pe login screen pe wapas na jaana ho — isi pattern se karte hain.

**Q: `setupWithNavController` kya karta hai?**
> `BottomNavigationView`/`NavigationView` ke tab clicks ko automatically `NavController.navigate()` se jod deta hai, sirf agar menu item IDs nav_graph destination IDs se match karein. Manual click listener likhne ki zaroorat khatam ho jaati hai.

---

## Interview Mein Bolna

> *"Navigation Component single-Activity architecture ke liye hai — Fragments ke beech navigation, back stack, aur data passing sab ek `nav_graph.xml` se declaratively manage hota hai. Safe Args plugin compile-time pe type-safe `Directions`/`Args` classes generate karta hai — Bundle mein manually key daalna nahi padta, na hi wrong type ka risk rehta hai. `popUpTo` + `popUpToInclusive` se back stack ko control karte hain — jaise login flow ke baad back press pe login screen pe wapas na jaana ho. `BottomNavigationView.setupWithNavController()` se tab-switching bhi ek line mein automatic ho jaata hai, bas menu item IDs nav_graph destination IDs se match hone chahiye."*
