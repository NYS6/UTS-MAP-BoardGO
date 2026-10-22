# BoardGO — UTS Pemrograman Mobile (Jetpack Compose + MVVM)

BoardGO adalah aplikasi katalog & komunitas board game. Repo ini berisi **dua versi**:

| Versi | Lokasi | Stack |
|-------|--------|-------|
| Web app (asli) | `/src`, `/public`, `package.json` | React + TanStack Router + Tailwind |
| **Android app (UTS)** | **`/android`** | **Kotlin + Jetpack Compose + MVVM** |

Project Android berada di folder **`android/`** agar kedua versi tidak saling menimpa.

## 🚀 Menjalankan versi Android

```bash
cd android
./gradlew assembleDebug
```

Atau buka folder `android/` di Android Studio lalu klik **Run ▶**.
Panduan lengkap (setup, struktur, penjelasan tiap kriteria UTS) ada di:
**[`android/ANDROID_GUIDE.md`](android/ANDROID_GUIDE.md)**

## ✅ Kriteria UTS yang Terpenuhi

| Kriteria | Implementasi |
|----------|--------------|
| 1. Setup Compose | `buildFeatures.compose = true`, Compose BOM, `ComponentActivity` + `setContent` |
| 2. 5 Layar Compose | List (`LazyColumn`), Detail (argumen `gameId`), Form/Input, Fitur Native Mockup (GPS/QR/Biometrik), Profil |
| 3. Theming Custom | `ui/theme/` — `Color.kt`, `Type.kt`, `Shape.kt`, `Theme.kt` (Material 3 buatan sendiri) |
| 4. State Management | `BoardGOViewModel` + `StateFlow` + `collectAsStateWithLifecycle` |
| 5. Navigasi & Argumen | `NavHost` + `NavController`, route `detail/{gameId}` & `create?gameId={gameId}` |
| 6. Form & Validasi | Field angka (`KeyboardType.Number` + filter digit + rentang 2–12) & email (`KeyboardType.Email` + regex) |

Semua data **dummy** (`data/DummyData.kt`) — tanpa backend, database, maupun akses hardware sungguhan.
