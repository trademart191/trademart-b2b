package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = B2BPrimaryDark,
    onPrimary = B2BOnPrimaryDark,
    primaryContainer = B2BPrimaryContainerDark,
    onPrimaryContainer = B2BOnPrimaryContainerDark,
    secondary = B2BSecondaryDark,
    onSecondary = B2BOnSecondaryDark,
    secondaryContainer = B2BSecondaryContainerDark,
    onSecondaryContainer = B2BOnSecondaryContainerDark,
    background = B2BBackgroundDark,
    onBackground = B2BOnBackgroundDark,
    surface = B2BSurfaceDark,
    onSurface = B2BOnSurfaceDark,
    surfaceVariant = B2BSurfaceVariantDark,
    onSurfaceVariant = B2BOnSurfaceVariantDark,
    outline = B2BOutlineDark,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = B2BPrimary,
    onPrimary = B2BOnPrimary,
    primaryContainer = B2BPrimaryContainerDark,
    onPrimaryContainer = B2BOnPrimaryContainer,
    secondary = B2BSecondary,
    onSecondary = B2BOnSecondary,
    secondaryContainer = B2BSecondaryContainer,
    onSecondaryContainer = B2BOnSecondaryContainer,
    tertiary = B2BTertiary,
    onTertiary = B2BOnTertiary,
    tertiaryContainer = B2BTertiaryContainer,
    onTertiaryContainer = B2BOnTertiaryContainer,
    background = B2BBackground,
    onBackground = B2BOnBackground,
    surface = B2BSurface,
    onSurface = B2BOnSurface,
    surfaceVariant = B2BSurfaceVariant,
    onSurfaceVariant = B2BOnSurfaceVariant,
    outline = B2BOutline,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent B2B branded colors
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
