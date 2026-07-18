# Android Views — TextView, Button, EditText, ImageView, CheckBox, RadioButton, Switch, ProgressBar

## Views kya hain?

View Android UI ka basic building block hai — jo kuch bhi screen pe dikhta hai wo ek View hai.
`TextView`, `Button`, `ImageView` — sab View ke subclasses hain.

---

## TextView

Text screen pe dikhata hai.

```xml
<TextView
    android:id="@+id/tvName"
    android:text="Hello Kamal"
    android:textSize="16sp"
    android:textColor="#FFFFFF"
    android:textStyle="bold"
    android:maxLines="2"
    android:ellipsize="end" />
```

**Key attributes:**
| Attribute | Use |
|---|---|
| `textSize` | sp mein — user font size respect karta hai |
| `textStyle` | bold / italic / normal |
| `maxLines` | zyada text ho toh kitni lines dikhani hain |
| `ellipsize` | overflow pe `...` dikhao |
| `autoLink` | URL/phone auto clickable bana do |

---

## Button / AppCompatButton

User action trigger karta hai.

```kotlin
binding.btnSubmit.setOnClickListener {
    // action
}
```

**`AppCompatButton` prefer karo** over `Button` — theme consistency ke liye.

---

## EditText

User se input leta hai.

```xml
<EditText
    android:hint="Enter name"
    android:inputType="text"
    android:imeOptions="actionDone" />
```

**Common `inputType` values:**
| Value | Use |
|---|---|
| `text` | Normal text |
| `textPassword` | Password — characters hide |
| `number` | Sirf numbers |
| `textEmailAddress` | Email keyboard |
| `phone` | Phone keyboard |

**TextWatcher** — har character pe kuch karna ho:
```kotlin
binding.etName.addTextChangedListener(object : TextWatcher {
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // real-time validation
    }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable?) {}
})
```

---

## ImageView

Image dikhata hai.

```xml
<ImageView
    android:src="@drawable/ic_logo"
    android:scaleType="centerCrop"
    android:contentDescription="Logo" />
```

**`scaleType` values:**
| Value | Behavior |
|---|---|
| `centerCrop` | Image fill karo, crop karo — ratio maintain |
| `fitCenter` | Poori image dikhao, space chhod do |
| `centerInside` | Fit karo without cropping |

---

## CheckBox

Multiple options mein multiple select.

```kotlin
binding.checkBoxTerms.setOnCheckedChangeListener { _, isChecked ->
    if (isChecked) {
        // checked
    }
}
```

---

## RadioButton + RadioGroup

Multiple options mein sirf ek select.

```kotlin
binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
    val selected = binding.root.findViewById<RadioButton>(checkedId)
    val text = selected.text.toString()
}
```

**Important:** `checkedId` = resource ID hai, index nahi. Isliye `findViewById` use karo.

---

## Switch (SwitchCompat)

On/Off toggle.

```kotlin
binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
    if (isChecked) enableDarkMode() else disableDarkMode()
}
```

---

## ProgressBar

Loading/progress dikhata hai.

```xml
<!-- Indeterminate — kab complete hoga pata nahi -->
<ProgressBar
    android:indeterminate="true" />

<!-- Determinate — progress pata hai -->
<ProgressBar
    style="@style/Widget.AppCompat.ProgressBar.Horizontal"
    android:max="100"
    android:progress="60" />
```

```kotlin
binding.progressBar.progress = 75  // programmatically set
```

**SeekBar** — user manually drag kare progress:
```kotlin
binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        binding.tvValue.text = progress.toString()
    }
    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
})
```

---

## Interview Mein Bolna

> *"Views Android UI ke basic building blocks hain. TextView text dikhata hai, EditText input leta hai — TextWatcher se real-time validation kar sakte hain. ImageView mein scaleType important hai — centerCrop image fill karta hai aur crop karta hai. CheckBox multiple select ke liye, RadioButton single select ke liye — RadioGroup mein checkedId resource ID hota hai, index nahi. ProgressBar dono indeterminate aur determinate ho sakta hai."*
