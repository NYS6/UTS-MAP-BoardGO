package id.boardgo.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.boardgo.app.data.DummyData
import id.boardgo.app.data.model.Game
import id.boardgo.app.data.model.GameSession
import id.boardgo.app.data.model.Player
import id.boardgo.app.data.model.Venue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ============================ MVVM ==========================================
 * BoardGOViewModel = state holder tunggal seluruh layar (MVVM).
 * Semua data dari DummyData (tanpa backend/Room/database/hardware sungguhan).
 * ===========================================================================
 */

enum class LoadStatus { LOADING, SUCCESS, ERROR }

/** Mode tampilan (dark mode manual). */
enum class ThemeMode { SYSTEM, LIGHT, DARK }

// ---------------------------------------------------------------------------
// State per layar
// ---------------------------------------------------------------------------

/** State layar Home (dashboard sesi — meniru web app). */
data class HomeUiState(
    val status: LoadStatus = LoadStatus.LOADING,
    val sessions: List<GameSession> = emptyList(),
    val selectedChip: String = "All",     // filter game: "All" | gameId
    val locating: Boolean = false,
    val locationArea: String = "BSD",
    val errorMessage: String? = null
) {
    val openSessions: List<GameSession>
        get() = sessions.filter {
            it.id !in DummyData.pastSessionIds &&
                (selectedChip == "All" || it.gameId == selectedChip)
        }

    /** Kartu featured: sesi yang belum penuh & belum diikuti user. */
    val featured: GameSession?
        get() = openSessions.find {
            DummyData.CURRENT_USER_ID !in it.joinedPlayerIds &&
                it.joinedPlayerIds.size < it.maxPlayers
        } ?: openSessions.firstOrNull()

    val rest: List<GameSession>
        get() = openSessions.filter { it.id != featured?.id }
}

/** State layar List / Discover. */
data class GameListUiState(
    val status: LoadStatus = LoadStatus.LOADING,
    val allGames: List<Game> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val errorMessage: String? = null
) {
    val filteredGames: List<Game>
        get() = allGames.filter { game ->
            val matchesQuery = game.name.contains(searchQuery, ignoreCase = true) ||
                game.description.contains(searchQuery, ignoreCase = true)
            val matchesCategory =
                selectedCategory == "All" || game.category.label == selectedCategory
            matchesQuery && matchesCategory
        }
}

/** State layar Nearby (FITUR JARAK GPS — mock lokasi + radius). */
data class NearbyUiState(
    val radiusKm: Double = 5.0,
    val pickedVenueId: String? = null,
    val zoom: Float = 1f,
    val hint: String? = null
) {
    val radiusPct: Float get() = ((radiusKm / MAP_KM) * 100.0).toFloat()
}

/** Lebar peta (km) — sama dengan web app (MAP_KM). */
const val MAP_KM = 12.0

/** State layar Detail. */
data class GameDetailUiState(
    val status: LoadStatus = LoadStatus.LOADING,
    val game: Game? = null,
    val sessions: List<GameSession> = emptyList(),
    val isFavorite: Boolean = false,
    val errorMessage: String? = null
)

/** State layar Form buat sesi. */
data class CreateSessionUiState(
    val gameId: String = "",
    val day: String = "Tonight",
    val time: String = "19:30",
    val venueId: String = "",
    val maxPlayers: String = "4",
    val fee: String = "Free",
    val note: String = "",
    val contactEmail: String = "",
    val gameIdError: String? = null,
    val venueIdError: String? = null,
    val maxPlayersError: String? = null,
    val noteError: String? = null,
    val contactEmailError: String? = null,
    val isSubmitting: Boolean = false,
    val isSubmitSuccess: Boolean = false,
    val createdSessionId: String? = null
)

/** State layar Register / Login (AuthForm). */
data class AuthUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isSubmitting: Boolean = false,
    val successTick: Int = 0   // bertambah tiap submit sukses -> layar navigasi
)

/** State layar Fitur Native (mockup GPS / QR / biometrik). */
data class NativeFeatureUiState(
    val isGpsLoading: Boolean = false,
    val gpsErrorMessage: String? = null,
    val currentLocation: String? = null,
    val nearbyRadiusKm: Int = 5,
    val isQrScanning: Boolean = false,
    val qrScanResult: String? = null,
    val isBioChecking: Boolean = false,
    val biometricResult: String? = null
)

/** State layar My Games (tab Upcoming / Past). */
data class MyGamesUiState(
    val showPast: Boolean = false,
    val upcoming: List<GameSession> = emptyList(),
    val past: List<GameSession> = emptyList()
) {
    val current: List<GameSession> get() = if (showPast) past else upcoming
}

/** State layar Profil. */
data class ProfileUiState(
    val me: Player? = null,
    val favoriteGames: List<Game> = emptyList(),
    val joinedSessions: List<GameSession> = emptyList()
)

