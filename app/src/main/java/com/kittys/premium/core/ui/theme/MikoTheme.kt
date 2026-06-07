package com.kittys.premium.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kittys.premium.core.ui.theme.MikoColors
import com.kittys.premium.core.ui.theme.MikoMaterialTypography

// ════════════════════════════════════════════════════════════════
//   MIKO — Theme Wrapper
// ════════════════════════════════════════════════════════════════

private val MikoLightColors = lightColorScheme(
    primary           = MikoColors.Primary,
    onPrimary         = MikoColors.TextOnPrimary,
    primaryContainer  = MikoColors.PastelLavender,
    onPrimaryContainer= MikoColors.TextPrimary,

    secondary         = MikoColors.Pink,
    onSecondary       = MikoColors.TextOnPrimary,
    secondaryContainer= MikoColors.SoftPink,
    onSecondaryContainer = MikoColors.TextPrimary,

    tertiary          = MikoColors.SoftSky,
    onTertiary        = MikoColors.TextPrimary,

    background        = MikoColors.Background,
    onBackground      = MikoColors.TextPrimary,
    surface           = MikoColors.Surface,
    onSurface         = MikoColors.TextPrimary,
    surfaceVariant    = MikoColors.SurfaceVariant,
    onSurfaceVariant  = MikoColors.TextSecondary,

    error             = MikoColors.Error,
    onError           = MikoColors.TextOnPrimary,
    errorContainer    = MikoColors.ErrorSoft,

    outline           = MikoColors.BorderMedium,
    outlineVariant    = MikoColors.BorderLight
)

private val MikoDarkColors = darkColorScheme(
    primary           = MikoColors.Primary,
    onPrimary         = MikoColors.TextOnPrimary,
    primaryContainer  = MikoColors.NavyPrimary,
    onPrimaryContainer= MikoColors.PastelLavender,

    secondary         = MikoColors.Pink,
    onSecondary       = MikoColors.TextOnPrimary,

    background        = MikoColors.DarkBackground,
    onBackground      = MikoColors.PastelLavender,
    surface           = MikoColors.DarkSurface,
    onSurface         = MikoColors.PastelLavender,

    error             = MikoColors.Error,
    onError           = MikoColors.TextOnPrimary
)

@Composable
fun MikoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) MikoDarkColors else MikoLightColors

    MaterialTheme(
        colorScheme = colors,
        typography  = MikoMaterialTypography,
        content     = content
    )
}

// Backward compat alias — old code using KittysTheme still works
@Composable
fun KittysTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = MikoTheme(darkTheme, content)
