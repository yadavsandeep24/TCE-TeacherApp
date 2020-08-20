package com.tce.teacherapp.ui.dashboard.students

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
import com.tce.teacherapp.databinding.FragmentStudentGalleryVideoPostDetailBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import kotlinx.android.synthetic.main.gallery_playback_control_landscape.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class StudentGalleryVideoPostDetailFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_student_gallery_video_post_detail, viewModelFactory) {

    private lateinit var binding: FragmentStudentGalleryVideoPostDetailBinding
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
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        binding = FragmentStudentGalleryVideoPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playbackStateListener = PlaybackStateListener()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).showHideBottomBar(false)

        binding.playerView.img_minimize.setOnClickListener {
            val bundle = Bundle()
            val studentGallerydata = arguments?.getParcelable("studentGalleryData") as StudentGalleryData?
            bundle.putParcelable("studentGalleryData",studentGallerydata)
            findNavController().navigate(R.id.action_studentGalleryVideoPostDetailFragment_to_studentGalleryVideoPostFragment,bundle)
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