// ---------------------------------------------------------------------------
class BoardGOViewModel : ViewModel() {

    // =================== SEMUA SESI (sumber kebenaran) =====================
    private val _allSessions = MutableStateFlow(DummyData.sessions)
    val allSessions: StateFlow<List<GameSession>> = _allSessions.asStateFlow()

    /** Join sesi (mockup) — memperbarui Home, My Games, Profil, Nearby. */
    fun joinSession(sessionId: String) {
        _allSessions.update { list ->
            list.map { s ->
                if (s.id == sessionId &&
                    DummyData.CURRENT_USER_ID !in s.joinedPlayerIds &&
                    s.joinedPlayerIds.size < s.maxPlayers
                ) {
                    s.copy(joinedPlayerIds = s.joinedPlayerIds + DummyData.CURRENT_USER_ID)
                } else s
            }
        }
        // Perbarui turunan
        _homeState.update { it.copy(sessions = _allSessions.value) }
        loadMyGames()
        loadProfile()
        loadGameDetailQuiet()
    }

    // =================== State Home ========================================
    private val _homeState = MutableStateFlow(HomeUiState(sessions = DummyData.sessions))
    val homeState: StateFlow<HomeUiState> = _homeState.asStateFlow()

    fun loadHome() {
        viewModelScope.launch {
            _homeState.update { it.copy(status = LoadStatus.LOADING, errorMessage = null) }
            delay(600)
            _homeState.update {
                it.copy(status = LoadStatus.SUCCESS, sessions = _allSessions.value)
            }
        }
    }

    fun onHomeChipChange(chip: String) {
        _homeState.update { it.copy(selectedChip = chip) }
    }

    /** MOCKUP tombol lokasi di Home ("NEARBY / LOCATING…"). */
    fun mockLocate() {
        viewModelScope.launch {
            _homeState.update { it.copy(locating = true) }
            delay(1200)
            _homeState.update { it.copy(locating = false) }
        }
    }

    // =================== State List / Discover =============================
    private val _listState = MutableStateFlow(GameListUiState())
    val listState: StateFlow<GameListUiState> = _listState.asStateFlow()

