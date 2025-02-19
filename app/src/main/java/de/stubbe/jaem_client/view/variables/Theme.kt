package de.stubbe.jaem_client.view.variables

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import de.stubbe.jaem_client.datastore.UserPreferences.Theme

val lightTheme = object : JAEMTheme {
    override val primary: Color
        get() = PrimaryLight
    override val secondary: Color
        get() = SecondaryLight
    override val accent: Color
        get() = AccentLight
    override val background: Color
        get() = BackgroundLight
    override val textPrimary: Color
        get() = TextPrimaryLight
    override val textSecondary: Color
        get() = TextSecondaryLight
    override val border: Color
        get() = BorderLight
    override val error: Color
        get() = ErrorLight
}

val darkTheme = object : JAEMTheme {
    override val primary: Color
        get() = PrimaryDark
    override val secondary: Color
        get() = SecondaryDark
    override val accent: Color
        get() = AccentDark
    override val background: Color
        get() = BackgroundDark
    override val textPrimary: Color
        get() = TextPrimaryDark
    override val textSecondary: Color
        get() = TextSecondaryDark
    override val border: Color
        get() = BorderDark
    override val error: Color
        get() = ErrorDark
}

val LocalJAEMTheme = staticCompositionLocalOf {
    lightTheme
}

val LocalJAEMTypography = staticCompositionLocalOf {
    CustomTypography
}

@Composable
fun JAEMTheme(
    theme: Theme = Theme.LIGHT,
    content: @Composable () -> Unit
) {
    val jaemTheme = when (theme) {
        Theme.LIGHT -> lightTheme
        Theme.DARK -> darkTheme
        Theme.SYSTEM -> if (isSystemInDarkTheme()) darkTheme else lightTheme
        else -> lightTheme
    }

    val systemUiController = rememberSystemUiController()

    systemUiController.setSystemBarsColor(
        color = jaemTheme.background
    )

    val jeamTextSelectionColors = TextSelectionColors(
        handleColor = darkTheme.accent,
        backgroundColor = darkTheme.accent.copy(alpha = 0.4f),
    )

    CompositionLocalProvider(
        LocalJAEMTheme provides jaemTheme,
        LocalJAEMTypography provides LocalJAEMTypography.current,
        LocalTextSelectionColors provides jeamTextSelectionColors,
    ) {
        ProvideTextStyle(value = CustomTypography.bodyMedium, content = content)
    }
}


object JAEMThemeProvider {
    val current: JAEMTheme
        @Composable
        get() = LocalJAEMTheme.current
}
