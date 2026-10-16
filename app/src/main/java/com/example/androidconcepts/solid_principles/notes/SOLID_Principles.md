# SOLID Principles — Notes

SOLID 5 object-oriented design principles ka set hai (Robert C. Martin / Uncle Bob ne popular kiya). Yeh code ko samajhne mein easy, badalne mein safe aur test karne mein aasan banate hain. Yeh ek concept-level topic hai, isliye koi demo app nahi hai. Har principle ko GALAT/SAHI Kotlin code aur Android ke example se samjha gaya hai.

> **Status:** Complete. Saare 5 principles (S, O, L, I, D), Galat Fehmiyaan, Interview Q aur "Interview Mein Bolna" done.

---

## Kya Hai

| Letter | Principle | Ek line mein |
|---|---|---|
| **S** | Single Responsibility | Class ke badalne ki sirf ek wajah ho |
| **O** | Open/Closed | Extension ke liye open, modification ke liye closed |
| **L** | Liskov Substitution | Child class, parent ki jagah bina kuch toode use ho sake |
| **I** | Interface Segregation | Bade interface ki jagah chhote, specific interfaces |
| **D** | Dependency Inversion | Concrete class par nahi, abstraction par depend karo |

**Kyun chahiye?** Ek hi class mein API call, DB, validation aur UI likh do toh ek chhota change bhi kuch aur tod deta hai, test likhna mushkil ho jaata hai, aur naye developer ko samajhne mein time lagta hai. SOLID in problems se bachata hai.

> Android mein MVVM + Repository SRP ka example hai, aur Hilt/DI Dependency Inversion se juda hai. Matlab SOLID ka kaafi hissa hum pehle se use kar rahe hain.

---

## S — Single Responsibility Principle (SRP)

> Ek class ke paas badalne ki **sirf ek hi wajah** honi chahiye.

"Ek kaam" ka matlab ek function nahi hai. Class mein kai functions ho sakte hain, bas sab ek hi zimmedari se jude hon.

### GALAT (violation)
```kotlin
class UserActivity : AppCompatActivity() {

    fun registerUser(email: String, password: String) {
        // 1. Validation
        if (!email.contains("@")) {
            showError("Invalid email")
            return
        }
        // 2. API call
        val response = retrofit.create(UserApi::class.java).register(email, password)
        // 3. Database
        db.userDao().insert(response.user)
        // 4. UI update
        textView.text = "Registered!"
    }
}
```
Yahan 4 wajah hain badalne ki: UI, API format, database, validation rule.

### SAHI (SRP follow)
```kotlin
class EmailValidator {                       // sirf validation
    fun isValid(email: String) = email.contains("@")
}

class UserRepository(                        // sirf data (API + DB)
    private val api: UserApi,
    private val dao: UserDao
) {
    suspend fun register(email: String, password: String) {
        val response = api.register(email, password)
        dao.insert(response.user)
    }
}

class UserViewModel(                         // sirf UI logic/state
    private val repository: UserRepository,
    private val validator: EmailValidator
) : ViewModel() { /* ... */ }

class UserActivity : AppCompatActivity()     // sirf UI dikhana
```

### Android mein kahan dikhta hai
| Class | Ek zimmedari |
|---|---|
| Activity / Fragment | UI dikhana |
| ViewModel | UI state sambhalna |
| Repository | Data ka source decide karna |
| DAO | Database queries |
| API Service | Network calls |

> **Common galti:** SRP ko "ek class = ek function" samajhna. Yeh galat hai.

---

## O — Open/Closed Principle (OCP)

> Class **extension ke liye open** ho, par **modification ke liye closed**.

Naya feature add karne ke liye purana, chalta hua code edit nahi karna padna chahiye. Naya code add karo. Purana code baar-baar edit karoge toh regression bug ka risk badhta hai.

### GALAT (violation): `when` ka chain
```kotlin
class DiscountCalculator {
    fun apply(customerType: String, total: Double): Double {
        return when (customerType) {
            "REGULAR" -> total * 0.95   // 5% off
            "PREMIUM" -> total * 0.90   // 10% off
            else -> total
        }
    }
}
```
"STUDENT" aaya toh is chalti class ko **edit** karna padega.

