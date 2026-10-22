package id.boardgo.app.data

import id.boardgo.app.data.model.Category
import id.boardgo.app.data.model.Difficulty
import id.boardgo.app.data.model.Game
import id.boardgo.app.data.model.GameSession
import id.boardgo.app.data.model.Level
import id.boardgo.app.data.model.Player
import id.boardgo.app.data.model.Venue

/**
 * Seluruh data DUMMY (tanpa backend/database). Data di-porting dari
 * src/lib/data.ts pada web app BoardGO.
 */

object DummyData {

    // ---------------------------------------------------------------------
    // Katalog game — MAHJONG & MONOPOLY di urutan depan sebagai GAME UTAMA
    // (paling populer), sesuai permintaan revisi.
    // ---------------------------------------------------------------------
    val featuredGameIds = listOf("mahjong", "monopoly")

    val games: List<Game> = listOf(
        Game("mahjong", "Mahjong", "The classic four-player tile game of skill, luck and sharp memory.", "4", "90 min", Difficulty.MEDIUM, Category.TILE_CARD),
        Game("monopoly", "Monopoly", "Buy, trade and build your way to a property empire. Friendships may vary.", "2–6", "120 min", Difficulty.EASY, Category.FAMILY),
        Game("catan", "Catan", "Trade, build and settle the island of Catan. Gather resources and outmaneuver rivals.", "3–4", "75 min", Difficulty.MEDIUM, Category.STRATEGY),
        Game("wingspan", "Wingspan", "Attract birds to your wildlife preserves in this engine-building card game.", "1–5", "60 min", Difficulty.MEDIUM, Category.STRATEGY),
        Game("mars", "Terraforming Mars", "Lead a corporation to make Mars habitable. Heavy, rewarding strategy.", "1–5", "120 min", Difficulty.HARD, Category.STRATEGY),
        Game("azul", "Azul", "Draft gorgeous tiles to decorate the walls of the Royal Palace of Evora.", "2–4", "40 min", Difficulty.EASY, Category.TILE_CARD),
        Game("splendor", "Splendor", "Collect gems and build a Renaissance merchant empire.", "2–4", "30 min", Difficulty.EASY, Category.TILE_CARD),
        Game("ticket", "Ticket to Ride", "Claim railway routes across the map and connect distant cities.", "2–5", "60 min", Difficulty.EASY, Category.FAMILY),
        Game("chess", "Chess", "The timeless duel of strategy. Casual or rated, every level welcome.", "2", "30 min", Difficulty.HARD, Category.CLASSIC),
        Game("checkers", "Checkers", "Jump, capture and crown your pieces in this quick classic.", "2", "20 min", Difficulty.EASY, Category.CLASSIC),
        Game("scrabble", "Scrabble", "Build words on the grid and hunt for triple-word scores.", "2–4", "60 min", Difficulty.MEDIUM, Category.WORD),
        Game("werewolf", "Werewolf", "A big-group party game of bluffing, deduction and dramatic accusations.", "6–15", "45 min", Difficulty.EASY, Category.PARTY)
    )

    // ---------------------------------------------------------------------
    // Venue (mockup GPS: distanceKm dipakai untuk radius pencarian)
    // ---------------------------------------------------------------------
    val venues: List<Venue> = listOf(
        Venue("v1", "Coffee & Dice", "BSD", 0.6, 40.0),
        Venue("v2", "The Board Room", "Gading Serpong", 1.4, 250.0),
        Venue("v3", "Rooftop Café", "BSD", 0.9, 150.0),
        Venue("v4", "Meeple House", "Alam Sutera", 3.2, 10.0),
        Venue("v5", "Roll & Brew", "Karawaci", 5.1, 290.0),
        Venue("v6", "Tabletop Hub", "Tangerang Selatan", 2.4, 110.0),
        Venue("v7", "Kopi Catur", "BSD", 1.8, 195.0),
        Venue("v8", "Pavilion Games", "Bintaro", 4.3, 75.0)
    )

    // ---------------------------------------------------------------------
    // Pemain
    // ---------------------------------------------------------------------
    const val CURRENT_USER_ID = "me"

