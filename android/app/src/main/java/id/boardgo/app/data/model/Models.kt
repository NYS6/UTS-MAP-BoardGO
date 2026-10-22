package id.boardgo.app.data.model

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

enum class Level(val label: String) {
    BEGINNER("Beginner"),
    CASUAL("Casual"),
    COMPETITIVE("Competitive")
}

data class Game(
    val id: String,
    val name: String,
    val description: String,
    val players: String,
    val duration: String,
    val difficulty: Difficulty,
    val category: Category
)

data class Venue(
    val id: String,
    val name: String,
    val area: String,
    val distanceKm: Double,
    val bearing: Double
)

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

