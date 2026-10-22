# BoardGO — UTS Pemrograman Mobile (Jetpack Compose + MVVM)

BoardGO adalah aplikasi katalog & komunitas board game. Repo ini berisi **dua versi**:

| Versi | Lokasi | Stack |
|-------|--------|-------|
| Web app (asli) | `/src`, `/public`, `package.json` | React + TanStack Router + Tailwind |
| **Android app (UTS)** | **`/android`** | **Kotlin + Jetpack Compose + MVVM** |

Project Android berada di folder **`android/`** agar kedua versi tidak saling menimpa.

## 🎨 Design

UI Android mengikuti design web app asli secara detail:
- **Font asli**: Anton (judul display UPPERCASE) + JetBrains Mono (label kecil) — di-bundle di `res/font/`
- **Foto game asli** dari repo web (`res/drawable/game_*.jpg`) untuk cover/hero/thumbnail
- Palet warna oranye `#F84F2F` + sky blue + latar `#E5ECF3` (konversi OKLCH dari `styles.css`)
- Bottom nav pill melayang (**HOME · FIND · (+) · MINE · ME**) — meniru web app;
  **GPS/jarak terintegrasi di tab FIND** (tombol peta di Discover → layar Nearby),
  tombol + = Host game
- Kartu surface + ring tipis, pill status kursi ("FILLING FAST · 1 SEAT"),
  section label mono + garis tipis
- **Game utama: MAHJONG & MONOPOLY** (urutan depan, kartu featured, chip, favorit)
- **10 layar**: Welcome, Register/Login, Home, Discover/List, Nearby (jarak GPS),
  Detail, Form, My Games, **Chat sesi**, Profil — fitur QR & biometrik DIHAPUS
- **Dark mode** + toggle manual (System/Light/Dark) di Profil → APPEARANCE
- **Fitur jarak GPS**: peta mock dengan pin venue (jarak+bearing), radius 1–10 km,
  zoom/recenter, daftar sesi terurut jarak (mock data, tanpa permission GPS)

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
