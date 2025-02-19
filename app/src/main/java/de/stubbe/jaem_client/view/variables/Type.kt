package de.stubbe.jaem_client.view.variables

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import de.stubbe.jaem_client.R

val Rationale = FontFamily(
    Font(R.font.rationale_regular, FontWeight.Thin),
    Font(R.font.rationale_regular, FontWeight.ExtraLight),
    Font(R.font.rationale_regular, FontWeight.Light),
    Font(R.font.rationale_regular, FontWeight.Normal),
    Font(R.font.rationale_regular, FontWeight.Medium),
    Font(R.font.rationale_regular, FontWeight.SemiBold),
    Font(R.font.rationale_regular, FontWeight.Bold),
    Font(R.font.rationale_regular, FontWeight.ExtraBold),
    Font(R.font.rationale_regular, FontWeight.Black),
)

val RaviPrakash = FontFamily(
    Font(R.font.ravi_prakash_regular, FontWeight.Thin),
    Font(R.font.ravi_prakash_regular, FontWeight.ExtraLight),
    Font(R.font.ravi_prakash_regular, FontWeight.Light),
    Font(R.font.ravi_prakash_regular, FontWeight.Normal),
    Font(R.font.ravi_prakash_regular, FontWeight.Medium),
    Font(R.font.ravi_prakash_regular, FontWeight.SemiBold),
    Font(R.font.ravi_prakash_regular, FontWeight.Bold),
    Font(R.font.ravi_prakash_regular, FontWeight.ExtraBold),
    Font(R.font.ravi_prakash_regular, FontWeight.Black),
)

val CustomTypography = Typography(
    displayLarge = Typography().displayLarge.copy(fontFamily = Rationale),
    displayMedium = Typography().displayMedium.copy(fontFamily = Rationale),
    displaySmall = Typography().displaySmall.copy(fontFamily = Rationale),

    headlineLarge = Typography().headlineLarge.copy(fontFamily = Rationale),
    headlineMedium = Typography().headlineMedium.copy(fontFamily = Rationale),
    headlineSmall = Typography().headlineSmall.copy(fontFamily = Rationale),

    titleLarge = Typography().titleLarge.copy(fontFamily = Rationale),
    titleMedium = Typography().titleMedium.copy(fontFamily = Rationale),
    titleSmall = Typography().titleSmall.copy(fontFamily = Rationale),

    bodyLarge = Typography().bodyLarge.copy(fontFamily = Rationale),
    bodyMedium = Typography().bodyMedium.copy(fontFamily = Rationale),
    bodySmall = Typography().bodySmall.copy(fontFamily = Rationale),

    labelLarge = Typography().labelLarge.copy(fontFamily = Rationale),
    labelMedium = Typography().labelMedium.copy(fontFamily = Rationale),
    labelSmall = Typography().labelSmall.copy(fontFamily = Rationale)
)