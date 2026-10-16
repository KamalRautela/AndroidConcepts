# Android Developer — Revision Plan
**Goal:** 0-2 Year Junior Android Developer interviews crack karna
**Target:** Job switch by December 2026

---

## Job Market Research (July 2026)
LinkedIn, Indeed, Naukri pe JDs dekh ke ye topics nikale:

**Har JD mein Required:**
- Kotlin
- Android SDK (Activity, Fragment, Lifecycle)
- MVVM Architecture
- Jetpack (ViewModel, LiveData, Room, Navigation)
- Retrofit + OkHttp
- Coroutines + Flow
- RecyclerView
- Git

**Mostly Required:**
- Hilt (Dependency Injection)
- ViewBinding
- Material Design

**Good to Have (abhi nahi):**
- Jetpack Compose
- Unit Testing
- Clean Architecture

---

## Learning Order

```
1. Kotlin
      ↓
2. Android Basics (Activity, Fragment, Intents, Permissions)
      ↓
3. RecyclerView
      ↓
4. MVVM + ViewModel + LiveData / StateFlow
      ↓
5. Retrofit + Coroutines + Error Handling
      ↓
6. Room Database
      ↓
7. Hilt (Dependency Injection)
      ↓
8. Navigation Component
      ↓
9. Local Storage (SharedPreferences / DataStore)
      ↓
10. Android Components (Services / Broadcast Receivers)
      ↓
11. WorkManager
      ↓
12. Unit Testing Basics
      ↓
13. Firebase / Firestore
      ↓
[Interview Ready]
```

---

## Detailed Topics

### 1. Kotlin
| Topic | Subtopics |
|---|---|
| Basics | Variables, Data Types, Functions, Null Safety |
| OOP | Classes, Objects, Inheritance, Interfaces, Data Classes |
| Collections | List, Map, Set, filter/map/reduce |
| Higher Order Functions | Lambda, it, also, let, apply, run, with |
| Coroutines | suspend, launch, async, Dispatchers, withContext |
| Flow | Flow, StateFlow, SharedFlow, collect |
| Sealed Class vs Enum | Kab kya use karein, exhaustive `when` |
| Object vs Companion Object | Singleton, factory methods, static-like members |
| Scope Functions | `let`, `also`, `apply`, `run`, `with` — fark aur use case |
| Extension Functions | Existing class mein bina inheritance ke naya function add karna |
| Generics | Generic classes/functions, `in`/`out` variance |
| Lateinit & Lazy | Fark, kab kaunsa use karein |
| Delegation | Class delegation (`by`), property delegation (`lazy`, `observable`) |

---

### 2. Android Basics
| Topic | Subtopics |
|---|---|
| Activity Lifecycle | onCreate→onDestroy, rotation, back press |
| Save Instance State | onSaveInstanceState, Bundle, ViewModel vs Bundle |
| Intents | Explicit, Implicit, putExtra, Parcelable |
| Intent Flags | NO_HISTORY, SINGLE_TOP, CLEAR_TOP, CLEAR_TASK, Launch Modes |
| Fragment Basics | Lifecycle, _binding null, onCreateView vs onViewCreated |
| Fragment Manager | add() vs replace(), commit, addToBackStack |
| Back Stack | popBackStack variants, named back stack |
| Fragment Communication | Interface, Result API, Shared ViewModel |
| Permissions | Types, Runtime request, Rationale, Permanently denied |
| Basic Views | TextView, Button, EditText, ImageView |
| Selection Widgets | CheckBox, RadioButton, Switch |
| Layouts | ConstraintLayout, LinearLayout, RelativeLayout, FrameLayout, ScrollView |
| UI Components | CardView, AlertDialog, SnackBar, FAB, ProgressBar, BottomNavigation |
| Image Loading | Glide, Coil |

*(RecyclerView alag se Section 3 mein detail ke saath cover hai)*

---

### 3. RecyclerView
| Topic | Subtopics |
|---|---|
| Basics | Adapter, ViewHolder, LayoutManager |
| ViewBinding in Adapter | |
| Click Listeners | Item click, button inside item |
| DiffUtil | ListAdapter, DiffUtil.Callback |
| Multiple ViewTypes | Header/Footer, different item types |
| Item Spacing (extra — cover ho chuka) | `ItemDecoration` se dynamic spacing (Grid/Linear layouts) |

---

### 4. MVVM + Jetpack
| Topic | Subtopics |
|---|---|
| ViewModel | viewModels(), activityViewModels(), scope |
| LiveData | observe(), MutableLiveData, transformations |
| StateFlow | MutableStateFlow, collect, lifecycleScope |
| LiveData vs StateFlow | Fark, kab kya use karein |
| MVVM Pattern | Model, View, ViewModel — data flow |
| Repository Pattern | ViewModel → Repository → DataSource |

---

### 5. Retrofit + Coroutines
| Topic | Subtopics |
|---|---|
| JSON Basics | Object, Array, Parsing |
| REST API Basics | GET, POST, Headers, Status Codes |
| Retrofit Setup | Builder, BaseUrl, GsonConverter |
| API Service | @GET, @POST, @Query, @Path, @Body, @Header |
| Coroutines with Retrofit | suspend functions, viewModelScope |
| Error Handling | try-catch, Resource/Result wrapper |
| Interceptors | Logging, Auth headers |

---

### 6. Room Database
| Topic | Subtopics |
|---|---|
| Room Basics | Entity, DAO, Database |
| DAO Operations | @Insert, @Update, @Delete, @Query |
| Room + ViewModel | Repository pattern |
| Flow with Room | Live data from DB |
| Migrations | Schema changes |

---

