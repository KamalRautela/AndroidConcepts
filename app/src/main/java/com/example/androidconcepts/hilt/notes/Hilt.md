# Hilt (Dependency Injection) — Notes

Manual DI (jo Retrofit/Room/DataStore mein use kiya tha — constructor default values) se Hilt DI mein migrate kiya. Do parallel demos: **Retrofit + Hilt** (Comments feature) aur **Room + Hilt** (Animal feature) — dono purane manual-DI wale features (`GetApiRepository`, `TodoRepository`) ko chhue bina, naye `hilt` package mein banaye, taaki "pehle vs baad" ka comparison saaf rahe.

---

## DI Basics — Kya Hai, Kyun Chahiye

**Dependency** — koi class jiski zaroorat kisi doosri class ko hai (jaise `ApiService`, `Repository` ki dependency hai).
**Injection** — dependency ko class ke andar khud banane ki jagah, **bahar se de dena**.

**Manual DI (jo pehle karte the):**
```kotlin
class DatastoreViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DatastoreRepository(application.datastore)   // khud bana raha hai
}
```
Problem — testing mushkil (fake dependency inject nahi kar sakte), tight coupling, app bada hone pe manual wiring complex ho jaati hai.

**Hilt** — yeh wiring **automatically** karta hai. Bas ek baar batao "yeh class kaise banti hai", Hilt jahan bhi zaroorat ho khud de deta hai.

---

## Core Pieces

| Piece | Kaam |
|---|---|
| `@HiltAndroidApp` | `Application` class pe — Hilt ka root DI container start karta hai |
| `@Inject constructor` | Apni khud ki class ke liye — "isse banane ka tareeka yeh hai" |
| `@Module` + `@Provides` | Third-party class (Retrofit, Room Database) ya interface (ApiService, Dao) ke liye — factory method se recipe |
| `@InstallIn(SingletonComponent::class)` | Module kis scope mein available hai (yahan — poore app ke liye) |
| `@AndroidEntryPoint` | Activity/Fragment ko Hilt-aware banata hai |
| `@HiltViewModel` | ViewModel ko Hilt se banane layak banata hai |
| `@Singleton` | Poore app-lifetime mein **sirf ek instance** — Hilt khud caching/thread-safety manage karta hai |

---

## Rule — Kab `@Inject constructor`, Kab `@Module`

| Situation | Kya karo |
|---|---|
| Apni khud ki class (source code apne paas hai) | Seedha `@Inject constructor` |
| Third-party class (Retrofit, OkHttpClient, Room Database) ya interface jiska instance factory method se banta hai (ApiService, Dao) | `@Module` + `@Provides` likhna zaroori — Hilt khud guess nahi kar sakta |

---

## Code — NetworkModule (Retrofit)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    @Provides
    @Singleton
    fun providesOkhttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        return OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
    }

    @Provides
    @Singleton
    fun providesRetrofitClient(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build()
    }

    @Provides
    @Singleton
    fun providesApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
```

**Chaining** — `providesRetrofitClient(okHttpClient: OkHttpClient)` mein parameter maanga, Hilt khud samajh jaata hai "pehle `OkHttpClient` banao (isi module se), phir Retrofit ko do." Poora dependency graph khud resolve hota hai.

## Code — DatabaseModule (Room)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providesDataBase(@ApplicationContext context: Context): AnimalDatabase {
        return Room.databaseBuilder(context, AnimalDatabase::class.java, "animal_database").build()
    }

    @Provides
    @Singleton
    fun providesDao(database: AnimalDatabase): AnimalDao {
        return database.animalDao()
    }
}
```

**`@ApplicationContext`** — qualifier annotation, Hilt ko batata hai "Activity ka Context nahi, poore App ka Context do" (jo manually `context.applicationContext` likhne ke barabar hai, purane `RoomInstance` mein jaisa tha).

---

## Code — Repository → ViewModel → Activity (dono features mein same pattern)

