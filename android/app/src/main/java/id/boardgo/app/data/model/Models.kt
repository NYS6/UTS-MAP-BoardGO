package id.boardgo.app.data.model

/**
 * Model data BoardGO — di-porting 1:1 dari model di web app
 * (src/lib/data.ts). Semua data di aplikasi ini adalah DUMMY,
 * tidak ada backend/Room/database sungguhan.
 */

/** Kategori board game (dipakai sebagai filter di layar List). */
enum class Category(val label: String) {
    STRATEGY("Strategy"),
    FAMILY("Family"),
    CLASSIC("Classic"),
    TILE_CARD("Tile & Card"),
    PARTY("Party"),
    WORD("Word")
}

enum class Difficulty(val label: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard")
}

/** Level pemain saat sesi bermain. */
enum class Level(val label: String) {
    BEGINNER("Beginner"),
    CASUAL("Casual"),
    COMPETITIVE("Competitive")
}

/** Item utama katalog. */
data class Game(
    val id: String,
    val name: String,
    val description: String,
    val players: String,
    val duration: String,
    val difficulty: Difficulty,
    val category: Category
)

/** Venue/café tempat sesi bermain; distance (km) dipakai mockup GPS. */
data class Venue(
    val id: String,
    val name: String,
    val area: String,
    val distanceKm: Double,
    val bearing: Double
)

/** Profil pemain. */
data class Player(
    val id: String,
    val name: String,
    val colorIndex: Int,
    val area: String,
    val bio: String,
    val rating: Double,
    val gamesPlayed: Int,
    val level: Level
)

/** Sesi bermain (tabel) — dibuat via layar Form. */
data class GameSession(
    val id: String,
    val gameId: String,
    val venueId: String,
    val hostId: String,
    val day: String,
    val time: String,
    val maxPlayers: Int,
    val joinedPlayerIds: List<String>,
    val level: Level,
    val note: String,
    val fee: String
)
