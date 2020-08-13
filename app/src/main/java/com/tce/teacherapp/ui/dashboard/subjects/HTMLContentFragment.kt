package com.tce.teacherapp.ui.dashboard.subjects

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentHtmlContentBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class HTMLContentFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseSubjectFragment(R.layout.fragment_html_content,viewModelFactory) {
    private lateinit var binding: FragmentHtmlContentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        binding = FragmentHtmlContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).showHideBottomBar(false)
        val url = arguments?.getString("url")
        setProperties(binding.wvHtml)
        binding.wvHtml.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView, urlNewString: String): Boolean {
                view.loadUrl(urlNewString)
                return true
            }

            override fun onPageStarted(view: WebView, url: String, facIcon: Bitmap?) {
                uiCommunicationListener.displayProgressBar(true)
            }

            override fun onPageFinished(view: WebView, url: String) {
              uiCommunicationListener.displayProgressBar(false)

            }
        }
        binding.wvHtml.loadUrl(url)
    }


    @SuppressLint("JavascriptInterface", "SetJavaScriptEnabled")
    private fun setProperties(webView: WebView) {
        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webView.isFocusable = true
        webView.isFocusableInTouchMode = true
        webSettings.allowFileAccessFromFileURLs = true
        webSettings.allowUniversalAccessFromFileURLs = true
        webSettings.allowContentAccess = true
        webSettings.allowFileAccess = true
        webSettings.pluginState = WebSettings.PluginState.ON
        webSettings.javaScriptCanOpenWindowsAutomatically
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.loadsImagesAutomatically = true
        webSettings.domStorageEnabled = true
        webSettings.databaseEnabled = true
        webSettings.setAppCacheEnabled(true)
        webSettings.defaultTextEncodingName = "utf-8"
        webSettings.builtInZoomControls = true
        webSettings.displayZoomControls = false
        webView.webChromeClient = WebChromeClient()
    }
}