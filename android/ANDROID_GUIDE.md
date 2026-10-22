# BoardGO — Panduan Lengkap Migrasi ke Jetpack Compose (UTS Pemrograman Mobile)

Aplikasi **BoardGO** (katalog & komunitas board game) versi Android dengan
**Jetpack Compose + MVVM + Dummy Data**. Semua data bersifat dummy — tidak ada
backend, database (Room/Supabase), maupun akses hardware sungguhan.

> Folder web app asli (`src/`, `package.json`, dll.) TIDAK dihapus — project
> Android berada di folder `android/` ini agar kedua versi bisa hidup berdampingan.

---

## 1. Cara Menjalankan di Android Studio

1. Buka **Android Studio** (Ladybug/Meerkat atau lebih baru).
2. **File → Open…** → pilih folder `android/` (bukan folder root repo).
3. Tunggu **Gradle Sync** selesai (unduhan pertama ± 2–5 menit).
   - Jika diminta, klik *Trust Project*.
   - Android Studio akan membuat `local.properties` otomatis (SDK path).
4. Pilih device: **emulator** (mis. Pixel 7 API 34+) atau HP fisik (USB debugging).
5. Klik tombol **Run ▶**. Selesai.

**Versi toolchain** (sudah teruji compile di repo ini):

| Komponen        | Versi        |
|-----------------|--------------|
| AGP             | 9.3.3        |
| Kotlin (built-in AGP 9) | 2.2.10 |
| Gradle          | 9.5.0        |
| compileSdk      | 37           |
| minSdk / targetSdk | 24 / 37  |
| Compose BOM     | 2026.09.00   |
| Navigation Compose | 2.10.2   |
| Lifecycle       | 2.11.0       |

> Catatan AGP 9: plugin `kotlin-android` sudah TIDAK dipakai karena AGP 9
> memiliki **built-in Kotlin**. Cukup pasang plugin `org.jetbrains.kotlin.plugin.compose`.

Build dari terminal (opsional):

```bash
cd android
./gradlew assembleDebug     # build APK debug
./gradlew testDebugUnitTest # jalankan unit test validasi
```

---

## 2. Setup Awal Compose (Kriteria 1)

**`android/gradle/libs.versions.toml`** — semua versi terpusat di version catalog.
**`android/app/build.gradle.kts`** — konfigurasi utama:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)   // Compose compiler plugin
}

android {
    namespace = "id.boardgo.app"
    compileSdk = 37
    ...
    buildFeatures {
        compose = true                   // <-- MENGAKTIFKAN JETPACK COMPOSE
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom)) // Compose BOM
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
}
```

**`MainActivity.kt`** — mewarisi **ComponentActivity** + **setContent**:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BoardGoTheme {                 // theme custom
                val viewModel: BoardGOViewModel = viewModel()
                BoardGoNavHost(viewModel = viewModel)
            }
        }
    }
}
```

---

## 3. Daftar Layar (Kriteria 2) — 5 Layar Compose

| # | Layar | File | Teknologi Utama |
|---|-------|------|-----------------|
| 1 | **Welcome / Landing** | `ui/screens/welcome/WelcomeScreen.kt` | Slogan "FIND PEOPLE. FIND GAMES. PLAY TOGETHER." + foto papan asli |
| 2 | **Register / Login** | `ui/screens/auth/AuthScreen.kt` | "GRAB A SEAT"/"WELCOME BACK" + validasi nama/email/password (min 6) |
| 3 | **Home (dashboard)** | `ui/screens/home/HomeScreen.kt` | "FIND A TABLE IN 30 SEC", chip filter, kartu Featured + Join (mockup) |
| 4 | **List / Discover** | `ui/screens/list/GameListScreen.kt` | **`LazyColumn`** + pencarian + filter kategori + cover foto game |
| 5 | **Nearby (jarak GPS)** | `ui/screens/nearby/NearbyScreen.kt` | **Peta mock**: pin venue (jarak+bearing), lingkaran radius, chip 1–10 km, zoom/recenter |
| 6 | **Detail Game** | `ui/screens/detail/GameDetailScreen.kt` | Argumen `gameId`, stat blocks, kartu sesi + SeatPill + Join |
| 7 | **Form / Input** | `ui/screens/form/CreateSessionScreen.kt` | Form validasi angka & email |
| 8 | **My Games** | `ui/screens/mygames/MyGamesScreen.kt` | Tab **Upcoming / Hosting / Past** (Hosting = sesi yang kita host, hasil Publish juga masuk) |
| 9 | **Chat Sesi** | `ui/screens/chat/ChatScreen.kt` | Bubble chat (masuk/keluar/sistem), input "Message the table", argumen `sessionId` |
| 10 | **Profil** | `ui/screens/profile/ProfileScreen.kt` | Statistik, favorit, **toggle dark mode** (System/Light/Dark), log out |
| 11 | **Notifications** | `ui/screens/notifications/NotificationsScreen.kt` | Daftar notifikasi + unread dot + "mark all read"; tap → chat/detail; push otomatis saat Join/Publish |

---

## 4. UI Theming — Material 3 Custom (Kriteria 3)

