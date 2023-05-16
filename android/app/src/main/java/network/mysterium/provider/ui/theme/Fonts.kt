package network.mysterium.provider.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import network.mysterium.provider.R

val FontFamily.Companion.Lexend
    get() = lexendFontFamily

private val lexendFontFamily = FontFamily(
    Font(R.font.lexend_black, weight = FontWeight.Black),
    Font(R.font.lexend_bold, weight = FontWeight.Bold),
    Font(R.font.lexend_extrabold, weight = FontWeight.ExtraBold),
    Font(R.font.lexend_extralight, weight = FontWeight.ExtraLight),
    Font(R.font.lexend_light, weight = FontWeight.Light),
    Font(R.font.lexend_medium, weight = FontWeight.Medium),
    Font(R.font.lexend_regular, weight = FontWeight.Normal),
    Font(R.font.lexend_semibold, weight = FontWeight.SemiBold),
    Font(R.font.lexend_thin, weight = FontWeight.Thin),
)