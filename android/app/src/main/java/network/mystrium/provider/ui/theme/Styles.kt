package network.mystrium.provider.ui.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Colors {
    val primary = Color(0xFFD61F85)
    val navHeader = Color.White
    val statusBar = Color(0xFF230536)
    val bgGradient = listOf(
        Color(0xFF230536),
        Color(0xFF711B58)
    )
    val textBg = Color.White
    val textButton = Color(0xFF6A678E)
    val textPrimary = Color(0xFF363355)
    val textSecondary = Color(0xFF6A678E)
    val primaryBg = Color.White
    val secondaryBg = Color(0xFFF8F9FD)
    val shadow = Color(0x1A090064)
    val textFieldBorder = Color(0xFFE2E1EF)
    val textFieldError = Color(0xFFF44D89)
    val textFieldErrorBg = Color(0xFFFFF7FA)
}

object Styles {
    val background = Brush.verticalGradient(Colors.bgGradient)
}

object TextStyles {
    val navigationHeader = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 20.sp
    )
    val logoParagraph = ParagraphStyle(
        lineHeight = 58.sp
    )
    val logoSpan = SpanStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Black,
        fontSize = 46.sp,
    )
    val body = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp
    )
    val label = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 15.sp
    )
    val button = body
    val header = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Black,
        fontSize = 20.sp,
        lineHeight = 25.sp
    )
}

object Paddings {
    val tiny = 2.dp
    val small = 8.dp
    val default = 18.dp
    val logoDescription = 33.dp
    val onboardButton = PaddingValues(
        horizontal = 65.dp,
        vertical = 53.dp
    )
    val applyButton = PaddingValues(
        top = 0.dp,
        start = 65.dp,
        end = 65.dp,
        bottom = 40.dp
    )
    val primaryButton = PaddingValues(default)
    val secondaryButton = PaddingValues(
        horizontal = 18.dp,
        vertical = 6.dp
    )
    val card = PaddingValues(
        horizontal = 26.dp,
        vertical = 30.dp
    )
    val cardButton = PaddingValues(
        horizontal = 26.dp,
        vertical = 23.dp
    )
}

object Corners {
    val card = 30.dp
    val default = 20.dp
    val small = 10.dp
}