### SAHI (OCP follow): interface + polymorphism
```kotlin
interface Discount {
    fun apply(total: Double): Double
}

class RegularDiscount : Discount {
    override fun apply(total: Double) = total * 0.95
}

class PremiumDiscount : Discount {
    override fun apply(total: Double) = total * 0.90
}

class DiscountCalculator {
    fun calculate(discount: Discount, total: Double) = discount.apply(total)
}

// Naya type: sirf nayi class, calculator touch nahi
class StudentDiscount : Discount {
    override fun apply(total: Double) = total * 0.80
}
```

| Customer | Total | Rate | Result |
|---|---|---|---|
| Regular | ₹1000 | × 0.95 | ₹950 |
| Premium | ₹1000 | × 0.90 | ₹900 |
| Student (naya) | ₹1000 | × 0.80 | ₹800 |

### Dusra example: Analytics
```kotlin
interface AnalyticsProvider { fun logEvent(name: String) }

class FirebaseProvider : AnalyticsProvider { override fun logEvent(name: String) { /* ... */ } }
class MixpanelProvider : AnalyticsProvider { override fun logEvent(name: String) { /* ... */ } }

class AnalyticsTracker(private val providers: List<AnalyticsProvider>) {
    fun track(event: String) = providers.forEach { it.logEvent(event) }
}
```
Naya tool aaya toh `AnalyticsTracker` nahi badalta, bas list mein nayi provider class judti hai.

### Android mein kahan dikhta hai
| Example | OCP kaise |
|---|---|
| RecyclerView.Adapter | Naya ViewType add karo, RecyclerView ka code touch nahi hota |
| Retrofit/OkHttp Interceptor | Naya interceptor add karo, OkHttp edit nahi |
| Hilt `@Binds` | Nayi implementation bind karo, caller code same |
| Custom View | `View` extend karte ho, modify nahi |

### Pehchaanne ka tareeka
1. Kya kal koi naya type/case aa sakta hai?
2. Aane par kya purani class edit karni padegi?

Dono "haan" hain toh interface se abstract karo.

> **Balance:** Har `when` ko interface mein badalna zaroori nahi. 2-3 fixed cases jo kabhi nahi badlenge, unke liye simple `when` theek hai. Warna over-engineering ho jaata hai.

---

## L — Liskov Substitution Principle (LSP)

> Jahan parent use ho raha hai, wahan **child daal do** toh program ka behaviour sahi hi rehna chahiye.

Child, parent ka contract (vaada) nahi tod sakta. Zyada kar sakta hai, kam ya ulta nahi.

### Classic example: Bird
```kotlin
open class Bird { open fun fly() = println("Flying") }

class Penguin : Bird() {
    override fun fly() = throw UnsupportedOperationException("Penguin cannot fly")
}

fun makeBirdFly(bird: Bird) = bird.fly()
makeBirdFly(Penguin())   // CRASH
```
`Bird` ne `fly()` ka vaada kiya tha, `Penguin` ne tod diya. Problem `Bird` ke design mein hai (usne bahut zyada vaada kar liya).

### Red flags
- Child mein `throw UnsupportedOperationException` ya `TODO()`
- Child ka override khaali ya bekaar
- Caller ko `if (x is Penguin)` jaisa type check karna pade

### GALAT (Android): read-only repository ko write interface diya
```kotlin
interface NotesRepository {
    suspend fun getNotes(): List<String>
    suspend fun addNote(note: String)
}

class CachedNotesRepository : NotesRepository {          // read-only cache
    override suspend fun getNotes() = listOf("Buy milk")
    override suspend fun addNote(note: String) {
        throw UnsupportedOperationException("Cache read-only hai")
    }
}
```

| ViewModel ko diya | `save("Call mom")` ka result |
|---|---|
| `LocalNotesRepository` | Add hua, list: `["Buy milk", "Call mom"]` |
| `CachedNotesRepository` | **CRASH**: `UnsupportedOperationException` |

ViewModel ka code same hai, par implementation badalte hi crash. Yahi LSP violation hai.