    val players: List<Player> = listOf(
        Player("me", "Dimas Pratama", 3, "BSD", "Weekend strategist. Will trade sheep for wood.", 4.8, 42, Level.COMPETITIVE),
        Player("u1", "Anisa Rahma", 1, "Gading Serpong", "Bird nerd. Wingspan every Friday.", 4.9, 88, Level.CASUAL),
        Player("u2", "Raka Wijaya", 2, "BSD", "Teaching new players is my favorite thing.", 4.7, 61, Level.CASUAL),
        Player("u3", "Kevin Tan", 3, "Alam Sutera", "Heavy euros, long nights.", 4.6, 120, Level.COMPETITIVE),
        Player("u4", "Sari Putri", 4, "Karawaci", "Just here for the snacks and Azul.", 4.9, 23, Level.BEGINNER),
        Player("u5", "Bima Santoso", 1, "Tangerang Selatan", "Ticket to Ride world traveler.", 4.5, 37, Level.CASUAL)
    )

    // ---------------------------------------------------------------------
    // Sesi bermain (s8 mahjong & s11 monopoly = sesi utama;
    // s7 = sesi lampau untuk tab Past di My Games)
    // ---------------------------------------------------------------------
    val sessions: List<GameSession> = listOf(
        GameSession("s8", "mahjong", "v1", "u4", "Tomorrow", "19:00", 4, listOf("u4", "u1"), Level.CASUAL, "Hong Kong rules, we'll teach scoring.", "Free"),
        GameSession("s11", "monopoly", "v8", "u5", "Sat", "14:00", 6, listOf("u5", "u1", "u4"), Level.CASUAL, "House rules: Free Parking jackpot.", "Rp20k"),
        GameSession("s1", "catan", "v1", "u2", "Tonight", "19:30", 4, listOf("u2", "u1", "u3"), Level.CASUAL, "Base game + Seafarers if we're quick. Beginners welcome!", "Free"),
        GameSession("s2", "wingspan", "v2", "u1", "Tonight", "20:00", 5, listOf("u1", "u4", "u5"), Level.CASUAL, "European expansion included.", "Rp25k"),
        GameSession("s3", "azul", "v3", "u4", "Tomorrow", "18:00", 4, listOf("u4", "u2", "u5"), Level.BEGINNER, "Chill game, rules taught.", "Free"),
        GameSession("s4", "mars", "v4", "u3", "Sat", "13:00", 5, listOf("u3", "me"), Level.COMPETITIVE, "Full corporate era with Prelude. Bring snacks.", "Rp30k"),
        GameSession("s5", "ticket", "v6", "u5", "Sat", "16:00", 5, listOf("u5"), Level.CASUAL, "Europe map.", "Free"),
        GameSession("s6", "splendor", "v5", "u3", "Sun", "15:00", 4, listOf("u3", "u4"), Level.CASUAL, "Quick games, multiple rounds.", "Rp15k"),
        GameSession("s9", "chess", "v7", "u3", "Tonight", "18:30", 2, listOf("u3"), Level.COMPETITIVE, "Rapid 15+10. Bring your best opening.", "Free"),
        GameSession("s10", "checkers", "v7", "u2", "Sun", "10:00", 2, listOf("u2"), Level.BEGINNER, "Relaxed Sunday morning game.", "Free"),
        GameSession("s12", "werewolf", "v6", "u1", "Sat", "19:00", 10, listOf("u1", "u2", "u4", "u5"), Level.BEGINNER, "Big group night, moderator provided.", "Rp10k"),
        GameSession("s13", "scrabble", "v3", "u4", "Sun", "16:00", 4, listOf("u4"), Level.CASUAL, "English dictionary, timer optional.", "Free"),
        GameSession("s7", "catan", "v2", "me", "Last Fri", "19:00", 4, listOf("me", "u1", "u2", "u4"), Level.CASUAL, "Done! Great games.", "Free")
    )

    /** Sesi yang sudah lewat (untuk tab "Past" di My Games). */
    val pastSessionIds: Set<String> = setOf("s7")

    // Opsi form
    val dayOptions = listOf("Tonight", "Tomorrow", "Sat", "Sun")
    val timeOptions = listOf("13:00", "16:00", "18:00", "19:30", "21:00")
    val feeOptions = listOf("Free", "Rp15k", "Rp25k", "Rp30k")

    // Warna avatar pemain p1–p4
    val playerColors = listOf(0xFFC07C56, 0xFF4B8A63, 0xFF557CAF, 0xFFA864A2)

    // ---------------------------------------------------------------------
    // Helper lookup
    // ---------------------------------------------------------------------
    fun gameById(id: String): Game? = games.find { it.id == id }
    fun venueById(id: String): Venue? = venues.find { it.id == id }
    fun playerById(id: String): Player? = players.find { it.id == id }

    /** Sesi AKTIF (belum lewat) untuk sebuah game. */
    fun sessionsForGame(gameId: String): List<GameSession> =
        sessions.filter { it.gameId == gameId && it.id !in pastSessionIds }
}