    fun loadGames() {
        viewModelScope.launch {
            _listState.update { it.copy(status = LoadStatus.LOADING, errorMessage = null) }
            delay(800)
            _listState.update {
                it.copy(status = LoadStatus.SUCCESS, allGames = DummyData.games)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _listState.update { it.copy(searchQuery = query) }
    }

    fun onCategorySelected(category: String) {
        _listState.update { it.copy(selectedCategory = category) }
    }

    // =================== State Nearby (jarak GPS) ===========================
    private val _nearbyState = MutableStateFlow(NearbyUiState())
    val nearbyState: StateFlow<NearbyUiState> = _nearbyState.asStateFlow()

    /** Sesi LIVE (belum lewat) yang berada dalam radius jarak tertentu. */
    fun nearbySessions(radiusKm: Double = _nearbyState.value.radiusKm): List<GameSession> =
        _allSessions.value
            .filter { s ->
                s.id !in DummyData.pastSessionIds &&
                    (DummyData.venueById(s.venueId)?.distanceKm ?: Double.MAX_VALUE) <= radiusKm
            }
            .sortedBy { DummyData.venueById(it.venueId)?.distanceKm ?: 0.0 }

    /** Sesi live pada sebuah venue. */
    fun venueSessions(venueId: String): List<GameSession> =
        _allSessions.value.filter {
            it.venueId == venueId && it.id !in DummyData.pastSessionIds
        }

    fun onNearbyRadiusChange(radiusKm: Double) {
        _nearbyState.update { it.copy(radiusKm = radiusKm, pickedVenueId = null, hint = null) }
    }

    fun onVenuePicked(venueId: String) {
        _nearbyState.update {
            it.copy(pickedVenueId = if (it.pickedVenueId == venueId) null else venueId, hint = null)
        }
    }

    fun onNearbyZoom(delta: Float) {
        _nearbyState.update { it.copy(zoom = (it.zoom + delta).coerceIn(1f, 2.5f)) }
    }

    fun onRecenter() {
        _nearbyState.update {
            it.copy(zoom = 1f, pickedVenueId = null, hint = "Centered on your location")
        }
    }

    fun onNearbyHintShown() {
        _nearbyState.update { it.copy(hint = null) }
    }

    // =================== State Detail =======================================
    private val _detailState = MutableStateFlow(GameDetailUiState())
    val detailState: StateFlow<GameDetailUiState> = _detailState.asStateFlow()

    fun loadGameDetail(gameId: String) {
        viewModelScope.launch {
            _detailState.update {
                it.copy(status = LoadStatus.LOADING, game = null, errorMessage = null)
            }
            delay(600)
            applyGameDetail(gameId)
        }
    }

    /** Refresh tanpa loading (dipanggil setelah joinSession). */
    private fun loadGameDetailQuiet() {
        val id = _detailState.value.game?.id ?: return
        applyGameDetail(id)
    }

    private fun applyGameDetail(gameId: String) {
        val game = DummyData.gameById(gameId)
        if (game != null) {
            _detailState.update {
                it.copy(
                    status = LoadStatus.SUCCESS,
                    game = game,
                    sessions = _allSessions.value.filter { s ->
                        s.gameId == gameId && s.id !in DummyData.pastSessionIds
                    }
                )
            }
        } else {
            _detailState.update {
                it.copy(status = LoadStatus.ERROR, errorMessage = "Game not found")
            }
        }
    }

    fun toggleFavorite() {
        _detailState.update { it.copy(isFavorite = !it.isFavorite) }
    }

    // =================== State Form =========================================
    private val _formState = MutableStateFlow(CreateSessionUiState())
    val formState: StateFlow<CreateSessionUiState> = _formState.asStateFlow()

    fun prefillGame(gameId: String) {
        _formState.update { it.copy(gameId = gameId, gameIdError = null) }
    }

    fun onFormGameChange(gameId: String) =
        _formState.update { it.copy(gameId = gameId, gameIdError = null) }

    fun onFormDayChange(day: String) = _formState.update { it.copy(day = day) }
    fun onFormTimeChange(time: String) = _formState.update { it.copy(time = time) }

    fun onFormVenueChange(venueId: String) =
        _formState.update { it.copy(venueId = venueId, venueIdError = null) }

    /** Field ANGKA: hanya digit (maks 2). */
    fun onFormMaxPlayersChange(raw: String) {
        val filtered = raw.filter { it.isDigit() }.take(2)
        _formState.update { it.copy(maxPlayers = filtered, maxPlayersError = null) }
    }

    fun onFormFeeChange(fee: String) = _formState.update { it.copy(fee = fee) }

    fun onFormNoteChange(note: String) {
        _formState.update { it.copy(note = note.take(120), noteError = null) }
    }

    fun onFormEmailChange(raw: String) {
        val trimmed = raw.trim()
        val error = when {
            trimmed.isEmpty() -> null
            !isValidEmail(trimmed) -> "Invalid email format"
            else -> null
        }
        _formState.update { it.copy(contactEmail = raw, contactEmailError = error) }
    }

    fun resetForm() {
        _formState.update { CreateSessionUiState() }
    }

    fun submitSessionForm() {
        val current = _formState.value
        var hasError = false

        val gameError = if (current.gameId.isBlank()) {
            hasError = true; "Pick a game first"
        } else null

        val venueError = if (current.venueId.isBlank()) {
            hasError = true; "Pick a venue first"
        } else null

        val maxPlayersNum = current.maxPlayers.toIntOrNull()
        val maxError = when {
            current.maxPlayers.isBlank() -> {
                hasError = true; "Number of players is required"
            }
            maxPlayersNum == null || maxPlayersNum < 2 || maxPlayersNum > 12 -> {
                hasError = true; "Players must be 2–12"
            }
            else -> null
        }

        val emailError = if (current.contactEmail.isNotBlank() &&
            !isValidEmail(current.contactEmail.trim())
        ) {
            hasError = true; "Invalid email format"
        } else null

        if (hasError) {
            _formState.update {
                it.copy(
                    gameIdError = gameError,
                    venueIdError = venueError,
                    maxPlayersError = maxError,
                    contactEmailError = emailError,
                    isSubmitSuccess = false,
                    createdSessionId = null
                )
            }
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isSubmitting = true) }
            delay(700)
            val newId = "c${System.currentTimeMillis()}"
            _formState.update {
                it.copy(isSubmitting = false, isSubmitSuccess = true, createdSessionId = newId)
            }
        }
    }

    // =================== State Auth (Register / Login) ======================
    private val _authState = MutableStateFlow(AuthUiState())
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    fun onAuthNameChange(v: String) =
        _authState.update { it.copy(name = v, nameError = null) }

    fun onAuthEmailChange(v: String) =
        _authState.update {
            it.copy(
                email = v,
                emailError = if (v.isNotBlank() && !isValidEmail(v.trim()))
                    "Invalid email format" else null
            )
        }

    fun onAuthPasswordChange(v: String) =
        _authState.update {
            it.copy(
                password = v,
                passwordError = if (v.isNotEmpty() && v.length < 6)
                    "Password must be at least 6 characters" else null
            )
        }

    fun resetAuth() {
        _authState.update { AuthUiState() }
    }

