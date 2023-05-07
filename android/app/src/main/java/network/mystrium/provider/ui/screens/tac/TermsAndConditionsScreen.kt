package network.mystrium.provider.ui.screens.tac

import androidx.compose.foundation.Image
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import network.mystrium.provider.R
import network.mystrium.provider.ui.components.content.ScreenContent
import network.mystrium.provider.ui.components.logo.HeaderLogo

@Composable
fun TermsAndConditionsScreen(
    toBack: () -> Unit
) {
    ScreenContent(
        title = {
            HeaderLogo()
        },
        navLeading = {
            IconButton(onClick = toBack) {
                Image(painter = painterResource(id = R.drawable.ic_back), contentDescription = null)
            }
        }
    ) {

    }
}

@Preview(showBackground = true)
@Composable
private fun TermsAndConditionsScreenPreview() {
    MaterialTheme {
        TermsAndConditionsScreen(toBack = {})
    }
}