### SAHI: interface chhota karo
```kotlin
interface NotesReader { suspend fun getNotes(): List<String> }
interface NotesWriter { suspend fun addNote(note: String) }

class LocalNotesRepository : NotesReader, NotesWriter { /* dono */ }
class CachedNotesRepository : NotesReader { /* sirf read */ }

class NotesListViewModel(private val reader: NotesReader) : ViewModel()   // Cached bhi chalega
class AddNoteViewModel(private val writer: NotesWriter) : ViewModel()     // Cached dena compile hi nahi hoga
```
Galti ab runtime crash ki jagah **compile time par** pakdi jaati hai.

### Android framework mein LSP
`Activity`/`Fragment` extend karte hue `super.onCreate()` call karna framework ka contract hai. Nahi kiya toh `SuperNotCalledException`.

> **Rule:** Child ko parent se kam nahi karna chahiye. Agar kuch support nahi karta, toh usse us parent/interface ka child banao hi mat. Yeh ISP se juda hai.

---

## I — Interface Segregation Principle (ISP)

> Class ko aise methods implement karne par majboor mat karo jo woh use hi nahi karti. Bade "fat" interface ki jagah chhote, specific interfaces banao.

### GALAT (violation): Fat interface
```kotlin
interface Worker {
    fun code()
    fun test()
    fun design()
}

class Designer : Worker {
    override fun code() { throw UnsupportedOperationException() }   // Designer code nahi karta
    override fun test() { throw UnsupportedOperationException() }
    override fun design() = println("Designing")
}
```
Designer ko aise methods implement karne pade jo woh karta hi nahi, aur woh bekaar ya crash karne wale ban gaye.

### SAHI (ISP follow): Chhote interfaces
```kotlin
interface Coder    { fun code() }
interface Tester   { fun test() }
interface Designer { fun design() }

class Developer : Coder, Tester {
    override fun code() = println("Coding")
    override fun test() = println("Testing")
}

class UiDesigner : Designer {
    override fun design() = println("Designing")
}
```

### Android mein kahan dikhta hai
```kotlin
editText.addTextChangedListener(object : TextWatcher {
    override fun beforeTextChanged(...) {}      // zaroorat nahi, par likhna pada
    override fun onTextChanged(...) {}          // zaroorat nahi, par likhna pada
    override fun afterTextChanged(s: Editable?) { /* sirf yeh chahiye */ }
})
```
`TextWatcher` mein 3 methods hain aur aksar sirf 1 chahiye, yeh halka ISP violation hai. Android KTX ne `doAfterTextChanged { }` extension se isse solve kiya. Sahi example `View.OnClickListener` hai, jisme sirf ek method `onClick()` hai.

> **LSP se connection:** LSP wale section mein `NotesRepository` ko `NotesReader` aur `NotesWriter` mein todna ISP hi tha. ISP design ka tareeka hai, LSP uska fayda.

---

## D — Dependency Inversion Principle (DIP)

> High-level class ko low-level concrete class par depend nahi karna chahiye. Dono ko **abstraction (interface)** par depend karna chahiye.

- **High-level:** jo business logic/decision leta hai (ViewModel)
- **Low-level:** jo asli kaam karta hai (Retrofit, Room, SharedPreferences)

Apni class ke andar `val x = ConcreteClass()` mat banao. Interface maango, asli object bahar se do.

### GALAT (violation)
```kotlin
class UserViewModel : ViewModel() {
    private val repository = UserRepository(RetrofitInstance.api)   // khud banaya

    fun load() = viewModelScope.launch { repository.getUsers() }
}
```
- ViewModel ko pata hai ki data Retrofit se aata hai (tight coupling)
- Unit test nahi likh sakte, test mein asli network call chalegi
- Retrofit ki jagah Room/Firebase aaya toh ViewModel edit karna padega (OCP bhi toota)

### SAHI (DIP follow)
```kotlin
interface UserRepository {                       // abstraction
    suspend fun getUsers(): List<User>
}

class UserRepositoryImpl(private val api: UserApi) : UserRepository {   // low-level
    override suspend fun getUsers() = api.getUsers()
}

class UserViewModel(
    private val repository: UserRepository       // interface maanga, concrete nahi
) : ViewModel() {
    fun load() = viewModelScope.launch { repository.getUsers() }
}
```