    /**
     * Submit register/login — validasi mengikuti web app:
     * nama wajib, email regex, password min 6 karakter.
     */
    fun submitAuth(isRegister: Boolean) {
        val cur = _authState.value
        var hasError = false

        val nameError = if (isRegister && cur.name.isBlank()) {
            hasError = true; "Tell us your name"
        } else null

        val emailError = if (!isValidEmail(cur.email.trim())) {
            hasError = true; "Enter a valid email"
        } else null

        val pwError = if (cur.password.length < 6) {
            hasError = true; "Password must be at least 6 characters"
        } else null

        if (hasError) {
            _authState.update {
                it.copy(nameError = nameError, emailError = emailError, passwordError = pwError)
            }
            return
        }

        viewModelScope.launch {
            _authState.update { it.copy(isSubmitting = true) }
            delay(700) // simulasi network
            _authState.update {
                it.copy(isSubmitting = false, successTick = it.successTick + 1)
            }
        }
    }

    // =================== State Fitur Native =================================
    private val _nativeState = MutableStateFlow(NativeFeatureUiState())
    val nativeState: StateFlow<NativeFeatureUiState> = _nativeState.asStateFlow()

    fun mockFetchLocation() {
        viewModelScope.launch {
            _nativeState.update {
                it.copy(isGpsLoading = true, gpsErrorMessage = null, currentLocation = null)
            }
            delay(1200)
            val success = (0..4).random() != 0
            if (success) {
                _nativeState.update {
                    it.copy(isGpsLoading = false, currentLocation = "BSD City · -6.3019, 106.6535")
                }
            } else {
                _nativeState.update {
                    it.copy(isGpsLoading = false, gpsErrorMessage = "Location unavailable. Try again.")
                }
            }
        }
    }

    fun onNativeRadiusChange(radiusKm: Int) {
        _nativeState.update { it.copy(nearbyRadiusKm = radiusKm) }
    }

    fun mockScanQr() {
        viewModelScope.launch {
            _nativeState.update { it.copy(isQrScanning = true, qrScanResult = null) }
            delay(1500)
            val codes = listOf("BG-CATAN-001", "BG-AZUL-042", "BG-WEREWOLF-013")
            _nativeState.update { it.copy(isQrScanning = false, qrScanResult = codes.random()) }
        }
    }

    fun clearQrResult() = _nativeState.update { it.copy(qrScanResult = null) }

    fun mockAuthenticateBiometric() {
        viewModelScope.launch {
            _nativeState.update { it.copy(isBioChecking = true, biometricResult = null) }
            delay(1400)
            val success = (0..4).random() != 0
            _nativeState.update {
                it.copy(
                    isBioChecking = false,
                    biometricResult = if (success) "success" else "failed"
                )
            }
        }
    }

    fun clearBiometricResult() = _nativeState.update { it.copy(biometricResult = null) }

    // =================== State My Games =====================================
    private val _myGamesState = MutableStateFlow(MyGamesUiState())
    val myGamesState: StateFlow<MyGamesUiState> = _myGamesState.asStateFlow()

    fun loadMyGames() {
        val joined = _allSessions.value.filter {
            DummyData.CURRENT_USER_ID in it.joinedPlayerIds
        }
        _myGamesState.update {
            it.copy(
                upcoming = joined.filter { s -> s.id !in DummyData.pastSessionIds },
                past = joined.filter { s -> s.id in DummyData.pastSessionIds }
            )
        }
    }

    fun onMyGamesTabChange(showPast: Boolean) {
        _myGamesState.update { it.copy(showPast = showPast) }
    }

    // =================== State Profil =======================================
    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    fun loadProfile() {
        val me = DummyData.playerById(DummyData.CURRENT_USER_ID)
        _profileState.update {
            it.copy(
                me = me,
                favoriteGames = DummyData.games.filter { g -> g.id in listOf("catan", "mars") },
                joinedSessions = _allSessions.value.filter { s ->
                    DummyData.CURRENT_USER_ID in s.joinedPlayerIds &&
                        s.id !in DummyData.pastSessionIds
                }
            )
        }
    }

    // =================== Theme (dark mode manual) ===========================
    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun onThemeModeChange(mode: ThemeMode) {
        _themeMode.value = mode
    }

    // =================== Helper =============================================
    /** Venue terdekat berdasarkan radius (mockup jarak GPS). */
    fun nearbyVenues(radiusKm: Int): List<Venue> =
        DummyData.venues.filter { it.distanceKm <= radiusKm }.sortedBy { it.distanceKm }

    // =================== Init (DI AKHIR CLASS agar semua property siap) ====
    init {
        loadGames()
        loadHome()
        loadMyGames()
        loadProfile()
    }

    companion object {
        /** Validasi format email (regex sederhana). */
        fun isValidEmail(email: String): Boolean {
            val regex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
            return regex.matches(email)
        }
    }
}
