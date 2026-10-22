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

enum class LoadStatus { LOADING, SUCCESS, ERROR }

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class HomeUiState(
    val status: LoadStatus = LoadStatus.LOADING,
    val sessions: List<GameSession> = emptyList(),
    val selectedChip: String = "All",
    val locating: Boolean = false,
    val locationArea: String = "BSD",
    val errorMessage: String? = null
) {
    val openSessions: List<GameSession>
        get() = sessions.filter {
            it.id !in DummyData.pastSessionIds &&
                (selectedChip == "All" || it.gameId == selectedChip)
        }

    val featured: GameSession?
        get() {
            val eligible = openSessions.filter {
                DummyData.CURRENT_USER_ID !in it.joinedPlayerIds &&
                    it.joinedPlayerIds.size < it.maxPlayers
            }
            return eligible.find { it.gameId in DummyData.featuredGameIds }
                ?: openSessions.find { it.gameId in DummyData.featuredGameIds }
                ?: eligible.firstOrNull()
                ?: openSessions.firstOrNull()
        }

    val rest: List<GameSession>
        get() = openSessions.filter { it.id != featured?.id }
}

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

data class NearbyUiState(
    val radiusKm: Double = 5.0,
    val pickedVenueId: String? = null,
    val zoom: Float = 1f,
    val hint: String? = null
) {
    val radiusPct: Float get() = ((radiusKm / MAP_KM) * 100.0).toFloat()
}

const val MAP_KM = 12.0

data class GameDetailUiState(
    val status: LoadStatus = LoadStatus.LOADING,
    val game: Game? = null,
    val sessions: List<GameSession> = emptyList(),
    val isFavorite: Boolean = false,
    val errorMessage: String? = null
)

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

data class AuthUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isSubmitting: Boolean = false,
    val successTick: Int = 0
)

data class ChatMessage(
    val id: String,
    val senderId: String,
    val text: String,
    val time: String,
    val system: Boolean = false
)

data class ChatUiState(
    val messagesBySession: Map<String, List<ChatMessage>> = emptyMap()
)

data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val time: String,
    val read: Boolean = false,
    val chatSessionId: String? = null,
    val gameId: String? = null
)

data class NotifUiState(
    val notifications: List<AppNotification> = emptyList()
) {
    val unreadCount: Int get() = notifications.count { !it.read }
}

enum class MyGamesTab { UPCOMING, HOSTING, PAST }

data class MyGamesUiState(
    val tab: MyGamesTab = MyGamesTab.UPCOMING,
    val upcoming: List<GameSession> = emptyList(),
    val hosting: List<GameSession> = emptyList(),
    val past: List<GameSession> = emptyList()
) {
    val current: List<GameSession>
        get() = when (tab) {
            MyGamesTab.UPCOMING -> upcoming
            MyGamesTab.HOSTING -> hosting
            MyGamesTab.PAST -> past
        }
}

data class ProfileUiState(
    val me: Player? = null,
    val favoriteGames: List<Game> = emptyList(),
    val joinedSessions: List<GameSession> = emptyList()
)

class BoardGOViewModel : ViewModel() {

    private val _allSessions = MutableStateFlow(DummyData.sessions)
    val allSessions: StateFlow<List<GameSession>> = _allSessions.asStateFlow()

    private val _chatState = MutableStateFlow(
        ChatUiState(
            messagesBySession = mapOf(

                "s4" to listOf(
                    ChatMessage("m1", "u3", "Hey! Prelude cards are packed 🔥", "10:02"),
                    ChatMessage("m2", DummyData.CURRENT_USER_ID, "Nice, I'll bring chips", "10:05"),
                    ChatMessage("m3", "u3", "See you Saturday at Meeple House", "10:06")
                )
            )
        )
    )
    val chatState: StateFlow<ChatUiState> = _chatState.asStateFlow()

    fun chatMessages(sessionId: String): List<ChatMessage> =
        _chatState.value.messagesBySession[sessionId].orEmpty()