Semua file di `ui/theme/` **dibuat sendiri**, bukan template default Android Studio:

- **`Color.kt`** — palet warna BoardGO hasil konversi OKLCH dari web app
  (primary oranye `#F84F2F`, sky blue, success, dll).
- **`Type.kt`** — memakai **font asli dari design web app** (di-bundle di `res/font/`):
  - **Anton** (`anton_regular.ttf`) untuk semua judul display — selalu UPPERCASE
  - **JetBrains Mono** (`jetbrainsmono_*.ttf`) untuk label kecil uppercase ber-letter-spacing
  - Sans-serif untuk teks isi (pengganti Inter)
- **`Shape.kt`** — `BoardGoShapes` (radius kartu 20 dp, chip pill).
- **`Theme.kt`** — `BoardGoTheme()` memasang colorScheme + typography + shapes
  ke `MaterialTheme`. Dipakai di MainActivity.

**Aset visual asli** dari repo web juga dibundel di `res/drawable/game_*.jpg`
(12 foto game: Catan, Wingspan, Azul, …) — dipakai untuk cover kartu, hero detail,
thumbnail My Games & Profil, sehingga tampilan identik dengan app web.

**Dark mode manual**: `Theme.kt` menyediakan skema gelap navy (`BoardGoDark`) +
skema terang; `MainActivity` memilih berdasarkan state `ThemeMode`
(System / Light / Dark) dari ViewModel. Toggle ada di **Profil → APPEARANCE**.

**Fitur jarak GPS (Nearby)**: layar `NearbyScreen` menampilkan peta mock
(jalan/sungai/taman digambar Canvas), pin venue diposisikan dari **jarak (km) +
bearing** memakai proyeksi sama seperti web app (`venuePoint`), lingkaran radius
pencarian, chip radius 1/2/3/5/10 km, kontrol zoom + recenter, dan daftar sesi
terurut jarak dengan badge km — semuanya dari dummy data (tanpa GPS asli).

---

## 5. State Management (Kriteria 4)

| Konsep | Penggunaan di aplikasi |
|--------|------------------------|
| **ViewModel** | `ui/viewmodel/BoardGOViewModel.kt` — state holder tunggal (MVVM) berisi `StateFlow` tiap layar: list, detail, form, fitur native, profil |
| **StateFlow + collectAsStateWithLifecycle** | Setiap layar membaca state dari ViewModel secara lifecycle-aware |
| **mutableStateOf / remember** | Dipakai untuk state lokal UI (mis. `collectAsStateWithLifecycle` menghasilkan state snapshot) |
| **Loading state** | `LoadStatus { LOADING, SUCCESS, ERROR }` + simulasi `delay()` di `viewModelScope` |

Pola MVVM di aplikasi ini:

```
View (Composable)  --memanggil event-->  ViewModel (StateFlow, validasi, dummy repository)
     ^                                          |
     +-----------membaca state (collectAsState)-+
```

---

## 6. Navigasi & Passing Argument (Kriteria 5)

**`ui/navigation/BoardGoNavHost.kt`** memakai `NavHost` + `NavController`:

| Route | Keterangan |
|-------|------------|
| `list` | Layar List (start destination) |
| `detail/{gameId}` | **Argumen wajib**: gameId dikirim dari List/Profil |
| `create?gameId={gameId}` | **Argumen opsional**: gameId dikirim dari Detail (pra-isi form) |
| `native` | Layar Fitur Native |
| `profile` | Layar Profil |

Contoh passing argument:

```kotlin
// Dari List → Detail
onGameClick = { gameId -> navController.navigate("detail/$gameId") }

// Dari Detail → Form (pra-isi game)
onCreateSession = { gameId -> navController.navigate("create?gameId=$gameId") }
```

Menerima argumen di tujuan:

```kotlin
composable(
    route = "detail/{gameId}",
    arguments = listOf(navArgument("gameId") { type = NavType.StringType })
) { entry ->
    val gameId = entry.arguments?.getString("gameId").orEmpty()
    GameDetailScreen(gameId = gameId, ...)
}
```

---

## 7. Form Input & Validasi (Kriteria 6)

Di `CreateSessionScreen.kt` + `BoardGOViewModel.kt`:

### a. Field ANGKA — Jumlah Pemain
```kotlin
OutlinedTextField(
    value = state.maxPlayers,
    onValueChange = viewModel::onFormMaxPlayersChange, // filter: hanya digit
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Number   // <-- keyboard angka
    ),
    isError = state.maxPlayersError != null
)
```
Validasi ViewModel:
- Hanya karakter `isDigit()` yang diterima (karakter lain dibuang otomatis).
- Harus bernilai **2–12** (`maxPlayersError` = "Jumlah pemain harus 2–12").

### b. Field EMAIL — Email Kontak
```kotlin
OutlinedTextField(
    value = state.contactEmail,
    onValueChange = viewModel::onFormEmailChange,
    keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Email    // <-- keyboard email
    ),
    isError = state.contactEmailError != null
)
```
Validasi regex di ViewModel:
```kotlin
fun isValidEmail(email: String): Boolean {
    val regex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    return regex.matches(email)
}
```
- Error muncul real-time saat ketikan tidak valid ("Format email tidak valid").
- Di-submit ulang dicek lagi (`submitSessionForm()`).

