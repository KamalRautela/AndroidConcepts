# Advanced Views — RecyclerView, ScrollView, CardView, FAB, Snackbar, Toast, AlertDialog, BottomNavigation

---

## RecyclerView

List/grid efficiently display karta hai — sirf visible items ke liye Views banata hai (recycle karta hai).

**3 mandatory parts:**
1. **Adapter** — data ko View se connect karta hai
2. **ViewHolder** — ek item ka View hold karta hai
3. **LayoutManager** — items kaise arrange hon (linear/grid)

```kotlin
// Adapter
class TopicAdapter(private val topics: List<String>) :
    RecyclerView.Adapter<TopicAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemTopicBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(topic: String) {
            binding.tvName.text = topic
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(topics[position])

    override fun getItemCount() = topics.size
}

// Activity mein setup
binding.recyclerView.layoutManager = LinearLayoutManager(this)
binding.recyclerView.adapter = TopicAdapter(myList)
```

**LayoutManagers:**
| LayoutManager | Use |
|---|---|
| `LinearLayoutManager` | Vertical / horizontal list |
| `GridLayoutManager(ctx, 2)` | 2-column grid |
| `StaggeredGridLayoutManager` | Pinterest-style uneven grid |

---

## ScrollView / NestedScrollView

Single content scroll karne ke liye. **Sirf ek direct child** allowed hai.

```xml
<androidx.core.widget.NestedScrollView
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical">
        <!-- content -->
    </LinearLayout>
</androidx.core.widget.NestedScrollView>
```

**NestedScrollView prefer karo** over ScrollView — RecyclerView ke saath conflict nahi karta.

**Programmatic scroll:**
```kotlin
// Top pe jaao
binding.nestedScrollView.smoothScrollTo(0, 0)

// Bottom pe jaao
binding.nestedScrollView.smoothScrollTo(0, binding.nestedScrollView.getChildAt(0).height)
```

**HorizontalScrollView** — horizontal scroll ke liye (same pattern, sirf ek child).

---

## CardView (MaterialCardView)

Content ko card-style container mein wrap karta hai with elevation aur rounded corners.

```xml
<com.google.android.material.card.MaterialCardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardCornerRadius="12dp"
    app:cardElevation="4dp"
    app:strokeColor="#0099CC"
    app:strokeWidth="1dp"
    android:clickable="true"
    android:focusable="true">

    <!-- content -->
</com.google.android.material.card.MaterialCardView>
```

**Key attributes:**
| Attribute | Use |
|---|---|
| `cardCornerRadius` | Rounded corners |
| `cardElevation` | Shadow ka depth |
| `strokeColor` / `strokeWidth` | Border |
| `rippleColor` | Click ripple color |
| `android:clickable="true"` | Clickable banana ke liye zaroor chahiye |

**`cardElevation` vs `strokeColor`:** Elevation = shadow, stroke = border. Dono saath use kar sakte ho.

---

## FAB — FloatingActionButton

Screen ke upar ek circular primary action button.

```xml
<!-- Normal FAB -->
<com.google.android.material.floatingactionbutton.FloatingActionButton
    android:id="@+id/fab"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:srcCompat="@android:drawable/ic_input_add"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintBottom_toBottomOf="parent"
    android:layout_margin="16dp" />

<!-- Extended FAB (with text) -->
<com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
    android:id="@+id/extendedFab"
    android:text="Add Topic"
    app:icon="@android:drawable/ic_input_add" />
```

**Content overlap problem:** FAB content ke upar aata hai — fix ke liye last content view pe `paddingBottom="80dp"` add karo.

---

## Toast

Quick, auto-dismiss message. User interact nahi kar sakta.

```kotlin
Toast.makeText(this, "Data saved!", Toast.LENGTH_SHORT).show()
Toast.makeText(this, "Long message here", Toast.LENGTH_LONG).show()
```

**Limitations:** Non-interactive, styling customize nahi ho sakta, Android 12+ pe custom Toast deprecated.

---

## Snackbar

Action ke saath dismissible message — Toast se zyada powerful.

