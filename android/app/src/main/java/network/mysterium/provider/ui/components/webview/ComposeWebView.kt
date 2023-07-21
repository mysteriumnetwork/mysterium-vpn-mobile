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
    ignoreList: List<String> = emptyList(),
    onLoadUrl: (Uri) -> Unit = {},
    onReload: (() -> Unit) -> Unit = {},
    onIgnoreCallback: (Uri) -> Unit = {},
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
                        val loadUrlPath = loadUrl.toString()
                        return if (loadUrlPath.isNotEmpty() && ignoreList.any { url -> loadUrlPath.contains(url) }) {
                            onIgnoreCallback(loadUrl)
                            true
                        } else {
                            onLoadUrl(loadUrl)
                            false
                        }
                    }
                }.apply {
                    settings.databaseEnabled = true
                    settings.javaScriptEnabled = true
                    settings.javaScriptCanOpenWindowsAutomatically = true
                    settings.domStorageEnabled = true
                }
                onReload {
                    loadUrl(url)
                }
            }
        }
    ) {
        it.loadUrl(url)
    }
}
