package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebPrototypeScreen(
  modifier: Modifier = Modifier
) {
  var isLoading by remember { mutableStateOf(true) }

  Box(modifier = modifier.fillMaxSize()) {
    AndroidView(
      factory = { context ->
        WebView(context).apply {
          settings.javaScriptEnabled = true
          settings.domStorageEnabled = true
          settings.loadWithOverviewMode = true
          settings.useWideViewPort = true
          settings.allowFileAccess = true
          settings.cacheMode = WebSettings.LOAD_DEFAULT

          webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
              super.onPageStarted(view, url, favicon)
              isLoading = true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
              super.onPageFinished(view, url)
              isLoading = false
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
              val url = request?.url?.toString() ?: return false
              if (url.startsWith("https://wa.me") || url.startsWith("whatsapp://")) {
                try {
                  val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                  context.startActivity(intent)
                  return true
                } catch (e: Exception) {
                  // Fallback
                }
              }
              return false
            }
          }

          webChromeClient = WebChromeClient()

          loadUrl("file:///android_asset/b2b_marketplace.html")
        }
      },
      modifier = Modifier.fillMaxSize()
    )

    if (isLoading) {
      CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
  }
}
