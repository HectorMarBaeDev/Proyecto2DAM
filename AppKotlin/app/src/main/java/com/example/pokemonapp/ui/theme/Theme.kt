package com.example.pokemonapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary            = LightBlue,
    onPrimary          = BackgroundDark,
    primaryContainer   = DarkBlue,
    onPrimaryContainer = BackgroundDark,

    secondary          = Blue,
    onSecondary        = BackgroundDark,
    secondaryContainer = CardDark,
    onSecondaryContainer = androidx.compose.ui.graphics.Color.White,

    tertiary           = PokemonYellow,
    onTertiary         = BackgroundDark,

    background         = BackgroundDark,
    onBackground       = androidx.compose.ui.graphics.Color.White,

    surface            = SurfaceDark,
    onSurface          = androidx.compose.ui.graphics.Color.White,

    surfaceVariant     = CardDark,
    onSurfaceVariant   = androidx.compose.ui.graphics.Color(0xFFCCCCCC),

    error              = androidx.compose.ui.graphics.Color(0xFFCF6679),
    onError            = BackgroundDark,
)

private val LightColorScheme = DarkColorScheme

@Composable
fun PokemonAppTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DarkBlue.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}