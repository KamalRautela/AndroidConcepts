# Room CRUD — Todo Demo Notes

Poora CRUD (Insert/Update/Delete/Query) + **Flow-based reactive updates** — ek hi Todo List app mein. Retrofit ke GET/POST/PUT/DELETE se alag isliye ek hi demo mein sab kiya, kyunki yahan ek hi local table hai (alag endpoints nahi).

---

## Kya Hai

Room Setup (Entity/DAO/Database/Singleton) ke baad, is topic mein poora **MVVM + Repository** flow banaya — local database ke saath. Sabse bada naya concept: **`Flow<List<Todo>>`** — jab bhi database mein data change ho (Insert/Update/Delete), UI **automatically** update ho jaati hai, koi manual reload nahi karna padta.

---

## Poori Chain

```
View (TodoActivity) → ViewModel (TodoViewModel) → Repository (TodoRepository) → TodoDao → RoomInstance (Database)
```

---

## Code — Ek-Ek Layer

### 1. `Todo.kt` — Entity
```kotlin
@Entity(tableName = "todo")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val isDone: Boolean = false
)
```

### 2. `TodoDao.kt` — DAO
```kotlin
@Dao
interface TodoDao {
    @Insert
    suspend fun insertTodo(todo: Todo)

    @Update
    suspend fun updateTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Query("SELECT * from todo order by id desc")
    fun getAllTodos(): Flow<List<Todo>>
}
```
> **Insert/Update/Delete = `suspend fun`** (one-time operations). **Query jo continuously observe karni hai = `Flow`, bina `suspend`** — Room khud naya emission bhejta hai jab table change ho.

### 3. `TodoDatabase.kt` — Database
```kotlin
@Database(entities = [Todo::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}
```

### 4. `RoomInstance.kt` — Singleton
```kotlin
object RoomInstance {
    @Volatile
    private var INSTANCE: TodoDatabase? = null

    fun getDatabase(context: Context): TodoDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                TodoDatabase::class.java,
                "todo_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
```

### 5. `TodoRepository.kt`
```kotlin
class TodoRepository(
    private val todoDao: TodoDao
) {
    suspend fun insertTodo(todo: Todo) { todoDao.insertTodo(todo) }
    suspend fun updateTodo(todo: Todo) { todoDao.updateTodo(todo) }
    suspend fun deleteTodo(todo: Todo) { todoDao.deleteTodo(todo) }
    fun getAllTodos(): Flow<List<Todo>> = todoDao.getAllTodos()
}
```

### 6. `TodoViewModel.kt` — `AndroidViewModel` (naya concept)
```kotlin
class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TodoRepository
    val todoLiveData: LiveData<List<Todo>>

    init {
        val dao = RoomInstance.getDatabase(application).todoDao()
        repository = TodoRepository(dao)
        todoLiveData = repository.getAllTodos().asLiveData()
    }

    fun insertTodo(title: String) {
        viewModelScope.launch { repository.insertTodo(Todo(title = title)) }
    }

    fun toggleDone(todo: Todo) {
        viewModelScope.launch { repository.updateTodo(todo.copy(isDone = !todo.isDone)) }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch { repository.deleteTodo(todo) }
    }
}
```

### 7. `TodoAdapter.kt` — RecyclerView Adapter
```kotlin
class TodoAdapter(
    private val onToggleDone: (Todo) -> Unit,
    private val onDelete: (Todo) -> Unit
) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    private var todos: List<Todo> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newTodos: List<Todo>) {
        todos = newTodos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTodoRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(todos[position])
    override fun getItemCount(): Int = todos.size

    inner class ViewHolder(private val binding: ItemTodoRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(todo: Todo) {
            binding.tvTodoTitle.text = todo.title
            binding.cbDone.isChecked = todo.isDone
            binding.cbDone.setOnClickListener { onToggleDone(todo) }
            binding.btnDeleteTodo.setOnClickListener { onDelete(todo) }
        }
    }
}
```

