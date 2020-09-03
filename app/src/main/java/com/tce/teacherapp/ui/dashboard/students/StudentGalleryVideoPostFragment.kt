package com.tce.teacherapp.ui.dashboard.students

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentGalleryData
import com.tce.teacherapp.databinding.FragmentStudentGalleryVideoPostBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.galley_playback_control_portrait.view.*
import kotlinx.android.synthetic.main.student_gallery_content_bottom_bar.view.*
import kotlinx.android.synthetic.main.student_gallery_content_edit_topbar.view.*
import kotlinx.android.synthetic.main.student_top_bar.view.tv_back
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class StudentGalleryVideoPostFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_student_gallery_video_post, viewModelFactory) {

    private lateinit var binding : FragmentStudentGalleryVideoPostBinding
    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var playbackStateListener: PlaybackStateListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        binding =  FragmentStudentGalleryVideoPostBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        playbackStateListener = PlaybackStateListener()
        val studentGallerydata = arguments?.getParcelable("studentGalleryData") as StudentGalleryData?
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).bottom_navigation_view.visibility = View.GONE

        binding.topBar.tv_back.setOnClickListener {
            activity?.onBackPressed()
        }

        if (studentGallerydata != null) {
            if(studentGallerydata.sharedItemList != null  && studentGallerydata.sharedItemList.isNotEmpty()){
                binding.tvSharedName.text = studentGallerydata.sharedItemList[0].Name
                binding.tvSharedCount.text = (studentGallerydata.sharedItemList.size -1).toString()+" others"
            }else{
                binding.llSharedUserContainer.visibility = View.GONE
            }
        }
        var lblCaption = studentGallerydata?.description
        binding.topBar.tv_tag.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("studentGalleryData",studentGallerydata)
            findNavController().navigate(R.id.action_studentGalleryVideoPostFragment_to_studentGalleryTagStudentFragment,bundle)
        }
        binding.playerView.img_expand.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("studentGalleryData",studentGallerydata)
            findNavController().navigate(R.id.action_studentGalleryVideoPostFragment_to_studentGalleryVideoPostDetailFragment,bundle)
        }

        binding.llSharedUserContainer.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("studentGalleryData",studentGallerydata)
            findNavController().navigate(R.id.action_studentGalleryVideoPostFragment_to_studentGalleryTaggedStudentFragment,bundle)
        }
        binding.topBar.tv_edit.setOnClickListener {
            binding.bottomBar.background = resources.getDrawable(R.color.white)
            binding.bottomBar.tv_title.setTextColor(resources.getColor(R.color.dark))
            binding.topBar.top_bar_title.visibility = View.VISIBLE
            binding.topBar.tv_save.visibility = View.VISIBLE
            binding.topBar.tv_tag.visibility = View.GONE
            binding.topBar.tv_edit.visibility = View.GONE
            binding.topBar.tv_delete.visibility = View.GONE
            binding.bottomBar.tv_desc.visibility = View.GONE
            binding.bottomBar.edt_caption.visibility = View.VISIBLE
            binding.bottomBar.edt_caption.setText(lblCaption)
            lblCaption?.length?.let { it1 -> binding.bottomBar.edt_caption.setSelection(it1) }
            binding.bottomBar.edt_caption.requestFocus()
        }

        binding.topBar.tv_save.setOnClickListener {
            lblCaption = binding.bottomBar.edt_caption.text.toString().trim()
            binding.bottomBar.tv_desc.text = lblCaption
            binding.bottomBar.tv_desc.tag = lblCaption
            makeTextViewResizable(binding.bottomBar.tv_desc, 1, "see more", true)
            binding.bottomBar.background = resources.getDrawable(R.color.color_black)
            binding.bottomBar.tv_title.setTextColor(resources.getColor(R.color.white))
            binding.topBar.top_bar_title.visibility = View.GONE
            binding.topBar.tv_save.visibility = View.GONE
            binding.topBar.tv_tag.visibility = View.VISIBLE
            binding.topBar.tv_edit.visibility = View.VISIBLE
            binding.topBar.tv_delete.visibility = View.VISIBLE
            binding.bottomBar.tv_desc.visibility = View.VISIBLE
            binding.bottomBar.edt_caption.visibility = View.GONE
        }
        binding.topBar.tv_delete.setOnClickListener {
            val dialog = Dialog(requireActivity(), android.R.style.Theme_Dialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.custom_yesno_dialog)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(resources.getColor(android.R.color.transparent))
            )
            dialog.show()

            val txtYes = dialog.findViewById(R.id.tvYes) as TextView
            val txtNo = dialog.findViewById(R.id.tvNo) as TextView

            val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = "Delete Video"
            val tvMessage = dialog.findViewById<TextView>(R.id.tv_message)
            tvMessage.text = "Are you sure you want to delete\n" +
                    "this video?"

            txtYes.setOnClickListener {
                dialog.dismiss()
            }
            txtNo.setOnClickListener {
                dialog.dismiss()
            }
        }
        binding.bottomBar.tv_desc.text = lblCaption
        if(binding.bottomBar.tv_desc.text.isNotEmpty()) {
            makeTextViewResizable(binding.bottomBar.tv_desc, 1, "see more", true)
        }
    }
    fun makeTextViewResizable(tv: TextView, maxLine: Int, expandText: String, viewMore: Boolean) {
        if (tv.tag == null) {
            tv.tag = tv.text
        }
        val vto = tv.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val obs = tv.viewTreeObserver
                obs.removeGlobalOnLayoutListener(this)
                if (maxLine == 0) {
                    val lineEndIndex = tv.layout.getLineEnd(0)
                    val text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                        .toString() + " " + expandText
                    tv.text = text
                    tv.movementMethod = LinkMovementMethod.getInstance()
                    tv.setText(
                        addClickablePartTextViewResizable(
                            Html.fromHtml(tv.text.toString()), tv, maxLine, expandText,
                            viewMore
                        ), TextView.BufferType.SPANNABLE
                    )
                } else if (maxLine > 0 && tv.lineCount >= maxLine) {
                    val lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                    val text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                        .toString() + " " + expandText
                    tv.text = text
                    tv.movementMethod = LinkMovementMethod.getInstance()
                    tv.setText(
                        addClickablePartTextViewResizable(
                            Html.fromHtml(tv.text.toString()), tv, maxLine, expandText,
                            viewMore
                        ), TextView.BufferType.SPANNABLE
                    )
                } else {
                    val lineEndIndex = tv.layout.getLineEnd(tv.layout.lineCount - 1)
                    val text = tv.text.subSequence(0, lineEndIndex).toString() + " " + expandText
                    tv.text = text
                    tv.movementMethod = LinkMovementMethod.getInstance()
                    tv.setText(
                        addClickablePartTextViewResizable(
                            Html.fromHtml(tv.text.toString()), tv, lineEndIndex, expandText,
                            viewMore
                        ), TextView.BufferType.SPANNABLE
                    )
                }
            }
        })
    }

    private fun addClickablePartTextViewResizable(
        strSpanned: Spanned, tv: TextView,
        maxLine: Int, spanableText: String, viewMore: Boolean
    ): SpannableStringBuilder? {
        val str = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)
        if (str.contains(spanableText)) {
            ssb.setSpan(object : MySpannable(true, requireContext()) {
                override fun onClick(widget: View) {
                    if (viewMore) {
                        tv.layoutParams = tv.layoutParams
                        tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                        tv.invalidate()
                        makeTextViewResizable(tv, -1, "see less", false)
                    } else {
                        tv.layoutParams = tv.layoutParams
                        tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                        tv.invalidate()
                        makeTextViewResizable(tv, 1, ".. see more", true)
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)
        }
        return ssb
    }

    open class MySpannable(isUnderline: Boolean, val context: Context) : ClickableSpan() {
        private var isUnderline = true
        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = isUnderline
            ds.color = Color.parseColor("#ffffff")
/*
            ds.textSize = context.resources.getDimension(R.dimen.font_size_14_dp)
*/
            ds.isFakeBoldText = true
        }

        override fun onClick(widget: View) {}

        /**
         * Constructor
         */
        init {
            this.isUnderline = isUnderline
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        //  hideSystemUi()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun initializePlayer() {
        val studentGallerydata = arguments?.getParcelable("studentGalleryData") as StudentGalleryData?
        val url = studentGallerydata?.src
        val trackSelector = DefaultTrackSelector(requireActivity())
        trackSelector.setParameters(
            trackSelector.buildUponParameters().setMaxVideoSizeSd()
        )
        if (player == null) {
            player = SimpleExoPlayer.Builder(requireActivity())
                .setTrackSelector(trackSelector)
                .build()
        }
        binding.playerView.player = player
        if (studentGallerydata != null) {
            if(studentGallerydata.contenttype.equals("audio",true)){
                binding.playerView.controllerHideOnTouch = false
                binding.playerView.controllerShowTimeoutMs =0
            }
        }
        val uri = Uri.parse(url)
        val mediaSource = buildMediaSource(uri)
        player!!.playWhenReady = playWhenReady
        player!!.seekTo(currentWindow, playbackPosition)
        playbackStateListener?.let { player!!.addListener(it) }
        player!!.prepare(mediaSource, false, false)
    }

    private fun releasePlayer() {
        if (player != null) {
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex
            playWhenReady = player!!.playWhenReady
            playbackStateListener?.let { player!!.removeListener(it) }
            player!!.release()
            player = null
        }
    }


    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(requireActivity(), "tce-exoplayer")
        return when (Util.inferContentType(uri)) {
            C.TYPE_HLS -> {
                HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            }
            C.TYPE_DASH -> {
                DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            }
            else -> {
                ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            }
        }
    }
    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        binding.playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private class PlaybackStateListener() : Player.EventListener {
        override fun onPlayerStateChanged(
            playWhenReady: Boolean,
            playbackState: Int
        ) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d("SAN", "changed state to $stateString playWhenReady: $playWhenReady")
        }

    }
}