    fun sendChat(sessionId: String, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        val time = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date())
        _chatState.update { state ->
            val current = state.messagesBySession[sessionId].orEmpty()
            val msg = ChatMessage(
                id = "m${System.currentTimeMillis()}",
                senderId = DummyData.CURRENT_USER_ID,
                text = trimmed,
                time = time
            )
            state.copy(messagesBySession = state.messagesBySession + (sessionId to (current + msg)))
        }
    }

    private val _notifState = MutableStateFlow(
        NotifUiState(
            notifications = listOf(

                AppNotification("n1", "Kevin replied in chat",
                    "See you Saturday at Meeple House", "10m",
                    chatSessionId = "s4"),
                AppNotification("n2", "New Catan table nearby",
                    "Coffee & Dice · Tonight 19:30 · 1 seat left", "1h",
                    gameId = "catan"),
                AppNotification("n3", "Rate your last game",
                    "How was Catan at The Board Room?", "2d", read = true)
            )
        )
    )
    val notifState: StateFlow<NotifUiState> = _notifState.asStateFlow()

    fun markNotifRead(notifId: String) {
        _notifState.update { state ->
            state.copy(
                notifications = state.notifications.map {
                    if (it.id == notifId) it.copy(read = true) else it
                }
            )
        }
    }

    fun markAllNotifsRead() {
        _notifState.update { state ->
            state.copy(notifications = state.notifications.map { it.copy(read = true) })
        }
    }

    private fun pushNotif(notif: AppNotification) {
        _notifState.update { it.copy(notifications = listOf(notif) + it.notifications) }
    }

    fun joinSession(sessionId: String) {
        val alreadyJoined = _allSessions.value
            .find { it.id == sessionId }
            ?.joinedPlayerIds
            ?.contains(DummyData.CURRENT_USER_ID) == true

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

        if (!alreadyJoined) {
            val joinedNow = _allSessions.value.find { it.id == sessionId }
                ?.joinedPlayerIds
                ?.contains(DummyData.CURRENT_USER_ID) == true
            if (joinedNow) {
                _chatState.update { state ->
                    val current = state.messagesBySession[sessionId].orEmpty()
                    val msg = ChatMessage(
                        id = "sys${System.currentTimeMillis()}",
                        senderId = "system",
                        text = "You joined the table 🎲",
                        time = "",
                        system = true
                    )
                    state.copy(
                        messagesBySession = state.messagesBySession + (sessionId to (current + msg))
                    )
                }
            }
        }

        _homeState.update { it.copy(sessions = _allSessions.value) }
        loadMyGames()
        loadProfile()
        loadGameDetailQuiet()

        if (!alreadyJoined) {
            val joinedNow = _allSessions.value.find { it.id == sessionId }
                ?.joinedPlayerIds
                ?.contains(DummyData.CURRENT_USER_ID) == true
            if (joinedNow) {
                pushNotif(
                    AppNotification(
                        id = "nj${System.currentTimeMillis()}",
                        title = "You're in!",
                        body = "Say hi to your table in chat.",
                        time = "now",
                        chatSessionId = sessionId
                    )
                )
            }
        }
    }

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

    fun mockLocate() {
        viewModelScope.launch {
            _homeState.update { it.copy(locating = true) }
            delay(1200)
            _homeState.update { it.copy(locating = false) }
        }
    }

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

    private val _nearbyState = MutableStateFlow(NearbyUiState())
    val nearbyState: StateFlow<NearbyUiState> = _nearbyState.asStateFlow()

    fun nearbySessions(radiusKm: Double = _nearbyState.value.radiusKm): List<GameSession> =
        _allSessions.value
            .filter { s ->
                s.id !in DummyData.pastSessionIds &&
                    (DummyData.venueById(s.venueId)?.distanceKm ?: Double.MAX_VALUE) <= radiusKm
            }
            .sortedBy { DummyData.venueById(it.venueId)?.distanceKm ?: 0.0 }

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

            val form = _formState.value
            val newSession = id.boardgo.app.data.model.GameSession(
                id = newId,
                gameId = form.gameId,
                venueId = form.venueId,
                hostId = DummyData.CURRENT_USER_ID,
                day = form.day,
                time = form.time,
                maxPlayers = form.maxPlayers.toIntOrNull() ?: 4,
                joinedPlayerIds = listOf(DummyData.CURRENT_USER_ID),
                level = id.boardgo.app.data.model.Level.CASUAL,
                note = form.note.ifBlank { "Come play!" },
                fee = form.fee
            )
            _allSessions.update { it + newSession }
            _homeState.update { h -> h.copy(sessions = _allSessions.value) }
            loadMyGames()
            loadProfile()

            pushNotif(
                AppNotification(
                    id = "np${System.currentTimeMillis()}",
                    title = "Session published",
                    body = "Players nearby can now join your table.",
                    time = "now",
                    gameId = form.gameId
                )
            )

            _formState.update {
                it.copy(isSubmitting = false, isSubmitSuccess = true, createdSessionId = newId)
            }
        }
    }

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
            delay(700)
            _authState.update {
                it.copy(isSubmitting = false, successTick = it.successTick + 1)
            }
        }
    }

    private val _myGamesState = MutableStateFlow(MyGamesUiState())
    val myGamesState: StateFlow<MyGamesUiState> = _myGamesState.asStateFlow()

    fun loadMyGames() {
        val all = _allSessions.value

        val upcoming = all.filter {
            DummyData.CURRENT_USER_ID in it.joinedPlayerIds &&
                it.hostId != DummyData.CURRENT_USER_ID &&
                it.id !in DummyData.pastSessionIds
        }

        val hosting = all.filter {
            it.hostId == DummyData.CURRENT_USER_ID && it.id !in DummyData.pastSessionIds
        }

        val past = all.filter {
            DummyData.CURRENT_USER_ID in it.joinedPlayerIds &&
                it.id in DummyData.pastSessionIds
        }
        _myGamesState.update { it.copy(upcoming = upcoming, hosting = hosting, past = past) }
    }

    fun onMyGamesTabChange(tab: MyGamesTab) {
        _myGamesState.update { it.copy(tab = tab) }
    }

    private val _profileState = MutableStateFlow(ProfileUiState())
    val profileState: StateFlow<ProfileUiState> = _profileState.asStateFlow()

    fun loadProfile() {
        val me = DummyData.playerById(DummyData.CURRENT_USER_ID)
        _profileState.update {
            it.copy(
                me = me,

                favoriteGames = DummyData.games.filter { g -> g.id in DummyData.featuredGameIds },
                joinedSessions = _allSessions.value.filter { s ->
                    DummyData.CURRENT_USER_ID in s.joinedPlayerIds &&
                        s.id !in DummyData.pastSessionIds
                }
            )
        }
    }

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun onThemeModeChange(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun nearbyVenues(radiusKm: Int): List<Venue> =
        DummyData.venues.filter { it.distanceKm <= radiusKm }.sortedBy { it.distanceKm }

    init {
        loadGames()
        loadHome()
        loadMyGames()
        loadProfile()
    }

    companion object {

        fun isValidEmail(email: String): Boolean {
            val regex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
            return regex.matches(email)
        }
    }
}