```kotlin
// Repository — apni class, seedha @Inject constructor
class CommentRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getComments(): List<Comment> = apiService.getComments()
}

// ViewModel — @HiltViewModel, plain ViewModel() (AndroidViewModel/Application ki zaroorat nahi)
@HiltViewModel
class CommentViewModel @Inject constructor(
    private val repository: CommentRepository
) : ViewModel() {
    private val _commentLiveData = MutableLiveData<GetApiResource<List<Comment>>>()
    val commentLiveData: LiveData<GetApiResource<List<Comment>>> = _commentLiveData

    fun getComments() {
        _commentLiveData.postValue(GetApiResource.Loading)
        viewModelScope.launch {
            try {
                _commentLiveData.postValue(GetApiResource.Success(repository.getComments()))
            } catch (e: Exception) {
                _commentLiveData.postValue(GetApiResource.Error(e.message ?: "Unknown Error"))
            }
        }
    }
}

// Activity — @AndroidEntryPoint, by viewModels() bina manual factory ke
@AndroidEntryPoint
class CommentActivity : AppCompatActivity() {
    private val viewModel: CommentViewModel by viewModels()
    ...
}
```

---

## Manual Singleton vs `@Singleton` — Fark

**Pehle (RoomInstance — manual):**
```kotlin
object RoomInstance {
    @Volatile private var INSTANCE: TodoDatabase? = null
    fun getDatabase(context: Context): TodoDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(...).build()
            INSTANCE = instance
            instance
        }
    }
}
```
Manual caching + thread-safety (`@Volatile`, `synchronized`) khud likhna padta tha.

**Ab (Hilt):**
```kotlin
@Provides
@Singleton
fun providesDataBase(@ApplicationContext context: Context): AnimalDatabase { ... }
```
`@Singleton` bolte hi Hilt khud caching + thread-safety generated code mein manage kar deta hai — humein `INSTANCE`/`synchronized` likhna hi nahi padta.

---

## Common Interview Questions

**Q: Dependency Injection kyun use karte hain?**
> Loose coupling ke liye — class khud apni dependency nahi banati, bahar se milti hai. Isse testing aasan hoti hai (fake dependency inject kar sakte ho), aur app bada hone pe wiring manage karna simple rehta hai.

**Q: `@Inject constructor` aur `@Module`+`@Provides` mein kab kya use karein?**
> Apni khud likhi class ho toh `@Inject constructor` seedha class pe laga do. Third-party class (Retrofit, Room Database) ya interface (jiska instance factory method se banta ho, jaise ApiService/Dao) ho toh `@Module`+`@Provides` likhna padta hai, kyunki unke constructor pe `@Inject` laga hi nahi sakte.

**Q: `@Singleton` kya karta hai?**
> Us dependency ka poore app-lifetime mein sirf ek instance banta hai, cache ho jaata hai — dubara request pe wahi cached instance milta hai. Manually jo `INSTANCE`/`synchronized`/`@Volatile` likhna padta tha, Hilt yeh generated code mein khud karta hai.

**Q: `@HiltViewModel` ke bina kya problem hoti?**
> `by viewModels()` ko pata nahi chalega ViewModel kaise banayi jaaye agar uske constructor mein dependencies ho (jaise Repository) — crash aayega. `@HiltViewModel` Hilt ko batata hai ki is ViewModel ko uske special ViewModelProvider.Factory se banao.

**Q: `object` Module kyun, `class` nahi?**
> Module stateless hota hai (sirf recipes deta hai, koi instance-level data nahi) — Kotlin `object` = automatic singleton, Dagger ko module ka instance banane ki zaroorat hi nahi padti, seedha static-jaisa method call ho jaata hai.

---

## Interview Mein Bolna

> *"Hilt Dagger ke upar ek Android-specific wrapper hai jo dependency injection ko simplify karta hai. `@HiltAndroidApp` se root container start hota hai, apni classes pe `@Inject constructor` laga dete hain, third-party classes (Retrofit, Room) ke liye `@Module`+`@Provides` likhte hain. `@AndroidEntryPoint` Activity/Fragment ko container se jodta hai, `@HiltViewModel` ViewModel ko bina manual factory ke inject karne layak banata hai. `@Singleton` scope se ek hi instance poore app mein reuse hota hai — jo pehle humein manually `@Volatile`+`synchronized` singleton likh ke karna padta tha, Hilt woh generated code se automatically karta hai."*
