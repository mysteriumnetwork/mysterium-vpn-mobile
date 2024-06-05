package network.mysterium.provider.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import network.mysterium.provider.R

val FontFamily.Companion.DMSans
    get() = dmsansFontFamily

val FontFamily.Companion.OpenSans
    get() = opensansFontFamily

private val dmsansFontFamily = FontFamily(
    Font(R.font.dmsans_black, weight = FontWeight.Black),
    Font(R.font.dmsans_bold, weight = FontWeight.Bold),
    Font(R.font.dmsans_extrabold, weight = FontWeight.ExtraBold),
    Font(R.font.dmsans_extralight, weight = FontWeight.ExtraLight),
    Font(R.font.dmsans_light, weight = FontWeight.Light),
    Font(R.font.dmsans_medium, weight = FontWeight.Medium),
    Font(R.font.dmsans_regular, weight = FontWeight.Normal),
    Font(R.font.dmsans_semibold, weight = FontWeight.SemiBold),
    Font(R.font.dmsans_thin, weight = FontWeight.Thin),
)

private val opensansFontFamily = FontFamily(
    Font(R.font.opensans_extrabold, weight = FontWeight.ExtraBold),
)

