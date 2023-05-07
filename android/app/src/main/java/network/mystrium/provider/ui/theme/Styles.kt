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
    val button = body
}

object Paddings {
    val small = 10.dp
    val default = 18.dp
    val logoDescription = 33.dp
    val logoText = 67.dp
    val onboardButton = PaddingValues(
        horizontal = 45.dp,
        vertical = 33.dp
    )
    val buttonContent = PaddingValues(default)
}

object Corners {
    val card = 30.dp
    val default = 20.dp
}
