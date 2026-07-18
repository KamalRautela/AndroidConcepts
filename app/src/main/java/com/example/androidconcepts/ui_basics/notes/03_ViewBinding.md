# ViewBinding

## ViewBinding kya hai?

ViewBinding ek feature hai jo XML layout ke liye automatically ek **binding class** generate karta hai.
Isse `findViewById()` ki zaroorat nahi padti — direct `binding.viewId` se access karo.

---

## Kyun use karte hain?

| Problem (`findViewById`) | Solution (ViewBinding) |
|---|---|
| NullPointerException aa sakta hai — wrong ID | Compile-time check — ID galat ho toh build fail |
| Type cast manually karna padta hai | Type-safe — already correct type |
| Boilerplate code zyada | Clean aur concise |

---

## Setup

`build.gradle.kts` mein:
```kotlin
android {
    buildFeatures {
        viewBinding = true
    }
}
```

---

## Activity mein use karna

```kotlin
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding  // auto-generated class

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ab directly access karo
        binding.tvTitle.text = "Hello"
        binding.btnSubmit.setOnClickListener { }
    }
}
```

**Binding class ka naam:** `activity_main.xml` → `ActivityMainBinding` (CamelCase + "Binding")

---

## Naming Convention

| XML File | Generated Class |
|---|---|
| `activity_main.xml` | `ActivityMainBinding` |
| `activity_scroll_view.xml` | `ActivityScrollViewBinding` |
| `item_topic.xml` | `ItemTopicBinding` |
| `fragment_home.xml` | `FragmentHomeBinding` |

---

## Fragment mein use karna

Fragment mein binding thoda alag hai — `onDestroyView` mein null karna padta hai memory leak avoid karne ke liye:

```kotlin
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // memory leak avoid karo
    }
}
```

---

## RecyclerView Adapter mein use karna

```kotlin
class TopicAdapter : RecyclerView.Adapter<TopicAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTopicBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    class ViewHolder(private val binding: ItemTopicBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(topic: String) {
            binding.tvName.text = topic
        }
    }
}
```

---

## Interview Mein Bolna

> *"ViewBinding XML layout ke liye automatically binding class generate karta hai. `findViewById` ki zaroorat nahi — compile-time type-safe access milti hai. Activity mein `inflate()` se binding banate hain aur `binding.root` ko `setContentView` mein pass karte hain. Fragment mein `_binding` nullable rakhte hain aur `onDestroyView` mein null karte hain — warna memory leak hoti hai kyunki Fragment ka view destroy ho jaata hai but Fragment ka reference reh jaata hai."*
