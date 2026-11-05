package com.danpe2.githubapp

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.LinearLayout

class MainActivity : Activity() {

    private lateinit var webView: WebView
    private lateinit var modeButton: Button

    private var desktopMode = false
    private lateinit var mobileUserAgent: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        desktopMode = getPreferences(MODE_PRIVATE)
            .getBoolean(KEY_DESKTOP_MODE, false)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFFFFFFFF.toInt())
        }

        modeButton = Button(this).apply {
            textSize = 14f
            isAllCaps = false
            gravity = Gravity.CENTER
            setOnClickListener { toggleDisplayMode() }
        }

        root.addView(
            modeButton,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        webView = WebView(this)
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            mobileUserAgent = userAgentString
        }

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        root.addView(
            webView,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        setContentView(root)

        applyDisplayMode(reload = false)
        webView.loadUrl(GITHUB_URL)
    }

    private fun toggleDisplayMode() {
        desktopMode = !desktopMode
        getPreferences(MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DESKTOP_MODE, desktopMode)
            .apply()

        applyDisplayMode(reload = true)
    }

    private fun applyDisplayMode(reload: Boolean) {
        modeButton.text = if (desktopMode) {
            "Mudar para celular"
        } else {
            "Mudar para computador"
        }

        webView.settings.apply {
            userAgentString = if (desktopMode) {
                getDesktopUserAgent()
            } else {
                mobileUserAgent
            }

            useWideViewPort = true
            loadWithOverviewMode = true
        }

        if (reload) {
            webView.reload()
        }
    }

    private fun getDesktopUserAgent(): String {
        return mobileUserAgent
            .replace(Regex("Linux; Android [^)]*"), "X11; Linux x86_64")
            .replace(" Mobile ", " ")
            .replace("wv", "Safari")
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }

    companion object {
        private const val GITHUB_URL = "https://github.com"
        private const val KEY_DESKTOP_MODE = "desktop_mode"
    }
}