### 7. Hilt
| Topic | Subtopics |
|---|---|
| DI Basics | Kya hai, kyun chahiye |
| Hilt Setup | @HiltAndroidApp, @AndroidEntryPoint |
| Modules | @Module, @Provides, @Binds |
| Inject | @Inject constructor, field injection |
| Scopes | @Singleton, @ActivityScoped |
| Hilt + ViewModel | @HiltViewModel |
| Hilt + Retrofit/Room | Module mein provide karna |

---

### 8. Navigation Component
| Topic | Subtopics |
|---|---|
| Setup | NavGraph, NavHostFragment, NavController |
| Navigate | navigate(), action |
| Pass Data | Safe Args |
| Back Stack | popUpTo, inclusive |
| Bottom Navigation | setupWithNavController |

---

### 9. Local Storage (SharedPreferences / DataStore)
| Topic | Subtopics |
|---|---|
| SharedPreferences | getSharedPreferences, edit/apply vs commit, common use cases |
| DataStore | Preferences DataStore, Flow-based reactive reads |
| SharedPreferences vs DataStore | Kab kya use karein, DataStore kyun modern recommendation hai |

---

### 10. Android Components (Services & Broadcast Receivers)
| Topic | Subtopics |
|---|---|
| Services | Started vs Bound Service, lifecycle |
| Foreground Service | Notification requirement, use cases (jaise music player) |
| Broadcast Receivers | Static vs Dynamic registration, common system broadcasts |
| Content Providers | Basic concept (junior role ke liye low priority, bas awareness) |

---

### 11. WorkManager
| Topic | Subtopics |
|---|---|
| WorkManager Basics | OneTimeWorkRequest, PeriodicWorkRequest |
| Constraints | Network, Battery, Charging conditions |
| WorkManager vs Service vs Coroutines | Kab kaunsa use karein — guaranteed background work vs foreground work vs in-app async |

---

### 12. Unit Testing Basics
| Topic | Subtopics |
|---|---|
| JUnit Basics | @Test, assertions (assertEquals, assertTrue) |
| Mockito | Mock objects, verify(), fake dependencies |
| ViewModel Testing | LiveData/coroutines test karna (InstantTaskExecutorRule, runTest) |

---

### 13. Firebase / Firestore
| Topic | Subtopics |
|---|---|
| Firebase Setup | google-services.json, Firebase Console se app link karna |
| Firebase Authentication | Email/Password, Google Sign-In |
| Firestore Basics | Collections, Documents, CRUD operations |
| Firestore Real-time Listeners | addSnapshotListener — reactive updates bina manual refresh ke |
| Firestore vs Room | Cloud vs Local database, kab kya use karein, offline caching |
| FCM (Push Notifications) | Basic setup, notification receive karna |

---

## Progress Tracker

| Section | Status |
|---|---|
| Kotlin | 🔄 Almost Done — Flow (StateFlow, SharedFlow) pending, revisit baad mein |
| Android Basics | ✅ Done (AndroidConcepts) |
| RecyclerView | 🔄 Basic demo exists — DiffUtil/ListAdapter/ViewTypes pending, revisit baad mein |
| MVVM + Jetpack | ✅ Done — ViewModel/LiveData/StateFlow/MVVM Pattern/Shared ViewModel (via fragment_communication) covered; Repository Pattern deferred to Retrofit+Room |
| Retrofit + Coroutines | ✅ Done — JSON Parsing, Setup, GET, POST, PUT, DELETE (poora CRUD) — MVVM+Repository+sealed Resource pattern; Repository Pattern (MVVM section se deferred) yahin implement hua |
| Room | ✅ Done — Entity/DAO/Database/Singleton Setup, poora CRUD + Flow reactive updates (Todo List demo, MVVM+Repository), Migrations (concept-level) |
| Hilt | ✅ Done — DI Basics, Setup (@HiltAndroidApp/@AndroidEntryPoint), Modules (@Module/@Provides — NetworkModule/DatabaseModule), @Inject constructor, @Singleton scope, @HiltViewModel — Retrofit (Comments) aur Room (Animal) dono migrate karke dikhaya, manual DI (RetrofitInstance/RoomInstance) untouched reference ke liye rakha |
| Navigation | ✅ Done — Setup (nav_graph, NavHostFragment, NavController), Navigate + Safe Args (type-safe data pass), Back Stack (popUpTo/popUpToInclusive), Bottom Navigation (setupWithNavController) — ek demo mein sab 5 subtopics |
| Local Storage (SharedPreferences/DataStore) | ✅ Done — dono topics MVVM+Repository+AndroidViewModel pattern ke saath (Room-consistent architecture, "least privilege" — Repository ko Context nahi, sirf specific tool) |
| Android Components (Services/Broadcast Receivers) | ✅ Done — Services (Started/Foreground/Bound), Broadcast Receivers (Static/Dynamic, system broadcast restriction, protected broadcasts, intent extras), Content Providers (concept-level notes) |
| WorkManager | ✅ Done — OneTimeWorkRequest + CoroutineWorker demo (button → enqueue → observe WorkInfo via LiveData), Constraints (NetworkType.CONNECTED + battery not low), inputData/outputData via workDataOf(), WorkInfo states + isFinished |
| Unit Testing Basics | ⏳ Pending |
| Firebase / Firestore | ⏳ Pending |

---

## Apps to Build (Practice)
1. **Todo App** — MVVM + Room + RecyclerView + Hilt
2. **News App** — Retrofit + RecyclerView + MVVM + Hilt
3. **Weather App** — Retrofit + Room (cache) + MVVM + Hilt + Navigation

---

*Last Updated: 12 October 2026 — WorkManager done (OneTimeWorkRequest, CoroutineWorker, Constraints, inputData/outputData, enqueue + observe WorkInfo); Broadcast Receivers aur Services pehle se done*
