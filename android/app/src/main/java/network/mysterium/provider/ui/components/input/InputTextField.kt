package network.mysterium.provider.ui.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import network.mysterium.provider.ui.theme.Colors
import network.mysterium.provider.ui.theme.Corners
import network.mysterium.provider.ui.theme.Paddings
import network.mysterium.provider.ui.theme.TextStyles

@Composable
fun InputTextField(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    info: String? = null,
    error: String? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val isError = error != null

    val borderWidth: Dp = when {
        isFocused -> 2.dp
        enabled -> 0.dp
        else -> 1.dp
    }
    val borderColor: Color = when {
        isError -> Colors.red500
        isFocused -> Colors.grey500
        title.isNotEmpty() -> Colors.grey300
        else -> Colors.grey200
    }
    val titleColor: Color = Colors.grey500
    val textFieldBg: Color = if (isError) {
        Colors.primaryBg
    } else {
        Color.Transparent
    }
    val textColor = if (isError) {
        Colors.red500
    } else {
        Colors.grey800
    }
    BasicTextField(
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyles.body3.copy(color = textColor),
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions
    ) { innerTextField ->
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(Paddings.small)
        ) {
            Row {
                Text(
                    text = title,
                    style = TextStyles.label,
                    color = titleColor
                )
                Spacer(modifier = Modifier.weight(1f))
                if (info != null) {
                    Text(
                        text = info,
                        style = TextStyles.label,
                        color = titleColor
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = textFieldBg)
                    .border(
                        width = borderWidth,
                        color = borderColor,
                        shape = RoundedCornerShape(Corners.small)
                    )
                    .padding(12.dp)
            ) {
                innerTextField()
            }

            error?.let {
                Text(
                    text = it,
                    style = TextStyles.label,
                    color = Colors.red500
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun InputTextFieldPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        InputTextField(
            modifier = Modifier.fillMaxWidth(),
            title = "Title",
            value = "Value",
            onValueChange = {},
            info = "MB"
        )

        InputTextField(
            modifier = Modifier.fillMaxWidth(),
            title = "Title",
            value = "Value",
            onValueChange = {},
            info = "MB",
            error = "Error"
        )
    }
}