### 8. `TodoActivity.kt` — View
```kotlin
private val viewModel: TodoViewModel by viewModels()
private lateinit var todoAdapter: TodoAdapter

private fun bindUi() {
    binding.rvTodos.apply {
        todoAdapter = TodoAdapter(
            onToggleDone = { todo -> viewModel.toggleDone(todo) },
            onDelete = { todo -> viewModel.deleteTodo(todo) }
        )
        layoutManager = LinearLayoutManager(this@TodoActivity)
        adapter = todoAdapter
    }

    binding.btnAdd.setOnClickListener {
        val title = binding.etTodoTitle.text.toString().trim()
        if (title.isNotEmpty()) {
            viewModel.insertTodo(title)
            binding.etTodoTitle.text?.clear()
        }
    }

    viewModel.todoLiveData.observe(this) { todos ->
        todoAdapter.submitList(todos)
        binding.tvEmpty.visibility = if (todos.isEmpty()) View.VISIBLE else View.GONE
        binding.rvTodos.visibility = if (todos.isEmpty()) View.GONE else View.VISIBLE
    }
}
```

---

## 9. Migrations — Concept (Full Demo Skip Kiya)

### Kya Hai

Jab app **already users ke phone pe install** hai (real data ke saath), aur schema change karna hai (naya column add karna, table modify karna) — purana data **delete nahi kar sakte**. Migration ek "upgrade plan" hai jo purane schema ko naye schema mein convert karta hai, **data preserve karte hue**.

> **Note:** Iska poora demo nahi banaya (extra Activity/UI ki zaroorat nahi thi concept samajhne ke liye) — sirf concept + code pattern yahan record kar rahe hain.

### Code Pattern

**Step 1 — Naya field add karo Entity mein, version bump karo:**
```kotlin
@Entity(tableName = "todo")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val isDone: Boolean = false,
    val priority: Int = 0   // naya field (version 2 mein add hua)
)

@Database(entities = [Todo::class], version = 2)   // version 1 se 2
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}
```

**Step 2 — Migration object banao (raw SQL likhte hain, yeh edge case hai jahan Room ka "convenience" nahi milta):**
```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE todo ADD COLUMN priority INTEGER NOT NULL DEFAULT 0")
    }
}
```
- `Migration(1, 2)` — version 1 se version 2 tak upgrade
- `migrate(db)` — andar raw SQL likhते hain jo actual schema change kare

**Step 3 — `RoomInstance` mein Migration register karo:**
```kotlin
Room.databaseBuilder(context.applicationContext, TodoDatabase::class.java, "todo_database")
    .addMigrations(MIGRATION_1_2)
    .build()
```

### Shortcut — Sirf Development Ke Liye (Production Mein NAHI)
```kotlin
Room.databaseBuilder(...)
    .fallbackToDestructiveMigration()
    .build()
```
Agar Migration define nahi ki, Room **poora database delete karke naya bana deta hai** — data **gayab** ho jaata hai. Development/testing mein theek hai, **production app mein kabhi use mat karna** (real users ka data delete ho jaayega).

### Multiple Version Jumps
Agar user bahut purana app version use kar raha hai (jaise version 1 se seedha version 3 pe upgrade), Room ko **sabhi beech ki migrations** chahiye:
```kotlin
.addMigrations(MIGRATION_1_2, MIGRATION_2_3)
```
Room khud chain lagata hai (1→2→3), aap sirf har consecutive version-jump ki migration define karo.

---

## Key Concepts

| Concept | Detail |
|---|---|
| `AndroidViewModel` vs `ViewModel` | Room ko database file banane ke liye `Context` chahiye. `AndroidViewModel(application)` automatically `Application` object deta hai — Retrofit mein zaroorat nahi thi kyunki network calls ko Context nahi chahiye. |
| `Flow<List<Todo>>` (bina `suspend`) | Continuously observe hone wali query — Room khud naya data emit karta hai jab table change ho. Insert/Update/Delete `suspend fun` hote hain (one-time). |
| `.asLiveData()` | `Flow` ko `LiveData` mein convert karta hai — taaki Activity mein familiar `.observe()` pattern use ho sake. |
| `todo.copy(isDone = !todo.isDone)` | `data class` immutable hai (`val` fields), `.copy()` se naya object banta hai. `!` operator Boolean ulta karta hai — ek line mein toggle (true↔false) dono directions handle. |
| `inner class ViewHolder` | `inner` zaroori hai jab ViewHolder ko outer class ke members (`onToggleDone`, `onDelete` lambdas) access karne hon. |
| `submitList()` + `notifyDataSetChanged()` | DiffUtil abhi skip kiya (RecyclerView ka pending topic) — simple approach: naya data set karo, poora list refresh karo. |
| Migrations | Schema change (naya column/table) production app mein hone pe purana data preserve karne ka tareeka — `Migration(oldVersion, newVersion)` object mein raw SQL likhte hain. `fallbackToDestructiveMigration()` sirf development ke liye (data delete kar deta hai). |

