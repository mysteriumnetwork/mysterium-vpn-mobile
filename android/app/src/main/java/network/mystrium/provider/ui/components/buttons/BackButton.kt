package network.mystrium.provider.ui.components.buttons

import androidx.compose.foundation.Image
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import network.mystrium.provider.R

@Composable
fun BackButton(
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Image(
            painter = painterResource(id = R.drawable.ic_back),
            contentDescription = null
        )
    }
}