# Android Layouts — LinearLayout, RelativeLayout, ConstraintLayout, FrameLayout

## Layout kya hai?

Layout ek ViewGroup hai — doosre Views ko hold karta hai aur screen pe position karta hai.
Sab layouts `ViewGroup` ke subclass hain jo khud `View` ka subclass hai.

---

## LinearLayout

Views ko ek line mein arrange karta hai — horizontal ya vertical.

```xml
<LinearLayout
    android:orientation="vertical"
    android:weightSum="3">

    <View android:layout_weight="1" />  <!-- 1/3 space -->
    <View android:layout_weight="2" />  <!-- 2/3 space -->
</LinearLayout>
```

**Key attributes:**
| Attribute | Use |
|---|---|
| `orientation` | horizontal / vertical |
| `gravity` | Children ko kahan align karo |
| `layout_weight` | Space distribute karo proportionally |
| `weightSum` | Total weight define karo |

**Limitation:** Nested LinearLayouts performance hit karte hain. Iske liye ConstraintLayout use karo.

---

## RelativeLayout

Views ko ek doosre ke relative ya parent ke relative position karo.

```xml
<RelativeLayout>
    <TextView
        android:id="@+id/tvTitle"
        android:layout_alignParentTop="true"
        android:layout_centerHorizontal="true" />

    <Button
        android:layout_below="@id/tvTitle"
        android:layout_toEndOf="@id/tvIcon" />
</RelativeLayout>
```

**Common attributes:**
| Attribute | Matlab |
|---|---|
| `layout_alignParentTop` | Parent ke top se align |
| `layout_centerInParent` | Parent ke center mein |
| `layout_below` | Kisi view ke neeche |
| `layout_toEndOf` | Kisi view ke end/right mein |

**2026 mein:** RelativeLayout mostly deprecated consider hota hai — ConstraintLayout use karo.

---

## ConstraintLayout

Most powerful layout — flat hierarchy, no nesting needed.

```xml
<ConstraintLayout>
    <TextView
        android:id="@+id/tvTitle"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />

    <Button
        app:layout_constraintTop_toBottomOf="@id/tvTitle"
        app:layout_constraintStart_toStartOf="parent" />
</ConstraintLayout>
```

**Key Concepts:**

**1. Chains** — Views ko group karo aur space distribute karo:
```xml
app:layout_constraintHorizontal_chainStyle="spread"   <!-- equal space -->
app:layout_constraintHorizontal_chainStyle="packed"   <!-- ek saath group -->
```

**2. Bias** — Chain position adjust karo (0.0 = start, 1.0 = end):
```xml
app:layout_constraintHorizontal_bias="0.3"
```

**3. Guideline** — Invisible helper line:
```xml
<Guideline
    android:orientation="vertical"
    app:layout_constraintGuide_percent="0.5" />  <!-- screen ka 50% -->
```

**ConstraintLayout kyun use karo:**
- Flat hierarchy → better performance
- Nesting nahi chahiye
- Complex layouts ek level mein

---

## FrameLayout

Views ko ek doosre ke upar stack karta hai (overlapping).

```xml
<FrameLayout>
    <ImageView />           <!-- background -->
    <TextView />            <!-- upar text -->
    <ProgressBar />         <!-- sabse upar loading -->
</FrameLayout>
```

**Use cases:**
- Loading spinner image ke upar dikhana
- Fragment container
- Badge number icon ke upar

**`layout_gravity`** — child ko FrameLayout mein position karo:
```xml
android:layout_gravity="bottom|end"   <!-- bottom-right corner -->
android:layout_gravity="center"       <!-- center mein -->
```

---

## Comparison Table

| Layout | Best For | Avoid When |
|---|---|---|
| LinearLayout | Simple row/column, weight distribution | Nesting chahiye — performance hit |
| RelativeLayout | Simple relative positioning | Complex UI — ConstraintLayout better |
| ConstraintLayout | Complex UI, flat hierarchy | Simple 1-2 view screens (overkill) |
| FrameLayout | Overlapping views, Fragment container | Multiple side-by-side views |

---

## Interview Mein Bolna

> *"ConstraintLayout sabse recommended hai — flat hierarchy maintain karta hai jo performance ke liye better hai. LinearLayout weight se space distribute karta hai. RelativeLayout mein views ek doosre ke relative position hote hain lekin ab ConstraintLayout zyada prefer hota hai. FrameLayout views ko stack karta hai — loading overlay ya Fragment container ke liye use karte hain."*
