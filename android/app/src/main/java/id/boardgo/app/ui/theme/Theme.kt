package id.boardgo.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Theme Material 3 CUSTOM BoardGO.
 * Sengaja tidak memakai MaterialTheme() bawaan Android Studio —
 * seluruh warna/tipografi/shape didefinisikan manual di package ui.theme.
 */

private val LightColors = lightColorScheme(
    primary = BoardGoColors.Primary,
    onPrimary = BoardGoColors.OnPrimary,
    primaryContainer = BoardGoColors.PrimaryContainer,
    onPrimaryContainer = BoardGoColors.OnPrimaryContainer,
    secondary = BoardGoColors.Secondary,
    onSecondary = BoardGoColors.OnSecondary,
    secondaryContainer = BoardGoColors.SurfaceVariant,
    onSecondaryContainer = BoardGoColors.Foreground,
    tertiary = BoardGoColors.Sky,
    onTertiary = BoardGoColors.OnPrimary,
    tertiaryContainer = BoardGoColors.SkyDeep,
    onTertiaryContainer = BoardGoColors.OnPrimary,
    background = BoardGoColors.Background,
    onBackground = BoardGoColors.Foreground,
    surface = BoardGoColors.Surface,
    onSurface = BoardGoColors.Foreground,
    surfaceVariant = BoardGoColors.SurfaceVariant,
    onSurfaceVariant = BoardGoColors.MutedForeground,
    outline = BoardGoColors.Outline,
    outlineVariant = BoardGoColors.OutlineStrong,
    error = BoardGoColors.Destructive,
    onError = BoardGoColors.OnDestructive,
    errorContainer = BoardGoColors.PrimaryContainer,
    onErrorContainer = BoardGoColors.OnPrimaryContainer,
    scrim = BoardGoColors.Foreground
)

/** Varian DARK (navy gelap) — mengikuti gaya glass/gelap web app. */
private val DarkColors = darkColorScheme(
    primary = BoardGoColors.Primary,
    onPrimary = BoardGoColors.OnPrimary,
    primaryContainer = Color(0xFF4A1A0E),
    onPrimaryContainer = Color(0xFFFFD9CE),
    secondary = Color(0xFF1E293B),
    onSecondary = Color(0xFFE8EEF7),
    secondaryContainer = Color(0xFF24324A),
    onSecondaryContainer = Color(0xFFC8D6E8),
    tertiary = Color(0xFF6E9AE8),
    onTertiary = Color(0xFF0B1B3A),
    tertiaryContainer = Color(0xFF1E3A6B),
    onTertiaryContainer = Color(0xFFD6E4FF),
    background = BoardGoDark.Background,
    onBackground = BoardGoDark.OnBackground,
    surface = BoardGoDark.Surface,
    onSurface = BoardGoDark.OnBackground,
    surfaceVariant = BoardGoDark.SurfaceVariant,
    onSurfaceVariant = BoardGoDark.OnSurfaceVariant,
    outline = BoardGoDark.Outline,
    outlineVariant = BoardGoDark.OutlineVariant,
    error = Color(0xFFEF5350),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFF5A1B1B),
    onErrorContainer = Color(0xFFFFD9D9),
    scrim = Color.Black
)

@Composable
fun BoardGoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Android 15+ (API 35) membuat warna system bar jadi edge-to-edge otomatis.
            @Suppress("DEPRECATION")
            if (android.os.Build.VERSION.SDK_INT < 35) {
                window.statusBarColor =
                    (if (darkTheme) BoardGoDark.Background else BoardGoColors.SkyDeep).toArgb()
                window.navigationBarColor = colorScheme.surface.toArgb()
            }
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BoardGoTypography,
        shapes = BoardGoShapes,
        content = content
    )
}