### Testing mein fayda
```kotlin
class FakeUserRepository : UserRepository {
    override suspend fun getUsers() = listOf(User("Kamal"), User("Rahul"))
}

val viewModel = UserViewModel(FakeUserRepository())   // network ke bina test
```
Asli app mein `UserRepositoryImpl` jaata hai, test mein `FakeUserRepository`.

### Android mein kahan dikhta hai: Hilt `@Binds`
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}
```
`@Binds` batata hai: "jahan `UserRepository` maanga jaaye, wahan `UserRepositoryImpl` do."

### DIP vs DI vs Hilt (interview mein confuse hote hain)
| | Matlab |
|---|---|
| **Dependency Inversion (DIP)** | Principle: abstraction par depend karo |
| **Dependency Injection (DI)** | Technique: object bahar se do (constructor se) |
| **Hilt** | Library: DI ko automatic karti hai |

> DI, DIP follow karne ka ek tareeka hai. Dono same cheez nahi hain.

---

## Galat Fehmiyaan

| Galat soch | Sahi baat |
|---|---|
| SRP = ek class mein ek function | SRP = badalne ki ek hi wajah. Class mein kai functions ho sakte hain |
| Har `when` ko interface mein badlo (OCP) | 2-3 fixed cases jo nahi badlenge, unke liye `when` theek hai. Warna over-engineering |
| Child class mein `throw UnsupportedOperationException` chalega | Yeh LSP violation hai, interface chhota karo |
| ISP = bahut saare interfaces banao | Sirf wahi todo jahan class ko bekaar methods implement karne pad rahe hon |
| DIP = DI = Hilt | DIP principle hai, DI technique hai, Hilt library hai |

---

## Common Interview Questions

**Q: SOLID kya hai?**
> SOLID 5 object-oriented design principles hain: Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation aur Dependency Inversion. Inka maksad code ko maintainable, testable aur flexible banana hai.

**Q: SOLID ka Android mein example do.**
> MVVM + Repository SRP hai (har layer ki ek zimmedari). Naya RecyclerView ViewType ya Interceptor add karna OCP hai. Hilt `@Binds` se interface inject karna DIP hai.

**Q: SRP kya hai?**
> Class ke badalne ki sirf ek wajah honi chahiye. Hamare project mein Activity UI handle karti hai, ViewModel state, aur Repository data. Isse code testable aur maintainable rehta hai.

**Q: OCP kya hai?**
> Naya behaviour add karne ke liye existing code modify na karna pade. Hum interface aur polymorphism use karte hain, jaise alag-alag Discount ya PaymentMethod implementations. Isse regression ka risk kam hota hai.

**Q: LSP kya hai? Violation ka example do.**
> Child class ko parent ki jagah daalne par behaviour sahi rehna chahiye. Agar child mein `UnsupportedOperationException` throw karna pade, toh LSP toot raha hai. Jaise read-only cache repository ko write wale interface se implement karna. Fix: interface ko `NotesReader` aur `NotesWriter` mein todna.

**Q: ISP kya hai?**
> Class ko aise methods implement karne par majboor nahi karna chahiye jo woh use nahi karti. Isliye ek bade interface ki jagah chhote, specific interfaces banate hain. Jaise `NotesReader` aur `NotesWriter` alag-alag.

**Q: Dependency Inversion aur Dependency Injection mein kya fark hai?**
> DIP ek principle hai: high-level module abstraction par depend kare, concrete class par nahi. DI ek technique hai: dependency bahar se (constructor se) di jaaye. Hilt ek library hai jo DI automate karti hai. DI, DIP follow karne ka tareeka hai.

---

## Interview Mein Bolna

> *"SOLID 5 design principles ka set hai jo code ko maintainable aur testable banata hai. Hamare project mein Activity sirf UI handle karti hai, ViewModel state, aur Repository data, yeh SRP hai. Naya behaviour interface aur polymorphism se add karte hain bina purana code edit kiye, yeh OCP hai. Child class parent ka contract nahi todti, aur interfaces chhote rakhte hain taaki class ko bekaar methods implement na karne pade, yeh LSP aur ISP hain. ViewModel Repository ka interface maangta hai aur Hilt `@Binds` se implementation inject hoti hai, yeh DIP hai. Isse unit test mein fake repository daalna aasan ho jaata hai."*
