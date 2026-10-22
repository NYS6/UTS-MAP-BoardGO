package id.boardgo.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Palet warna BoardGO — hasil konversi dari OKLCH di web app
 * (src/styles.css) ke ARGB hex agar bisa dipakai di Compose.
 */
object BoardGoColors {
    // Netral
    val Background = Color(0xFFE5ECF3)
    val Surface = Color(0xFFF4F7FB)
    val SurfaceVariant = Color(0xFFD7DFE8)
    val Foreground = Color(0xFF080F1C)
    val MutedForeground = Color(0xFF566577)
    val Outline = Color(0x33080F1C)
    val OutlineStrong = Color(0x22080F1C)

    // Brand
    val Primary = Color(0xFFF84F2F)      // oranye meeple
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimaryContainer = Color(0xFFFFE1DA)
    val OnPrimaryContainer = Color(0xFF5C1608)

    val Secondary = Color(0xFFD7DFE8)
    val OnSecondary = Color(0xFF080F1C)

    val Sky = Color(0xFF3E6DC8)
    val SkyDeep = Color(0xFF2750A2)
    val Meeple = Color(0xFFF2A119)
    val Success = Color(0xFF349D62)
    val Destructive = Color(0xFFDF2225)
    val OnDestructive = Color(0xFFFFFFFF)

    // Avatar pemain p1–p4
    val Player1 = Color(0xFFC07C56)
    val Player2 = Color(0xFF4B8A63)
    val Player3 = Color(0xFF557CAF)
    val Player4 = Color(0xFFA864A2)
}
