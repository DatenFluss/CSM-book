package com.android.csm_book.presentation.content

import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun LatexView(formula: String, modifier: Modifier = Modifier) {
    val htmlContent = """
        <html>
        <head>
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/KaTeX/0.13.11/katex.min.css">
            <script src="https://cdnjs.cloudflare.com/ajax/libs/KaTeX/0.13.11/katex.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/KaTeX/0.13.11/contrib/auto-render.min.js"></script>
        </head>
        <body>
            <div id="formula">$formula</div>
            <script>
                renderMathInElement(document.body, {
                    delimiters: [
                        {left: "$$", right: "$$", display: true},
                        {left: "\\(", right: "\\)", display: false}
                    ]
                });
            </script>
        </body>
        </html>
    """

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.cacheMode = WebSettings.LOAD_NO_CACHE
                webViewClient = WebViewClient()
                loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            }
        },
        modifier = modifier.fillMaxWidth()
    )
}
