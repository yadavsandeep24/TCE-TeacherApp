package com.tce.teacherapp.ui.dashboard.subjects

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
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
import com.tce.teacherapp.databinding.FragmentVideoPlayerBinding
import com.tce.teacherapp.db.entity.Resource
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import kotlinx.android.synthetic.main.custom_playback_control.view.*
import kotlinx.coroutines.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class VideoPlayerFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseSubjectFragment(R.layout.fragment_video_player,viewModelFactory) {

    private lateinit var binding: FragmentVideoPlayerBinding
    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0
    private var playbackStateListener: PlaybackStateListener? = null
    var selectedResourceVo:Resource? =null
    var allTopicResourceList:ArrayList<Resource>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        binding = FragmentVideoPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tvTitle = arguments?.getString("title")
        val isShowModalityIcon = arguments?.getBoolean("isModality")
        allTopicResourceList = arguments?.getParcelableArrayList("resourceList")
        selectedResourceVo = arguments?.getParcelable("resourceVo")

        if(isShowModalityIcon != null && !isShowModalityIcon) {
            binding.playerView.ib_modality.visibility = View.GONE
        }else{
            binding.playerView.ib_modality.visibility = View.VISIBLE
        }
        setNextAndPreviousButtonVisibility()
        binding.playerView.tv_title.text = tvTitle
        playbackStateListener = PlaybackStateListener()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).showHideBottomBar(false)

        binding.playerView.iv_close.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.playerView.ib_modality.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setNextAndPreviousButtonVisibility() {
        GlobalScope.launch(Dispatchers.Main) {
            val currentResourcePosition = withContext(Dispatchers.IO) {
                if (allTopicResourceList != null) {
                    for (i in 0 until allTopicResourceList!!.size){
                        val resource = allTopicResourceList!![i]
                        if (selectedResourceVo != null) {
                            if(resource.id.equals(selectedResourceVo!!.id,true)) {
                                return@withContext i
                            }

                        }
                    }
                }else{
                    return@withContext  -1
                }
            }

            if(currentResourcePosition == -1){
                binding.playerView.ib_prev.isEnabled = false
                binding.playerView.ib_next.isEnabled = false
                binding.playerView.ib_prev.alpha =0.4f
                binding.playerView.ib_next.alpha =0.4f
            }else{
                if(currentResourcePosition ==0){
                    binding.playerView.ib_prev.isEnabled = false
                    binding.playerView.ib_prev.alpha =0.4f
                }else{
                    if (allTopicResourceList != null) {
                        if(currentResourcePosition == allTopicResourceList!!.size-1) {
                            binding.playerView.ib_next.isEnabled = false
                            binding.playerView.ib_next.alpha =0.4f
                        }else{
                            binding.playerView.ib_prev.isEnabled = true
                            binding.playerView.ib_next.isEnabled = true
                            binding.playerView.ib_prev.alpha =1f
                            binding.playerView.ib_next.alpha =1f
                            binding.playerView.ib_prev.setOnClickListener {
                                val prevPos = currentResourcePosition as Int -1
                                Log.d("SAN", allTopicResourceList!![prevPos].title)
                                val resource = allTopicResourceList!![prevPos]
                                val bundle = Bundle()
                                val arrList = ArrayList<Resource>()
                                arrList.addAll(allTopicResourceList!!)
                                bundle.putParcelableArrayList("resourceList",arrList)
                                bundle.putParcelable("resourceVo",resource)
                                bundle.putString("title","-"+resource.title)
                                bundle.putString("url",resource.src)
                                when {
                                    resource.contenttype.equals("av", true) -> {
                                        binding.playerView.tv_title.text = resource.title
                                        selectedResourceVo = resource
                                        setNextAndPreviousButtonVisibility()
                                        initializePlayer(resource.src)
                                    }
                                    resource.contenttype.equals("Image", true) -> {
                                        //findNavController().navigate(R.id.action_chapterResourceSelectionFragment_to_imageContentFragment,bundle)
                                    }
                                    resource.contenttype.equals("activity", true) -> {
                                       // findNavController().navigate(R.id.action_chapterResourceSelectionFragment_to_HTMLContentFragment,bundle)
                                    }

                                }
                            }
                            binding.playerView.ib_next.setOnClickListener {
                                val nextPos = currentResourcePosition as Int +1
                                Log.d("SAN", allTopicResourceList!![nextPos].title!!)
                                val resource = allTopicResourceList!![nextPos]
                                val bundle = Bundle()
                                val arrList = ArrayList<Resource>()
                                arrList.addAll(allTopicResourceList!!)
                                bundle.putParcelableArrayList("resourceList",arrList)
                                bundle.putParcelable("resourceVo",resource)
                                bundle.putString("title","-"+resource.title)
                                bundle.putString("url",resource.src)
                                when {
                                    resource.contenttype.equals("av", true) -> {
                                        binding.playerView.tv_title.text = resource.title
                                        selectedResourceVo = resource
                                        setNextAndPreviousButtonVisibility()
                                        initializePlayer(resource.src)
                                    }
                                    resource.contenttype.equals("Image", true) -> {
                                        //findNavController().navigate(R.id.action_chapterResourceSelectionFragment_to_imageContentFragment,bundle)
                                    }
                                    resource.contenttype.equals("activity", true) -> {
                                        //findNavController().navigate(R.id.action_chapterResourceSelectionFragment_to_HTMLContentFragment,bundle)
                                    }
                                    resource.contenttype.equals("audio", true) -> {
                                    }
                                }
                            }
                        }

                    }
                }

            }



        }

    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            val url = arguments?.getString("url")
            initializePlayer(url)
        }
    }

    override fun onResume() {
        super.onResume()
      //  hideSystemUi()
        if (Util.SDK_INT <= 23 || player == null) {
            val url = arguments?.getString("url")
            initializePlayer(url)
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

    private fun initializePlayer(url:String?) {
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