### c. Field wajib lain
- **Game** & **Venue** wajib dipilih → pesan error di bawah pilihan.
- **Catatan** maksimal 120 karakter (counter tampil di UI).
- Unit test validasi email: `app/src/test/.../ValidasiFormTest.kt`
  (`./gradlew testDebugUnitTest`).

---

## 8. Arsitektur MVVM & Struktur Folder

```
android/app/src/main/java/id/boardgo/
├── MainActivity.kt                  // ComponentActivity + setContent
├── data/
│   ├── model/Models.kt              // Game, Venue, Player, GameSession
│   └── DummyData.kt                 // 12 game, 8 venue, 6 pemain, 12 sesi (dummy)
└── ui/
    ├── theme/                       // Color.kt, Type.kt, Shape.kt, Theme.kt (CUSTOM)
    ├── components/CommonComponents.kt // Chip, TopBar, Avatar, Button, dll
    ├── navigation/BoardGoNavHost.kt // NavHost + argumen + bottom bar
    ├── viewmodel/BoardGOViewModel.kt// SEMUA state + logika (MVVM)
    └── screens/
        ├── welcome/WelcomeScreen.kt       // Landing (start destination)
        ├── list/GameListScreen.kt         // List/LazyColumn + foto cover
        ├── detail/GameDetailScreen.kt     // Detail + argumen gameId
        ├── form/CreateSessionScreen.kt    // Form validasi angka & email
        ├── mygames/MyGamesScreen.kt       // Tab Upcoming/Hosting/Past
        ├── chat/ChatScreen.kt             // Chat sesi (bubble + input)
        ├── notifications/NotificationsScreen.kt // Notifikasi + mark all read
        └── profile/ProfileScreen.kt       // Profil & statistik
res/
├── font/     // Anton + JetBrains Mono (font asli design web)
└── drawable/ // 12 foto game game_*.jpg (asli dari repo web)
```

**Alur data (MVVM):**

1. `DummyData` = "repository" dummy (12 game: Catan, Wingspan, Azul, …).
2. `BoardGOViewModel` membaca DummyData, menyimpannya di `StateFlow`,
   menangani aksi (submit form, mock GPS, dll.) di `viewModelScope`.
3. Layar Compose hanya **membaca state** dan **memanggil fungsi event** —
   tanpa logika bisnis di dalam Composable.

---

## 9. Checklist Kriteria UTS

- [x] 1a. Build Compose aktif (`buildFeatures.compose = true` + BOM + plugin compose)
- [x] 1b. `MainActivity` = `ComponentActivity` + `setContent`
- [x] 2a. Layar List dengan `LazyColumn`
- [x] 2b. Layar Detail (tampil detail item, argumen gameId)
- [x] 2c. Layar Form/Input
- [x] 2d. Layar Fitur Native Mockup (GPS — layar **Nearby**: peta mock + jarak/radius
  tanpa permission GPS asli; lokasi mock juga ada di baris ◉ NEARBY pada Home)
- [x] 2e. Layar Bebas (Profil)
- [x] 3. Theme Material 3 custom (color, typography, shapes — bukan template default)
- [x] 4. State management: ViewModel + StateFlow + remember/mutableStateOf (loading, input)
- [x] 5. Navigasi NavHost + NavController + passing argument (gameId)
- [x] 6a. Field angka: `KeyboardType.Number` + validasi hanya angka + rentang
- [x] 6b. Field email: `KeyboardType.Email` + validasi regex
- [x] Semua data dummy (tanpa backend/Room/database/hardware sungguhan)

---

## 10. Fitur Mockup Native (GPS) — Penjelasan untuk Demo

| Fitur | Implementasi | Yang ditunjukkan ke dosen |
|-------|--------------|---------------------------|
| **GPS / Jarak** (layar Nearby, diintegrasi di tab FIND) | Peta mock digambar `Canvas` (jalan/sungai/taman); pin venue diposisikan dari **jarak + bearing** (proyeksi sama dengan web app); lingkaran radius + chip 1/2/3/5/10 km; zoom + recenter (mockup); daftar sesi terurut jarak + badge km | Mockup fitur native tanpa hardware, pemrosesan data jarak, state radius/pilihan venue |
| **Lokasi mock di Home** | Baris "◉ NEARBY" → simulasi `delay(1200)` → status LOCATING… lalu kembali (mockup locate) | Mockup aksi lokasi UI-only |

Catatan revisi: **fitur QR Scanner & Biometrik DIHAPUS** atas permintaan.
Semua mockup **tidak memakai permission** apa pun (tidak ada CAMERA/ACCESS_FINE_LOCATION
di manifest) — murni UI + logika simulasi dummy, sesuai ketentuan tugas.

---

## 11. Menjalankan Unit Test

```bash
cd android
./gradlew testDebugUnitTest
```

Hasil yang diharapkan: `ValidasiFormTest` lulus (email valid diterima, email invalid ditolak).