```kotlin
// Simple
Snackbar.make(binding.root, "Item deleted", Snackbar.LENGTH_SHORT).show()

// Action ke saath
Snackbar.make(binding.root, "Item deleted", Snackbar.LENGTH_LONG)
    .setAction("UNDO") {
        Toast.makeText(this, "Restored!", Toast.LENGTH_SHORT).show()
    }.show()

// Manual dismiss
Snackbar.make(binding.root, "Changes saved", Snackbar.LENGTH_INDEFINITE)
    .setAction("DISMISS") { }
    .show()
```

**Toast vs Snackbar:**
| | Toast | Snackbar |
|---|---|---|
| Action button | No | Yes |
| Dismiss manually | No | Yes (INDEFINITE) |
| Position | Center/Bottom | Bottom of screen |
| Use | Simple info | User action result |

---

## AlertDialog

User se confirmation ya input lena.

```kotlin
// Basic
AlertDialog.Builder(this)
    .setTitle("Delete?")
    .setMessage("This cannot be undone.")
    .setPositiveButton("Delete") { _, _ -> /* delete */ }
    .setNegativeButton("Cancel", null)
    .show()

// Single choice (radio)
val options = arrayOf("Light", "Dark", "System")
var selected = 0
AlertDialog.Builder(this)
    .setSingleChoiceItems(options, selected) { _, which -> selected = which }
    .setPositiveButton("OK") { _, _ -> applyTheme(options[selected]) }
    .show()

// Multi choice (checkboxes)
val items = arrayOf("Email", "SMS", "Push")
val checked = booleanArrayOf(true, false, true)
AlertDialog.Builder(this)
    .setMultiChoiceItems(items, checked) { _, which, isChecked -> checked[which] = isChecked }
    .setPositiveButton("OK") { _, _ ->
        val selected = items.filterIndexed { i, _ -> checked[i] }
    }.show()

// Custom view (input)
val input = EditText(this).apply { hint = "Enter name"; setPadding(48, 32, 48, 32) }
AlertDialog.Builder(this)
    .setView(input)
    .setPositiveButton("OK") { _, _ -> val name = input.text.toString().trim() }
    .show()
```

**`filterIndexed { i, _ -> checked[i] }`** — items list mein se sirf woh elements lo jahan `checked[i]` = true hai.

---

## BottomNavigationView

Bottom pe tabs — typically 3-5 destinations ke liye.

```xml
<com.google.android.material.bottomnavigation.BottomNavigationView
    android:id="@+id/bottomNav"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:menu="@menu/bottom_nav_menu"
    app:itemIconTint="@color/selector_nav"
    app:itemTextColor="@color/selector_nav" />
```

```xml
<!-- res/menu/bottom_nav_menu.xml -->
<menu>
    <item android:id="@+id/nav_home" android:icon="@drawable/ic_home" android:title="Home" />
    <item android:id="@+id/nav_search" android:icon="@drawable/ic_search" android:title="Search" />
</menu>
```

```kotlin
binding.bottomNav.setOnItemSelectedListener { item ->
    when (item.itemId) {
        R.id.nav_home -> showPage(binding.pageHome)
        R.id.nav_search -> showPage(binding.pageSearch)
    }
    true
}

private fun showPage(page: View) {
    listOf(binding.pageHome, binding.pageSearch).forEach { it.visibility = View.GONE }
    page.visibility = View.VISIBLE
}
```

---

## Interview Mein Bolna

> *"RecyclerView efficient list hai — Adapter data bind karta hai, ViewHolder View hold karta hai, LayoutManager arrangement decide karta hai. ScrollView ke liye NestedScrollView prefer karo kyunki RecyclerView ke saath conflict avoid hota hai. CardView elevation aur stroke se visual separation deta hai — clickable banana ke liye `android:clickable='true'` set karna padta hai. Toast simple message ke liye, Snackbar action ke saath message ke liye. AlertDialog confirmation, choice, ya input ke liye. BottomNavigation 3-5 tabs ke liye — Fragment ya simple VISIBLE/GONE toggle se implement karte hain."*
