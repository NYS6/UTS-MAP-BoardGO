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

/** Varian gelap (opsional) — tetap memakai token warna BoardGO. */
private val DarkColors = darkColorScheme(
    primary = BoardGoColors.Primary,
    onPrimary = BoardGoColors.OnPrimary,
    primaryContainer = BoardGoColors.OnPrimaryContainer,
    onPrimaryContainer = BoardGoColors.PrimaryContainer,
    secondary = BoardGoColors.SurfaceVariant,
    onSecondary = BoardGoColors.Foreground,
    tertiary = BoardGoColors.Sky,
    onTertiary = BoardGoColors.OnPrimary,
    background = BoardGoColors.Foreground,
    onBackground = BoardGoColors.Surface,
    surface = Color(0xFF111A28),
    onSurface = BoardGoColors.Surface,
    surfaceVariant = Color(0xFF1B2433),
    onSurfaceVariant = BoardGoColors.MutedForeground,
    outline = BoardGoColors.OutlineStrong,
    error = BoardGoColors.Destructive,
    onError = BoardGoColors.OnDestructive
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
                window.statusBarColor = BoardGoColors.SkyDeep.toArgb()
                window.navigationBarColor = BoardGoColors.Surface.toArgb()
            }
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BoardGoTypography,
        shapes = BoardGoShapes,
        content = content
    )
}
