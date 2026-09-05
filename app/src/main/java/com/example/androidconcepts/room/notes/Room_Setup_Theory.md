# Room Database — Setup Theory Notes

Room ka foundation — kya hai, SQLite se fark, aur Entity/DAO/Database ka setup. Isse pehle koi actual project code nahi bana — yeh pure theory + example code hai, jab implementation shuru hogi tab isi pattern ko real files mein dalenge.

---

## Kya Hai

**Room** — Google ka **ORM (Object-Relational Mapping) library** hai, jo **SQLite ke upar** built hai. Room khud database engine nahi hai — SQLite ke upar ek convenient, type-safe wrapper hai.

```
Aapka Code → Room (declarative layer) → SQLite (actual database engine) → Disk pe file
```

> **Retrofit se parallel:** Bilkul wahi relationship jaisa Retrofit/OkHttp ka tha. Retrofit = declarative layer upar, OkHttp = actual networking neeche. Room = declarative layer upar, SQLite = actual database engine neeche.

---

## Room Se Pehle — Raw SQLite Ki Problems

```kotlin
val db = SQLiteDatabase.openOrCreateDatabase("app.db", null)
db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY, name TEXT)")

val cursor = db.rawQuery("SELECT * FROM users", null)
cursor.moveToFirst()
val name = cursor.getString(cursor.getColumnIndex("name"))
cursor.close()
```

| Problem | Kyun |
|---|---|
| Raw SQL strings | Typo ho toh **runtime pe crash**, compile-time pe pata nahi chalta |
| Manual `Cursor` handling | Bhoolne se memory leak, boilerplate zyada |
| Koi type safety nahi | Column se manually value nikaalni padti hai, galat type daal sakte ho |

## Room Ke Saath

```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
}
```
Room **compile-time pe SQL query verify karta hai** (galat column naam likha toh build hi fail ho jaayega), aur result **automatically Kotlin objects** mein aata hai — `Cursor` manually handle nahi karna.

---

## 3 Core Building Blocks

| Component | Kya Hai |
|---|---|
| `@Entity` | `data class` jo **ek table** represent karti hai |
| `@Dao` | Interface jisme database operations annotations se define hote hain (Retrofit ke `ApiService` jaisa concept) |
| `@Database` (abstract class) | Entity + DAO ko jodta hai, Room yahan se poora database implementation generate karta hai |

### 1. `@Entity` — Table Define Karna

```kotlin
@Entity(tableName = "posts")
data class Post(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val body: String
)
```
- `@Entity(tableName = "posts")` — is data class se `posts` table banega
- `@PrimaryKey(autoGenerate = true)` — `id` primary key, Room khud auto-increment karega
- Baaki properties automatically columns ban jaate hain (property naam = column naam)

### 2. `@Dao` — Operations Define Karna

```kotlin
@Dao
interface PostDao {
    @Insert
    suspend fun insertPost(post: Post)

    @Query("SELECT * FROM posts")
    suspend fun getAllPosts(): List<Post>

    @Delete
    suspend fun deletePost(post: Post)
}
```
- `@Insert`, `@Delete`, `@Update` — predefined annotations, koi SQL likhne ki zaroorat nahi
- `@Query` — custom SQL chahiye ho tab (jaise filtering) — Room compile-time pe validate karta hai

### 3. `@Database` — Sabko Jodna

```kotlin
@Database(entities = [Post::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
}
```
- `entities = [...]` — kaunse Entities (tables) is database mein hain
- `version` — schema version (Migrations ke liye zaroori)
- `abstract class` — Room khud implementation generate karta hai (annotation processing se)

---

## Singleton Pattern — Database Instance Banana

```kotlin
object RoomInstance {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}
```

### Naye Concepts

- **`@Volatile`** — `INSTANCE` variable ki value **saare threads ko turant dikhe** (memory visibility guarantee) — multi-threaded environment ke liye zaroori.
- **`INSTANCE ?: synchronized(this) { ... }`** — pehle check karo instance pehle se hai kya. Nahi toh `synchronized` block mein **thread-safe** tareeke se banao, taaki 2 threads same time pe alag-alag database na bana dein.
- **`Room.databaseBuilder(context, AppDatabase::class.java, "app_database")`** — `Retrofit.Builder()` jaisa Builder pattern. Context, Database class, aur DB file ka naam chahiye.
- **`context.applicationContext`** — Activity context nahi, **Application-level context** — warna Activity destroy hone pe memory leak ho sakta hai.

### Fark Retrofit Se — Context Kyun Chahiye Yahan

Retrofit ko Context nahi chahiye tha (sirf network calls, koi file system access nahi). Room ko **Context chahiye** kyunki database **ek actual file hai device storage pe** — usko banane/access karne ke liye Android context zaroori hai.

---

## Common Interview Questions

**Q: Room aur SQLite mein fark?**
> SQLite ek raw database engine hai — Android mein built-in hai, lekin use karna verbose aur error-prone hai (raw SQL strings, manual Cursor handling). Room SQLite ke upar ek ORM layer hai jo compile-time verification, type safety, aur boilerplate reduction deta hai — SQL likhna kam padta hai.

**Q: `@Dao` interface ka implementation kaun likhta hai?**
> Aap sirf interface + annotations likhte ho, body nahi. Room **annotation processing** (compile-time code generation) se khud iska implementation generate karta hai — Retrofit ke dynamic proxy (runtime) se alag mechanism hai, lekin similar spirit: "aap declare karo, framework implement kare".

**Q: `Room.databaseBuilder()` ko singleton (`object`) mein kyun banate hain?**
> Database instance banana expensive hai (file system access, schema setup). Poori app mein **ek hi instance** honi chahiye — multiple instances se data inconsistency aur resource waste ho sakta hai.

**Q: `applicationContext` kyun use karte hain, Activity context kyun nahi?**
> Room database Activity se zyada time tak zinda rehta hai (poori app ke lifecycle tak). Agar Activity context hold kiya, toh Activity destroy hone ke baad bhi uska reference bacha rahega — **memory leak**. `applicationContext` poori app ke lifecycle tak valid rehta hai, safe hai.

**Q: `@Volatile` aur `synchronized` ka combo kyun chahiye singleton mein?**
> Yeh **Double-Checked Locking** pattern hai. `@Volatile` ensure karta hai variable ki latest value saare threads ko dikhe. `synchronized` ensure karta hai ki agar 2 threads **ek saath** pehli baar database maangein, dono alag instance na bana dein — race condition avoid hoti hai.

---

## Interview Mein Bolna

> *"Room SQLite ke upar built ek ORM library hai — raw SQL, manual Cursor handling aur runtime crashes se bachata hai kyunki compile-time pe queries verify hoti hain. Teen core pieces hain — Entity (table), DAO (operations, annotation-based interface), aur Database (dono ko jodta hai, Room annotation processing se implementation generate karta hai). Database instance banana expensive hai isliye singleton pattern use karte hain, Double-Checked Locking (@Volatile + synchronized) ke saath taaki multi-threaded access mein bhi sirf ek hi instance bane. applicationContext use karte hain kyunki database Activity se zyada time tak zinda rehta hai — Activity context use karne se memory leak ho sakta hai."*