---

## Common Interview Questions

**Q: Room mein Flow use karne ka fayda kya hai LiveData ke upar?**
> `Flow` pure Kotlin hai (Room ke DAO mein directly use hota hai), aur automatically reactive hai — jab table change ho, naya data emit hota hai bina kisi manual trigger ke. `.asLiveData()` se ise LiveData mein convert kar sakte hain UI layer ke liye.

**Q: `AndroidViewModel` kab use karte hain, plain `ViewModel` kab?**
> Jab ViewModel ko `Context`/`Application` chahiye ho (jaise Room database banane ke liye) — `AndroidViewModel`. Jab koi Context zaroorat nahi (jaise Retrofit calls) — plain `ViewModel`.

**Q: `@Insert`/`@Update`/`@Delete` `suspend fun` hain, lekin `@Query` wali function `Flow` return karti hai bina `suspend` ke — fark kyun?**
> Insert/Update/Delete **one-time operations** hain — ek baar chalti hain, complete hoti hain (`suspend` se coroutine ke andar call karte hain). `Flow` return karne wali query **continuous stream** hai — khud hi baar-baar emit karti rahegi jab data change ho, isliye `suspend` ki zaroorat nahi (Flow khud coroutine-aware hai).

**Q: Migration kyun zaroori hai, `fallbackToDestructiveMigration()` kyun use nahi karte production mein?**
> Real users ke paas app mein already data hoga. Schema change karne pe agar Migration define nahi ki, Room fallback mein **poora database delete karke naya banata hai** — users ka saara data (jaise unke saved Todos) gayab ho jaayega. Migration se purana data **naye schema mein convert** hota hai, kuch lose nahi hota.

---

## Gotchas — Real Mistakes Jo Is Session Mein Hue

**1. `lateinit var` class property assign hi nahi hua**
```kotlin
// GALAT — 'adapter' local scope mein set hua (RecyclerView ka property), class ka 'todoAdapter' kabhi initialize nahi hua
binding.rvTodos.apply {
    adapter = TodoAdapter(...)   // yeh sirf rvTodos.adapter set kar raha hai
    layoutManager = ...
    adapter = todoAdapter        // CRASH: UninitializedPropertyAccessException — todoAdapter abhi tak null/uninitialized
}

// SAHI — class property ko hi directly assign karo
binding.rvTodos.apply {
    todoAdapter = TodoAdapter(...)   // class ka todoAdapter ab initialized
    layoutManager = ...
    adapter = todoAdapter             // ab sahi kaam karega
}
```
> **Yaad rakhne wali baat:** `apply { }` block ke andar `this` = us object (yahan `rvTodos`) ka reference hai. Agar aap `adapter = ...` likhoge, yeh `rvTodos.adapter` set karega, class ka koi alag `todoAdapter` variable nahi — dono naam similar lagne se confusion ho sakta hai.

**2. Naming convention — PascalCase property**
```kotlin
// GALAT convention — PascalCase sirf class names ke liye
val TodoLiveData: LiveData<List<Todo>>

// SAHI — properties/variables camelCase mein
val todoLiveData: LiveData<List<Todo>>
```

---

## Interview Mein Bolna

> *"Room CRUD demo mein MVVM + Repository pattern local database ke saath implement kiya — bilkul Retrofit jaisa architecture, bas data source Room hai. Sabse important fark — Insert/Update/Delete suspend functions hain (one-time), lekin list fetch karne wali query Flow return karti hai — Room automatically naya data emit karta hai jab bhi table change ho, isliye UI hamesha reactive rehti hai bina manual refresh ke. AndroidViewModel use kiya kyunki Room database banane ke liye Context chahiye, jo Application object se milta hai. data class ka .copy() function immutable objects ko update karne ka tareeka hai — poora naya object banta hai sirf specify ki gayi fields change karke."*
