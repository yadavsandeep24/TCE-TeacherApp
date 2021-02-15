package com.tce.teacherapp.ui.dashboard.subjects

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.webkit.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.tceapi.ActivityVo
import com.tce.teacherapp.api.response.tceapi.Asset
import com.tce.teacherapp.databinding.FragmentHtmlContentBinding
import com.tce.teacherapp.db.entity.Resource
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.util.glide.SvgSoftwareLayerSetter
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.custom_playback_control.view.*
import kotlinx.coroutines.*
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class HTMLContentFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseSubjectFragment(R.layout.fragment_html_content, viewModelFactory) {
    private lateinit var binding: FragmentHtmlContentBinding
    var selectedResourceVo: Resource? = null
    var allTopicResourceList: List<Asset> = emptyList()
    var isDigitalAsset: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
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
        allTopicResourceList = arguments?.getParcelableArrayList("resourceList")!!
        selectedResourceVo = arguments?.getParcelable("resourceVo")

        val url = arguments?.getString("url")
        isDigitalAsset = arguments?.getBoolean("isDigitalAsset")
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
              //  uiCommunicationListener.displayProgressBar(false)

                insertJavascript()
            }
        }
        Glide.with(requireActivity())
            .load(R.drawable.group)
            .transform(BlurTransformation(200, 2))
            .into(binding.ivContainer)

        binding.wvHtml.loadUrl(url!!)
        binding.ivCancel.setOnClickListener {
            toggle(false)
/*
            val animate = TranslateAnimation(
                0F,  // fromXDelta
                0F,  // toXDelta
                0F,  // fromYDelta
                binding.container.height.toFloat()
            ) // toYDelta
animate.setAnimationListener(object : Animation.AnimationListener {
    override fun onAnimationStart(animation: Animation) {
    }

    override fun onAnimationEnd(animation: Animation) {
        insertJavascript()
        binding.container.visibility = View.GONE
    }

    override fun onAnimationRepeat(animation: Animation) {

    }
})
            animate.duration = 500
            animate.fillAfter = true
            binding.container.startAnimation(animate)*/


      /*
            val bottomDown: Animation = AnimationUtils.loadAnimation(
                context,
                R.anim.bottom_down
            )
            bottomDown.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {
                }

                override fun onAnimationEnd(animation: Animation) {
                    // insertJavascript()
                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
            binding.container.startAnimation(bottomDown)*/
        }

        // sendDataToWebView()

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
        webView.addJavascriptInterface(IUserInterface(requireActivity()), "Android")
        webView.webChromeClient = WebChromeClient()
    }


    inner class IUserInterface
    /**
     * Instantiate the interface and set the context
     */
    internal constructor(internal var mContext: Context) {

        @JavascriptInterface
        fun player_loaded() {
            Log.d("SAN", "player_loaded")
            activity?.runOnUiThread { uiCommunicationListener.displayProgressBar(false) }

        }

        @JavascriptInterface
        fun activity_next() {
            Log.d("SAN", "activity_next")
        }

        @JavascriptInterface
        fun activity_previous() {
            Log.d("SAN", "activity_previous")
        }

        @JavascriptInterface
        fun activity_back() {
            Log.d("SAN", "activity_back")
            activity?.runOnUiThread { activity?.onBackPressed() }
        }

        @JavascriptInterface
        fun activity_openmenu() {
            Log.d("SAN", "activity_openmenu")
            val resourceAsset = Asset(
                "",
                "resource",
                "",
                "",
                "",
                "",
                "Resource",
                "",
                "",
                "",
                "",
                "",
                "",
                "Resource",
                "",
                "",
                "",
                false
            )
            val tempResourceList = ArrayList<Asset>()
            tempResourceList.addAll(allTopicResourceList)
            tempResourceList.add(resourceAsset)
            activity?.runOnUiThread {
                setResourceItem(
                    requireContext().resources.configuration.orientation,
                    tempResourceList
                )
            }

        }

    }

    private fun sendDataToWebView() {
        GlobalScope.launch(Dispatchers.Main) {
            val currentResourcePosition = withContext(Dispatchers.IO) {
                if (allTopicResourceList != null) {
                    for (i in 0 until allTopicResourceList!!.size) {
                        val resource = allTopicResourceList!![i]
                        if (selectedResourceVo != null) {
                            /*    if (resource.id.equals(selectedResourceVo!!.id, true)) {
                                    return@withContext i
                                }*/

                        }
                    }
                } else {
                    return@withContext -1
                }
            }

            if (currentResourcePosition != -1) {
                var previousResourceVo: Resource? = null
                var nextResourceVo: Resource? = null
                /*       when (currentResourcePosition) {
                           0 -> {
                               nextResourceVo = allTopicResourceList!![currentResourcePosition as Int + 1]
                           }
                           allTopicResourceList!!.size - 1 -> {
                               previousResourceVo =
                                   allTopicResourceList!![currentResourcePosition as Int - 1]
                           }
                           else -> {
                               nextResourceVo = allTopicResourceList!![currentResourcePosition as Int + 1]
                               previousResourceVo = allTopicResourceList!![currentResourcePosition as Int - 1]
                           }
                       }*/
                val jsonObject = JsonObject()
                try {
                    jsonObject.addProperty("id", selectedResourceVo?.id)
                    jsonObject.addProperty("CurrentTitle", selectedResourceVo?.title)
                    if (nextResourceVo != null) {
                        jsonObject.addProperty("NextTitle", nextResourceVo.title)
                    } else {
                        jsonObject.addProperty("NextTitle", "")
                    }
                    if (previousResourceVo != null) {
                        jsonObject.addProperty("PrevTitle", previousResourceVo.title)
                    } else {
                        jsonObject.addProperty("PrevTitle", "")
                    }
                    jsonObject.addProperty("Src", selectedResourceVo?.src)
                    jsonObject.addProperty("webviewtype", "Android")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Log.d("SAN", "jsonObject-->$jsonObject")
                binding.wvHtml.evaluateJavascript(
                    "javascript: ex_tool_activity_Initiate($jsonObject)",
                    null
                )

            }
        }

    }

    private fun setResourceItem(
        orientation: Int,
        it: List<Asset>
    ) {
        toggle(true)
     /*   val bottomUp: Animation = AnimationUtils.loadAnimation(
            context,
            R.anim.bottom_up
        )
        bottomUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                binding.container.visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
        })
        binding.container.startAnimation(bottomUp)*/

        binding.llRow1.removeAllViews()
        binding.llRow2.removeAllViews()
        binding.llRow3.removeAllViews()
        binding.llRow4.removeAllViews()
        binding.llRow5.removeAllViews()
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding.llRow4.visibility = View.VISIBLE
            binding.llRow5.visibility = View.VISIBLE
            val chunked = it.chunked(3)

            for (j in chunked.indices) {
                val chunkedResourceTypeList = chunked[j]
                val colSize = chunkedResourceTypeList.size
                for (i in chunkedResourceTypeList.indices) {
                    if (colSize == 1) {
                        setResourceCell(getParentView(j), chunkedResourceTypeList[i], 3)
                    } else if (colSize == 2) {
                        if (i == 0) {
                            setResourceCell(getParentView(j), chunkedResourceTypeList[i], 1)
                        } else {
                            setResourceCell(getParentView(j), chunkedResourceTypeList[i], 2)
                        }
                    } else if (colSize == 3) {
                        when (i) {
                            0 -> {
                                setResourceCell(getParentView(j), chunkedResourceTypeList[i], 1)
                            }
                            1 -> {
                                setResourceCell(getParentView(j), chunkedResourceTypeList[i], -1)
                            }
                            2 -> {
                                setResourceCell(getParentView(j), chunkedResourceTypeList[i], 2)
                            }
                        }
                    }
                }
            }
        } else {
            binding.llRow4.visibility = View.GONE
            binding.llRow5.visibility = View.GONE
            val chunked = it.chunked(5)

            for (j in chunked.indices) {
                val chunkedResourceTypeList = chunked[j]
                val colSize = chunkedResourceTypeList.size
                for (i in chunkedResourceTypeList.indices) {
                    if (colSize == 1) {
                        setResourceCell(getParentView(j), chunkedResourceTypeList[i], 3)
                    } else if (colSize == 2) {
                        if (i == 0) {
                            setResourceCell(getParentView(j), chunkedResourceTypeList[i], 1)
                        } else {
                            setResourceCell(getParentView(j), chunkedResourceTypeList[i], 2)
                        }
                    } else {
                        when (i) {
                            0 -> {
                                setResourceCell(getParentView(j), chunkedResourceTypeList[i], 1)
                            }
                            colSize - 1 -> {
                                setResourceCell(getParentView(j), chunkedResourceTypeList[i], 2)
                            }
                            else -> {
                                setResourceCell(getParentView(j), chunkedResourceTypeList[i], -1)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getParentView(position: Int): LinearLayout {
        return when (position) {
            0 -> {
                binding.llRow1
            }
            1 -> {
                binding.llRow2
            }
            2 -> {
                binding.llRow3
            }
            3 -> {
                binding.llRow4
            }
            4 -> {
                binding.llRow5
            }
            else -> {
                binding.llRow1
            }
        }
    }

    private fun setResourceCell(
        parentView: LinearLayout,
        chapterResourceType: Asset,
        viewType: Int
    ) {
        var view = LayoutInflater.from(activity)
            .inflate(R.layout.resource_selection_item_center, parentView, false)
        if (viewType == 1) {
            view = LayoutInflater.from(activity)
                .inflate(R.layout.resource_selection_item_left, parentView, false)
        } else if (viewType == 2) {
            view = LayoutInflater.from(activity)
                .inflate(R.layout.resource_selection_item_right, parentView, false)
        } else if (viewType == 3) {
            view = LayoutInflater.from(activity)
                .inflate(R.layout.resource_selection_item_left_right, parentView, false)
        }
        val ivIcon = view.findViewById<AppCompatImageView>(R.id.iv_icon)
        val tvName = view.findViewById<TextView>(R.id.tv_name)
        var iconDrawable: Drawable? = null
        /*    if(chapterResourceType.icon != null && chapterResourceType.icon!!.isNotEmpty()) {
                iconDrawable = Utility.getDrawable((chapterResourceType.icon?.substring(0,chapterResourceType.icon!!.lastIndexOf(".")))?.replace("-","_"), requireContext())
            }*/
        if (!chapterResourceType.thumbFileName.isNullOrEmpty()) {
            val url = GlideUrl(
                "http://172.18.1.57:8080/tce-repo-api/1/web/1/content/fileservice/${chapterResourceType.encryptedFilePath}/${chapterResourceType.assetId}/${chapterResourceType.thumbFileName}",
                LazyHeaders.Builder()
                    .addHeader("Cookie", "access_token=${viewModel.getAccessToken()}")
                    .build()
            )
            val requestBuilder = Glide.with(requireActivity())
                .`as`(PictureDrawable::class.java)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(SvgSoftwareLayerSetter())
            requestBuilder.load(url).into(ivIcon)
        } else {
            if (chapterResourceType.assetId.equals("pdf", true)) {
                ivIcon.setBackgroundDrawable(resources.getDrawable(R.drawable.e_book))
            } else {
                ivIcon.setBackgroundDrawable(resources.getDrawable(R.drawable.ic_resources))
            }
        }
        tvName.text = chapterResourceType.title
        parentView.addView(view)
        view.tag = chapterResourceType.assetId
        setListener(view)
    }

    private fun setListener(view: View) {
        view.setOnClickListener {
            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.IO) {
                    val selectedAssetId = view.tag.toString()
                    for (resources in allTopicResourceList) {
                        resources.bValue = resources.assetId.equals(selectedAssetId, true)
                    }
                }
                toggle(false)
                uiCommunicationListener.displayProgressBar(true)
                insertJavascript()

            }
        }

    }

    private fun insertJavascript() {
        val activityVo = isDigitalAsset?.let {
            ActivityVo(
                viewModel.getAccessToken(),
                viewModel.getSessionTImeOut(),
                it,
                allTopicResourceList,
                "android",
                viewModel.getRefreshToken()
            )
        }
        val gson = Gson()
        val json = gson.toJson(activityVo)

        val value = json.toString()
        Log.d("SAN", "jsonObject-->$value")
        binding.wvHtml.evaluateJavascript(
            "javascript: ex_tool_activity_Initiate(JSON.stringify($value))",
            null
        )
    }

    private fun toggle(show: Boolean) {
        val redLayout: View = binding.container
        val parent: ViewGroup = binding.flMain
        val transition: Transition = Slide(Gravity.BOTTOM)
        transition.duration = 600
        transition.addTarget(redLayout)
        TransitionManager.beginDelayedTransition(parent, transition)
        redLayout.visibility = if (show) View.VISIBLE else View.GONE
    }
}