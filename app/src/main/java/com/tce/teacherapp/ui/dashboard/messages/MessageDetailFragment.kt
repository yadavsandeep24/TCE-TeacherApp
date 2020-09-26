package com.tce.teacherapp.ui.dashboard.messages

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.airbnb.epoxy.addGlidePreloader
import com.airbnb.epoxy.glidePreloader
import com.bumptech.glide.Glide
import com.example.gabriel.soundrecorder.recorder.RecorderViewModel
import com.example.gabriel.soundrecorder.util.InjectorUtils
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.MessageListResponseItem
import com.tce.teacherapp.api.response.Resource
import com.tce.teacherapp.databinding.FragmentMessageDetailBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.messages.adapter.ISelectedResourceClickListener
import com.tce.teacherapp.ui.dashboard.messages.adapter.messageDetailEpoxyHolder
import com.tce.teacherapp.ui.dashboard.messages.state.MessageStateEvent
import com.tce.teacherapp.ui.dashboard.subjects.adapter.SubjectListEpoxyHolder
import com.tce.teacherapp.ui.dashboard.subjects.loadImage
import com.tce.teacherapp.util.StateMessageCallback
import com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior
import com.tce.teacherapp.util.gallerypicker.model.GalleryAlbums
import com.tce.teacherapp.util.gallerypicker.model.GalleryData
import com.tce.teacherapp.util.gallerypicker.presenter.MessageDetailPhotosPresenterImpl
import com.tce.teacherapp.util.gallerypicker.presenter.MessageDetailVideosPresenterImpl
import com.tce.teacherapp.util.gallerypicker.utils.MLog
import com.tce.teacherapp.util.gallerypicker.utils.RunOnUiThread
import com.tce.teacherapp.util.gallerypicker.view.ImagePickerContract
import com.tce.teacherapp.util.gallerypicker.view.OnPhoneImagesObtained
import com.tce.teacherapp.util.gallerypicker.view.adapters.ImageGridAdapter
import com.tce.teacherapp.util.gallerypicker.view.adapters.VideoGridAdapter
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_group_chat.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.jetbrains.anko.doAsync
import java.io.File
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class MessageDetailFragment @Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseMessageFragment(R.layout.fragment_message_detail, viewModelFactory), ImagePickerContract,
    ISelectedResourceClickListener {

    private lateinit var binding: FragmentMessageDetailBinding
    var photoList: java.util.ArrayList<GalleryData> = java.util.ArrayList()
    var albumList: java.util.ArrayList<GalleryAlbums> = java.util.ArrayList()
    var messageVo: MessageListResponseItem? = null
    private var strFileType: String = "Photo"
    var photoids: java.util.ArrayList<Int> = java.util.ArrayList()
    val imagePickerPresenter: MessageDetailPhotosPresenterImpl =
        MessageDetailPhotosPresenterImpl(this)
    val videoPickerPresenter: MessageDetailVideosPresenterImpl =
        MessageDetailVideosPresenterImpl(this)
    lateinit var listener: OnPhoneImagesObtained
    private val PERMISSIONS_READ_WRITE = 123
    lateinit var ctx: Context
    lateinit var glm: GridLayoutManager
    private var viewModelRecorder: RecorderViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessageDetailBinding.inflate(inflater, container, false)
        ctx = inflater.context
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SAN", "onViewCreated")
        messageVo = arguments?.getParcelable("messageData") as MessageListResponseItem?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        // (activity as DashboardActivity).setCustomToolbar(R.layout.subject_list_top_bar)
        (activity as DashboardActivity).expandAppBar(false)

        // uiCommunicationListener.displayProgressBar(true)

        viewModel.setStateEvent(
            MessageStateEvent.GetMessageConversionEvent(
                messageVo!!.Type,
                messageVo!!.ToId!!
            )
        )

        if (messageVo != null) {
            binding.tvTitle1.text = messageVo!!.Title

            if (messageVo!!.Type.equals("1", false) || messageVo!!.Type.equals("2", false)) {
                binding.tvSubTitle1.visibility = View.GONE
            } else {
                binding.tvSubTitle1.visibility = View.VISIBLE
                binding.tvSubTitle1.text = messageVo!!.unReadCount.toString() + " Members"
            }

            when {
                messageVo!!.Type.equals("1", false) -> {
                    binding.sendMessage.visibility = View.VISIBLE
                    binding.imgMessageDetail.background =
                        requireContext().getDrawable(R.drawable.ic_profile_icon)
                }
                messageVo!!.Type.equals("2", false) -> {
                    binding.sendMessage.visibility = View.GONE
                    binding.imgMessageDetail.background =
                        requireContext().getDrawable(R.drawable.ic_dummy_school)
                }
                messageVo!!.Type.equals("3", false) -> {
                    binding.sendMessage.visibility = View.VISIBLE
                    binding.imgMessageDetail.background =
                        requireContext().getDrawable(R.drawable.ic_dummy_class_apple)
                }
            }

            binding.rvMessageDetail.layoutManager = GridLayoutManager(activity, 1)
            binding.rvMessageDetail.setHasFixedSize(true)
            val epoxyVisibilityTracker = EpoxyVisibilityTracker()
            epoxyVisibilityTracker.attach(binding.rvMessageDetail)
            binding.rvMessageDetail.addGlidePreloader(
                Glide.with(this),
                preloader = glidePreloader { requestManager, model: SubjectListEpoxyHolder, _ ->
                    requestManager.loadImage(model.imageUrl)
                }
            )

        }

        binding.headerContainer.setOnClickListener {
            if (messageVo!!.Type.equals("3", false)) {
                val bundle = Bundle()
                bundle.putParcelable("messageData", messageVo)
                findNavController().navigate(
                    R.id.action_messageDetailFragment_to_groupInfoFragment,
                    bundle
                )
            }
        }

        binding.imgBack.setOnClickListener {
            activity?.onBackPressed()
        }


        subscribeObservers()

        if (resources.getString(R.string.app_type)
                .equals(resources.getString(R.string.app_type_parent),true)) {
            binding.resourceContainer.visibility = View.GONE
            binding.divider1.visibility = View.GONE
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.isDraggable = false

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.state =
                        com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
                }

            }
        })

        binding.imgAttachment.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                uiCommunicationListener.hideSoftKeyboard()
                binding.maskLayout.visibility = View.VISIBLE
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.visibility = View.GONE
            }
        }

        binding.maskLayout.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
        }

        binding.imgBack.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
            activity?.onBackPressed()
        }

        binding.edtTypeMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.imgSendMessage.setOnClickListener {
            uiCommunicationListener.hideSoftKeyboard()
            uiCommunicationListener.hideSoftKeyboard()

            val dialog = Dialog(requireActivity(), android.R.style.Theme_Dialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.custom_success_dialog)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(resources.getColor(android.R.color.transparent))
            )
            dialog.show()

            val txtTitle = dialog.findViewById(R.id.tv_title) as TextView

            txtTitle.text = "Message sent successfully."

            Handler().postDelayed({
                dialog.dismiss()
                activity?.onBackPressed()
            }, 1000)

        }

        binding.photoContainer.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            val bottomUp: Animation = AnimationUtils.loadAnimation(
                context,
                R.anim.bottom_up
            )
            binding.attachmentContainer.startAnimation(bottomUp)
            binding.attachmentContainer.visibility = View.VISIBLE
            binding.attachmentVoiceContainer.visibility = View.GONE
            strFileType = "Photo"
            initViews()

        }

        binding.videoContainer.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            val bottomUp: Animation = AnimationUtils.loadAnimation(
                context,
                R.anim.bottom_up
            )
            binding.attachmentContainer.startAnimation(bottomUp)
            binding.attachmentContainer.visibility = View.VISIBLE
            binding.attachmentVoiceContainer.visibility = View.GONE
            strFileType = "Video"
            initViews()

        }

        binding.resourceContainer.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            findNavController().navigate(
                R.id.action_messageDetailFragment_to_resourceFragment
            )

        }

        binding.fileContainer.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            val mimeTypes = arrayOf(
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                "application/pdf",
                "application/mp4"
            )
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            var mimeTypesStr = ""
            for (mimeType in mimeTypes) {
                mimeTypesStr += "$mimeType|"
            }
            i.type = "*/*"
            if (mimeTypes.isNotEmpty()) {
                i.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
            startActivityForResult(i, 1000)
        }

        binding.voiceContainer.setOnClickListener {
            println("Requesting permission")
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.RECORD_AUDIO
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                println("Requesting permission")
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                    ), 0
                )
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.visibility = View.GONE
                val bottomUp: Animation = AnimationUtils.loadAnimation(
                    context,
                    R.anim.bottom_up
                )
                binding.attachmentVoiceContainer.startAnimation(bottomUp)
                binding.attachmentVoiceContainer.visibility = View.VISIBLE
                binding.attachmentContainer.visibility = View.GONE
                initRecorderUI()
            }
        }

        binding.tvRecord.setOnClickListener {
            if (binding.tvRecord.text.toString().equals("Record", ignoreCase = true)) {
                startRecording()
            } else {
                stopRecording()
            }

        }

    }

    private fun subscribeObservers() {


        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.messageResponse?.let {
                    Log.d("SAN", "messageList-->" + (it.messageList?.size ?: 0))
                    binding.rvMessageDetail.withModels {
                        var postion = 0
                        it.messageList?.forEach { msg ->
                            postion += 1
                            messageDetailEpoxyHolder {
                                id(postion)
                                messageType(msg.Type)
                                strMessage(if (msg.Message.isNullOrEmpty()) "" else msg.Message)
                                resourceList(if (msg.Resource.isNullOrEmpty()) emptyList() else msg.Resource)
                                strDateTime(msg.SendDate)
                                strIsRead(if (msg.isRead) "1" else "0")
                                listener(this@MessageDetailFragment)
                            }
                        }

                    }
                }
            }
        })


        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer {
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })


        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->
            Log.d("SAN", "SubjectListFragment-->viewModel.stateMessage")

            stateMessage?.let {

                uiCommunicationListener.onResponseReceived(
                    response = it.response,
                    stateMessageCallback = object : StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.clearStateMessage()
                        }
                    }
                )
            }
        })


    }

    fun initViews() {
        photoList.clear()
        albumList.clear()
        photoids.clear()
        if (isReadWritePermitted()) initGalleryViews() else checkReadWritePermission()
    }

    fun initGalleryViews() {
        Log.d("SAN", "initGalleryViews")
        glm = GridLayoutManager(ctx, 4)
        binding.imageGrid.itemAnimator = null
        val bundle = this.arguments
        if (bundle != null) photoids =
            if (bundle.containsKey("photoids")) bundle.getIntegerArrayList("photoids")!! else java.util.ArrayList()
        galleryOperation(0)
    }


    private fun initRecorderUI() {
        //Get the viewmodel factory
        val factory = InjectorUtils.provideRecorderViewModelFactory()

        //Getting the viewmodel
        viewModelRecorder = ViewModelProviders.of(this, factory).get(RecorderViewModel::class.java)

        addObserver()
    }

    private fun addObserver() {
        viewModelRecorder?.getRecordingTime()?.observe(requireActivity(), Observer {
            binding.tvRecordTime.text = it
        })
    }

    @SuppressLint("RestrictedApi")
    private fun startRecording() {
        viewModelRecorder?.startRecording()

        binding.tvRecord.background = resources.getDrawable(R.drawable.bg_grey_rounded)
        binding.tvRecord.setText("Cancel")
        binding.tvRecord.setTextColor(resources.getColor(R.color.orange))
    }

    @SuppressLint("RestrictedApi")
    private fun stopRecording() {
        viewModelRecorder?.stopRecording()

        binding.tvRecord.background = resources.getDrawable(R.drawable.bg_orange_rounded)
        binding.tvRecord.text = "Record"
        binding.tvRecord.setTextColor(resources.getColor(R.color.white))
        binding.imgSendMessage.alpha = 1.0f
        binding.imgSendMessage.isEnabled = true
    }

    override fun initRecyclerViews() {
        Log.d("SAN", "galleryOperation")
        if (strFileType.equals("Photo")) {
            binding.imageGrid.adapter = ImageGridAdapter(imageList = photoList, threshold = 0)
        } else {
            binding.imageGrid.adapter = VideoGridAdapter(imageList = photoList, threshold = 0)
        }
    }

    override fun galleryOperation(type: Int) {
        Log.d("SAN", "galleryOperation")
        doAsync {
            albumList = java.util.ArrayList()
            listener = object : OnPhoneImagesObtained {
                override fun onComplete(albums: ArrayList<GalleryAlbums>) {
                    albums.sortWith(compareBy { it.name })
                    for (album in albums) {
                        albumList.add(album)
                    }
                    albumList.add(0, GalleryAlbums(0, "All Videos", albumPhotos = photoList))
                    photoList.sortWith(compareByDescending { File(it.photoUri).lastModified() })

                    for (id in photoids) {
                        for (image in photoList) {
                            if (id == image.id) image.isSelected = true
                        }
                    }

                    RunOnUiThread(ctx).safely {
                        binding.imageGrid.layoutManager = glm
                        initRecyclerViews()
                    }
                }

                override fun onError() {
                    MLog.e("CURSOR", "FAILED")
                }
            }

            doAsync {
                getPhoneAlbums(ctx, listener, type)
            }
        }
    }

    override fun toggleDropdown() {
    }

    override fun getPhoneAlbums(
        context: Context,
        listener: OnPhoneImagesObtained,
        type: Int
    ) {
        Log.d("SAN", "getPhoneAlbums")
        if (strFileType.equals("Photo")) {
            imagePickerPresenter.getPhoneAlbums()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                videoPickerPresenter.getPhoneAlbums()
            }
        }
    }

    override fun updateTitle(galleryAlbums: GalleryAlbums) {
    }

    override fun updateSelectedPhotos(selectedlist: ArrayList<GalleryData>) {
        Log.d("SAN", "updateSelectedPhotos")
        for (selected in selectedlist) {
            for (photo in photoList) {
                photo.isSelected = selected.id == photo.id
                photo.isEnabled = selected.id == photo.id
            }
        }
        binding.imgSendMessage.alpha = 1.0f
        binding.imgSendMessage.isEnabled = true
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun checkReadWritePermission(): Boolean {
        Log.d("SAN", "checkReadWritePermission")
        requestPermissions(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), PERMISSIONS_READ_WRITE
        )
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d("SAN", "onRequestPermissionsResult")
        when (requestCode) {
            PERMISSIONS_READ_WRITE -> if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) initGalleryViews()
        }
    }

    private fun isReadWritePermitted(): Boolean =
        (context?.checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && context?.checkCallingOrSelfPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)

    override fun onResourceSelected(resource: Resource) {
        Log.d("SAN", "onResourceSelected")
        val bundle = Bundle()
        bundle.putString("title", resource.title)
        bundle.putString("url", resource.src)
        if (resource.src != null && resource.src.isNotEmpty()) {
            when {
                resource.contenttype.equals("av", true) -> {
                    bundle.putBoolean("isModality", false)
                    findNavController().navigate(
                        R.id.action_messageDetailFragment_to_videoPlayerFragment3,
                        bundle
                    )
                }
                resource.contenttype.equals("Image", true) -> {
                    findNavController().navigate(
                        R.id.action_messageDetailFragment_to_imageContentFragment3,
                        bundle
                    )
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("SAN", "onActivityResult-->")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1000) {
                Log.d("SAN", "onActivityResult-->MessageDetailFragment")

            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as DashboardActivity).bottom_navigation_view.visibility = View.GONE
    }

}
