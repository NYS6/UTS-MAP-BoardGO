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
 * BoardGOViewModel adalah state holder tunggal untuk seluruh layar.
 * View (Composable) hanya membaca StateFlow dan memanggil fungsi event.
 * Semua data dari DummyData (tanpa backend/Room/database).
 * ===========================================================================
 */

enum class LoadStatus { LOADING, SUCCESS, ERROR }

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

/** State layar Fitur Native (mockup GPS / QR / biometrik). */
data class NativeFeatureUiState(
    val isGpsLoading: Boolean = false,
    val gpsErrorMessage: String? = null,
    val currentLocation: String? = null,
    val nearbyRadiusKm: Int = 5,
    val isQrScanning: Boolean = false,
    val qrScanResult: String? = null,
    val isBioChecking: Boolean = false,
    val biometricResult: String? = null       // "success" / "failed"
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

    // =================== State Layar List ===================================
    private val _listState = MutableStateFlow(GameListUiState())
    val listState: StateFlow<GameListUiState> = _listState.asStateFlow()

    init {
        loadGames()
    }

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

    // =================== State Layar Detail =================================
    private val _detailState = MutableStateFlow(GameDetailUiState())
    val detailState: StateFlow<GameDetailUiState> = _detailState.asStateFlow()

    fun loadGameDetail(gameId: String) {
        viewModelScope.launch {
            _detailState.update {
                it.copy(status = LoadStatus.LOADING, game = null, errorMessage = null)
            }
            delay(600)
            val game = DummyData.gameById(gameId)
            if (game != null) {
                _detailState.update {
                    it.copy(
                        status = LoadStatus.SUCCESS,
                        game = game,
                        sessions = DummyData.sessionsForGame(gameId)
                    )
                }
            } else {
                _detailState.update {
                    it.copy(status = LoadStatus.ERROR, errorMessage = "Game not found")
                }
            }
        }
    }

    fun toggleFavorite() {
        _detailState.update { it.copy(isFavorite = !it.isFavorite) }
    }

    // =================== State Layar Form ===================================
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

    /** Field ANGKA: hanya digit yang diterima (maks 2 karakter). */
    fun onFormMaxPlayersChange(raw: String) {
        val filtered = raw.filter { it.isDigit() }.take(2)
        _formState.update { it.copy(maxPlayers = filtered, maxPlayersError = null) }
    }

    fun onFormFeeChange(fee: String) = _formState.update { it.copy(fee = fee) }

    fun onFormNoteChange(note: String) {
        _formState.update { it.copy(note = note.take(120), noteError = null) }
    }

    /** Field EMAIL: validasi regex real-time. */
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

    /** Submit + validasi lengkap (game/venue wajib, players 2–12, email format). */
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
                it.copy(
                    isSubmitting = false,
                    isSubmitSuccess = true,
                    createdSessionId = newId
                )
            }
        }
    }

    // =================== State Layar Fitur Native ===========================
    private val _nativeState = MutableStateFlow(NativeFeatureUiState())
    val nativeState: StateFlow<NativeFeatureUiState> = _nativeState.asStateFlow()

    /** MOCKUP GPS — simulasi tanpa hardware. */
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

    fun onNearbyRadiusChange(radiusKm: Int) {
        _nativeState.update { it.copy(nearbyRadiusKm = radiusKm) }
    }

    /** MOCKUP QR Scanner. */
    fun mockScanQr() {
        viewModelScope.launch {
            _nativeState.update { it.copy(isQrScanning = true, qrScanResult = null) }
            delay(1500)
            val codes = listOf("BG-CATAN-001", "BG-AZUL-042", "BG-WEREWOLF-013")
            _nativeState.update {
                it.copy(isQrScanning = false, qrScanResult = codes.random())
            }
        }
    }

    fun clearQrResult() = _nativeState.update { it.copy(qrScanResult = null) }

    /** MOCKUP Biometrik. */
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

    // =================== State Layar My Games ===============================
    private val _myGamesState = MutableStateFlow(MyGamesUiState())
    val myGamesState: StateFlow<MyGamesUiState> = _myGamesState.asStateFlow()

    fun loadMyGames() {
        val joined = DummyData.sessions.filter {
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

    // =================== State Layar Profil =================================
    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    init {
        loadProfile()
        loadMyGames()
    }

    fun loadProfile() {
        val me = DummyData.playerById(DummyData.CURRENT_USER_ID)
        _profileState.update {
            it.copy(
                me = me,
                favoriteGames = DummyData.games.filter { g -> g.id in listOf("catan", "mars") },
                joinedSessions = DummyData.sessions.filter { s ->
                    DummyData.CURRENT_USER_ID in s.joinedPlayerIds && s.id !in DummyData.pastSessionIds
                }
            )
        }
    }

    /** Venue terdekat berdasarkan radius (mockup GPS). */
    fun nearbyVenues(radiusKm: Int): List<Venue> =
        DummyData.venues.filter { it.distanceKm <= radiusKm }.sortedBy { it.distanceKm }

    companion object {
        /** Validasi format email (regex sederhana). */
        fun isValidEmail(email: String): Boolean {
            val regex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
            return regex.matches(email)
        }
    }
}
