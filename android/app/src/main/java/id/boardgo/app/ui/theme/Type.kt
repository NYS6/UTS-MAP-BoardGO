package id.boardgo.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import id.boardgo.app.R

val AntonFamily = FontFamily(
    Font(R.font.anton_regular, FontWeight.Normal),
    Font(R.font.anton_regular, FontWeight.Medium),
    Font(R.font.anton_regular, FontWeight.SemiBold),
    Font(R.font.anton_regular, FontWeight.Bold),
    Font(R.font.anton_regular, FontWeight.Black)
)

val MonoFamily = FontFamily(
    Font(R.font.jetbrainsmono_regular, FontWeight.Normal),
    Font(R.font.jetbrainsmono_medium, FontWeight.Medium),
    Font(R.font.jetbrainsmono_bold, FontWeight.Bold)
)

val BoardGoTypography = Typography(

    displayLarge = TextStyle(
        fontFamily = AntonFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 44.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Start
    ),
    displayMedium = TextStyle(
        fontFamily = AntonFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp,
        lineHeight = 34.sp
    ),
    displaySmall = TextStyle(
        fontFamily = AntonFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 27.sp,
        lineHeight = 28.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = AntonFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 22.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = AntonFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 19.sp
    ),

    titleLarge = TextStyle(
        fontFamily = AntonFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 19.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.5.sp,
        lineHeight = 19.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),

    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        letterSpacing = 0.2.sp
    ),

    labelMedium = TextStyle(
        fontFamily = MonoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp
    ),

    labelSmall = TextStyle(
        fontFamily = MonoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 9.sp,
        letterSpacing = 0.7.sp
    )
)

