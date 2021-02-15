package com.tce.teacherapp.ui.dashboard.messages

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gabriel.soundrecorder.recorder.RecorderViewModel
import com.example.gabriel.soundrecorder.util.InjectorUtils
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.databinding.FragmentGroupChatBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.util.gallerypicker.model.GalleryAlbums
import com.tce.teacherapp.util.gallerypicker.model.GalleryData
import com.tce.teacherapp.util.gallerypicker.presenter.PhotosPresenterImpl
import com.tce.teacherapp.util.gallerypicker.presenter.VideosPresenterImpl
import com.tce.teacherapp.util.gallerypicker.utils.MLog
import com.tce.teacherapp.util.gallerypicker.utils.RunOnUiThread
import com.tce.teacherapp.util.gallerypicker.view.ImagePickerContract
import com.tce.teacherapp.util.gallerypicker.view.OnPhoneImagesObtained
import com.tce.teacherapp.util.gallerypicker.view.adapters.ImageGridAdapter
import com.tce.teacherapp.util.gallerypicker.view.adapters.VideoGridAdapter
import com.tce.teacherapp.util.sheets.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.jetbrains.anko.doAsync
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class GroupChatFragment @Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseMessageFragment(R.layout.fragment_group_chat, viewModelFactory), ImagePickerContract {

    private lateinit var binding: FragmentGroupChatBinding
    var PICK_FILE_FROM_GALLARY: Int = 100

    var photoList: java.util.ArrayList<GalleryData> = java.util.ArrayList()
    var albumList: java.util.ArrayList<GalleryAlbums> = java.util.ArrayList()
    lateinit var glm: GridLayoutManager
    var photoids: java.util.ArrayList<Int> = java.util.ArrayList()
    val imagePickerPresenter: PhotosPresenterImpl = PhotosPresenterImpl(this)
    val videoPickerPresenter: VideosPresenterImpl = VideosPresenterImpl(this)
    lateinit var listener: OnPhoneImagesObtained
    private val PERMISSIONS_READ_WRITE = 123

    lateinit var ctx: Context

    private var strFileType: String = "Photo"

    private var viewModelRecorder: RecorderViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupChatBinding.inflate(inflater, container, false)
        ctx = inflater.context
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun initViews() {
        photoList.clear()
        albumList.clear()
        photoids.clear()
        if (isReadWritePermitted()) initGalleryViews() else checkReadWritePermission()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun initGalleryViews() {
        glm = GridLayoutManager(ctx, 4)
        binding.imageGrid.itemAnimator = null
        val bundle = this.arguments
        if (bundle != null) photoids =
            if (bundle.containsKey("photoids")) bundle.getIntegerArrayList("photoids")!! else java.util.ArrayList()
        galleryOperation(0)
    }

    override fun initRecyclerViews() {
        if (strFileType.equals("Photo")) {
            binding.imageGrid.adapter = ImageGridAdapter(imageList = photoList, threshold = 0)
        } else {
            binding.imageGrid.adapter = VideoGridAdapter(imageList = photoList, threshold = 0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun galleryOperation(type: Int) {
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun getPhoneAlbums(
        context: Context,
        listener: OnPhoneImagesObtained,
        type: Int
    ) {
        if (strFileType.equals("Photo")) {
            imagePickerPresenter.getPhoneAlbums()
        } else {
            videoPickerPresenter.getPhoneAlbums()
        }
    }

    override fun updateTitle(galleryAlbums: GalleryAlbums) {
    }

    override fun updateSelectedPhotos(selectedlist: ArrayList<GalleryData>) {
        for (selected in selectedlist) {
            for (photo in photoList) {
                photo.isSelected = selected.id == photo.id
                photo.isEnabled = selected.id == photo.id
            }
        }
        binding.imgSendMessage.alpha = 1.0f
        binding.imgSendMessage.isEnabled = true
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        // (activity as DashboardActivity).setCustomToolbar(R.layout.subject_list_top_bar)
        (activity as DashboardActivity).expandAppBar(false)
        val studentList: ArrayList<StudentListResponseItem>? =
            arguments?.getParcelableArrayList("studentList")
        if (studentList != null && studentList.size > 0) {
            var studentNames = ""
            for (student in studentList) {
                studentNames = studentNames + ", " + student.Name
            }

            if (!TextUtils.isEmpty(studentNames)) {
                studentNames = studentNames.replaceFirst(",", "").trim()
            }

            binding.tvTitle1.text = studentNames
            binding.tvSubTitle1.text = studentList.size.toString() + " Members"
        } else {
            binding.tvTitle1.text = "Class Apple"
            binding.tvSubTitle1.text = "30 Members"
        }
        val formattedString: String = SimpleDateFormat("dd MMM yyyy").format(Date())
        binding.tvDate.text = formattedString


        if (resources.getString(R.string.app_type)
                .equals(resources.getString(R.string.app_type_parent),true)) {
            binding.resourceContainer.visibility = GONE
            binding.divider1.visibility = GONE
        }
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.isDraggable = false

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
                }

            }
        })

        binding.imgAttachment.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                uiCommunicationListener.hideSoftKeyboard()
                binding.maskLayout.visibility = VISIBLE
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.visibility = GONE
            }
        }

        binding.maskLayout.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = GONE
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
                findNavController().navigate(R.id.action_groupChatFragment_to_messageListFragment)
            }, 1000)

        }

        binding.photoContainer.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = GONE
            val bottomUp: Animation = AnimationUtils.loadAnimation(
                context,
                R.anim.bottom_up
            )
            binding.attachmentContainer.startAnimation(bottomUp)
            binding.attachmentContainer.visibility = View.VISIBLE
            binding.attachmentVoiceContainer.visibility = GONE
            strFileType = "Photo"
            initViews()

        }

        binding.videoContainer.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = GONE
            val bottomUp: Animation = AnimationUtils.loadAnimation(
                context,
                R.anim.bottom_up
            )
            binding.attachmentContainer.startAnimation(bottomUp)
            binding.attachmentContainer.visibility = View.VISIBLE
            binding.attachmentVoiceContainer.visibility = GONE
            strFileType = "Video"
            initViews()

        }

        binding.resourceContainer.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = GONE
            val flowType = arguments?.getInt("flowType")
            if (flowType == 1) {
                findNavController().navigate(
                    R.id.action_groupChatFragment2_to_resourceFragment2
                )
            } else {
                findNavController().navigate(
                    R.id.action_groupChatFragment_to_resourceFragment
                )
            }
        }

        binding.fileContainer.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = GONE
            val mimeTypes = arrayOf(
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                "application/pdf",
                "application/mp4"
            )
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            var mimeTypesStr = ""
            for (mimeType in mimeTypes) {
                mimeTypesStr += "$mimeType|"
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                intent.type = "*/*"
                if (mimeTypes.size > 0) {
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
                }
            } else {
                intent.type = "*/*"
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
            startActivityForResult(Intent.createChooser(intent, "Select File"), 1000)

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
                binding.maskLayout.visibility = GONE
                val bottomUp: Animation = AnimationUtils.loadAnimation(
                    context,
                    R.anim.bottom_up
                )
                binding.attachmentVoiceContainer.startAnimation(bottomUp)
                binding.attachmentVoiceContainer.visibility = View.VISIBLE
                binding.attachmentContainer.visibility = GONE
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
        binding.tvRecord.setText("Record")
        binding.tvRecord.setTextColor(resources.getColor(R.color.white))
        binding.imgSendMessage.alpha = 1.0f
        binding.imgSendMessage.isEnabled = true
    }

    @TargetApi(Build.VERSION_CODES.N)
    @SuppressLint("RestrictedApi")
    private fun pauseRecording() {
        viewModelRecorder?.pauseRecording()
    }

    @TargetApi(Build.VERSION_CODES.N)
    @SuppressLint("RestrictedApi")
    private fun resumeRecording() {
        viewModelRecorder?.resumeRecording()
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun checkReadWritePermission(): Boolean {
        requestPermissions(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ), PERMISSIONS_READ_WRITE
        )
        return true
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_READ_WRITE -> if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) initGalleryViews()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as DashboardActivity).bottom_navigation_view.visibility = GONE
    }

    private fun isReadWritePermitted(): Boolean =
        (context?.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && context?.checkCallingOrSelfPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.imgSendMessage.alpha = 1.0f
        binding.imgSendMessage.isEnabled = true


    }

}
