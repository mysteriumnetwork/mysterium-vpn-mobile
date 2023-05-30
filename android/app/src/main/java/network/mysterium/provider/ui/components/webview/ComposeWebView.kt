package network.mysterium.provider.ui.components.webview

import android.annotation.SuppressLint
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

private const val TAG: String = "ComposeWebView"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ComposeWebView(
    modifier: Modifier = Modifier,
    url: String,
    onLoadUrl: (Uri) -> Unit = {},
    onReload: (() -> Unit) -> Unit = {}
) {
    AndroidView(
        modifier = modifier
            .fillMaxSize(),
        factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val loadUrl = request?.url ?: return false
                        onLoadUrl(loadUrl)
                        return false
                    }
                }.apply {
                    settings.javaScriptEnabled = true
                    settings.javaScriptCanOpenWindowsAutomatically = true
                    settings.domStorageEnabled = true
                }
                loadUrl(url)
                onReload {
                    loadUrl(url)
                }
            }
        }
    ) {
        it.loadUrl(url)
    }
}
