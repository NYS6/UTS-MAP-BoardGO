package id.boardgo.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shapes custom BoardGO (mengikuti radius web app: base 1rem / 16dp,
 * kartu 24dp, chip pill).
 */
val BoardGoShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/** Radius khusus yang dipakai berulang di komponen BoardGO. */
object BoardGoRadius {
    val card = 20.dp
    val chip = 50
    val sheet = 24.dp
}
