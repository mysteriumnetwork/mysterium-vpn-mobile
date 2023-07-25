package network.mysterium.provider.ui.components.markdown

import android.util.TypedValue
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import io.noties.markwon.Markwon
import network.mysterium.provider.R

@Composable
fun MarkdownText(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = TextStyle.Default,
    color: Color = Color.Unspecified
) {
    val context = LocalContext.current
    val markwon = remember { Markwon.create(context) }
    AndroidView(modifier = modifier,
        factory = {
            TextView(context).apply {
                typeface = ResourcesCompat.getFont(context, R.font.lexend_regular)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, textStyle.fontSize.value)
                if (color != Color.Unspecified) {
                    setTextColor(color.toArgb())
                }
                markwon.setMarkdown(this, text)

            }
        }, update = { markwon.setMarkdown(it, text) }
    )
}
