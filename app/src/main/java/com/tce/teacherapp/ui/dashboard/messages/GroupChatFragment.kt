package com.tce.teacherapp.ui.dashboard.messages

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gabriel.soundrecorder.recorder.RecorderViewModel
import com.example.gabriel.soundrecorder.util.InjectorUtils
import com.picker.gallery.model.GalleryAlbums
import com.picker.gallery.model.GalleryData
import com.picker.gallery.presenter.PhotosPresenterImpl
import com.picker.gallery.presenter.VideosPresenterImpl
import com.picker.gallery.utils.MLog
import com.picker.gallery.utils.RunOnUiThread
import com.picker.gallery.view.ImagePickerContract
import com.picker.gallery.view.OnPhoneImagesObtained
import com.picker.gallery.view.adapters.AlbumAdapter
import com.picker.gallery.view.adapters.ImageGridAdapter
import com.picker.gallery.view.adapters.VideoGridAdapter
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentGroupChatBinding
import com.tce.teacherapp.db.entity.Student
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_group_chat.*
import org.jetbrains.anko.doAsync
import java.io.File


class GroupChatFragment : Fragment(), ImagePickerContract {

   private lateinit var binding : FragmentGroupChatBinding
    private lateinit var studentList : ArrayList<Student>
     var PICK_FILE_FROM_GALLARY : Int = 100

    var photoList: java.util.ArrayList<GalleryData> = java.util.ArrayList()
    var albumList: java.util.ArrayList<GalleryAlbums> = java.util.ArrayList()
    lateinit var glm: GridLayoutManager
    var photoids: java.util.ArrayList<Int> = java.util.ArrayList()
    val imagePickerPresenter: PhotosPresenterImpl = PhotosPresenterImpl(this)
    val videoPickerPresenter: VideosPresenterImpl = VideosPresenterImpl(this)
    lateinit var listener: OnPhoneImagesObtained
    private val PERMISSIONS_READ_WRITE = 123

    lateinit var ctx: Context

    private var strFileType : String = "Photo"

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

    fun initViews() {
        photoList.clear()
        albumList.clear()
        photoids.clear()
        if (isReadWritePermitted()) initGalleryViews() else checkReadWritePermission()
    }

    fun initGalleryViews() {
        glm = GridLayoutManager(ctx, 4)
        binding.imageGrid.itemAnimator = null
        val bundle = this.arguments
        if (bundle != null) photoids = if (bundle.containsKey("photoids")) bundle.getIntegerArrayList("photoids")!! else java.util.ArrayList()
        galleryOperation()
    }

    override fun initRecyclerViews() {
        binding.albumsrecyclerview.layoutManager = LinearLayoutManager(ctx)
        binding.albumsrecyclerview.adapter = AlbumAdapter(java.util.ArrayList(), this)
        if(strFileType.equals("Photo")) {
            binding.imageGrid.adapter = ImageGridAdapter(imageList = photoList, threshold = 0)
        }else{
            binding.imageGrid.adapter = VideoGridAdapter(imageList = photoList, threshold = 0)
        }
    }

    override fun galleryOperation() {
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
                getPhoneAlbums(ctx, listener)
            }
            }
    }

    override fun toggleDropdown() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPhoneAlbums(context: Context, listener: OnPhoneImagesObtained) {
        if(strFileType.equals("Photo")) {
            imagePickerPresenter.getPhoneAlbums()
        }else{
            videoPickerPresenter.getPhoneAlbums()
        }
    }

    override fun updateTitle(galleryAlbums: GalleryAlbums) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateSelectedPhotos(selectedlist: ArrayList<GalleryData>) {
        for (selected in selectedlist) {
            for (photo in photoList) {
                photo.isSelected = selected.id == photo.id
                photo.isEnabled = selected.id == photo.id
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        studentList = arguments?.getParcelableArrayList<Student>("studentList") as  ArrayList<Student>


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        // (activity as DashboardActivity).setCustomToolbar(R.layout.subject_list_top_bar)
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).showHideUnderDevelopmentLabel(false)

        var studentNames = ""
        for (student in studentList){
          studentNames = studentNames + ", " + student.name
        }

        if(!TextUtils.isEmpty(studentNames)){
          studentNames = studentNames.replaceFirst(",","")
        }

        binding.tvTitle1.setText(studentNames)
        binding.tvSubTitle1.setText(studentList.size.toString() + " Members")

        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.skipCollapsed = true

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                val rotation = when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> 0f
                    BottomSheetBehavior.STATE_COLLAPSED -> 180f
                    BottomSheetBehavior.STATE_HIDDEN -> 180f
                    else -> return
                }

            }
        })

        binding.imgAttachment.setOnClickListener(View.OnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        })

        binding.mainContainer.setOnClickListener(View.OnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

        })

        binding.imgBack.setOnClickListener(View.OnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
            activity?.onBackPressed()
        })

        binding.photoContainer.setOnClickListener(View.OnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val bottomUp: Animation = AnimationUtils.loadAnimation(
                context,
                R.anim.bottom_up
            )
            binding.attachmentContainer.startAnimation(bottomUp)
            binding.attachmentContainer.visibility = View.VISIBLE
            binding.attachmentVoiceContainer.visibility = View.GONE
            strFileType = "Photo"
            initViews()

        })

        binding.videoContainer.setOnClickListener(View.OnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val bottomUp: Animation = AnimationUtils.loadAnimation(
                context,
                R.anim.bottom_up
            )
            binding.attachmentContainer.startAnimation(bottomUp)
            binding.attachmentContainer.visibility = View.VISIBLE
            binding.attachmentVoiceContainer.visibility = View.GONE
            strFileType = "Video"
            initViews()

        })

        binding.resourceContainer.setOnClickListener(View.OnClickListener {
            findNavController().navigate(
                R.id.action_groupChatFragment_to_resourceFragment
            )
        })

        binding.fileContainer.setOnClickListener(View.OnClickListener {

            val mimeTypes = arrayOf("application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                "application/pdf", "application/mp4")
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

        })

        binding.voiceContainer.setOnClickListener(View.OnClickListener {
            println("Requesting permission")
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                println("Requesting permission")
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO), 0)
            }else{
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                val bottomUp: Animation = AnimationUtils.loadAnimation(
                    context,
                    R.anim.bottom_up
                )
                binding.attachmentVoiceContainer.startAnimation(bottomUp)
                binding.attachmentVoiceContainer.visibility = View.VISIBLE
                binding.attachmentContainer.visibility = View.GONE
                initRecorderUI()
            }
        })

        binding.tvRecord.setOnClickListener(View.OnClickListener {
            if(binding.tvRecord.text.toString().equals("Record", ignoreCase = true)){
                startRecording()
            }else{
                stopRecording()
            }

        })


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
    private fun stopRecording(){
        viewModelRecorder?.stopRecording()

        binding.tvRecord.background = resources.getDrawable(R.drawable.bg_orange_rounded)
        binding.tvRecord.setText("Record")
        binding.tvRecord.setTextColor(resources.getColor(R.color.white))
    }

    @TargetApi(Build.VERSION_CODES.N)
    @SuppressLint("RestrictedApi")
    private fun pauseRecording(){
        viewModelRecorder?.pauseRecording()
    }

    @TargetApi(Build.VERSION_CODES.N)
    @SuppressLint("RestrictedApi")
    private fun resumeRecording(){
        viewModelRecorder?.resumeRecording()
    }



    @TargetApi(android.os.Build.VERSION_CODES.JELLY_BEAN)
    fun checkReadWritePermission(): Boolean {
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_READ_WRITE)
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_READ_WRITE -> if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) initGalleryViews()
        }
    }

    private fun isReadWritePermitted(): Boolean = (context?.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && context?.checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

      /*  if (resultCode == RESULT_OK) {
            val selectedImageUri: Uri?
            if (data!!.data == null) {
                val path = MediaStore.Images.Media.insertImage(
                    requireActivity().contentResolver,
                    data.extras!!.get("data") as Bitmap,
                    "Title",
                    null
                )
                selectedImageUri = Uri.parse(path)
            } else {
                selectedImageUri = data.data
            }
            var selectedPath: String? = null
            if (selectedImageUri != null) {
                if (requestCode == PICK_FILE_FROM_GALLARY) {
                    selectedPath = Utility.getRealPathFromURI(requireActivity(), selectedImageUri)
                    if (!TextUtils.isEmpty(selectedPath)) {
                        if (!StringUtils.containsIgnoreCase(selectedPath, ".png")
                            && !StringUtils.containsIgnoreCase(selectedPath, ".jpg")
                            && !StringUtils.containsIgnoreCase(selectedPath, ".bmp")
                            && !StringUtils.containsIgnoreCase(selectedPath, ".jpeg")
                        ) {
                           *//* Utility.showToast(
                                activity!!,
                                getString(com.zl.baseapp.R.string.invalid_file_images),
                                Toast.LENGTH_LONG,
                                Gravity.CENTER
                            )*//*
                            selectedPath = null
                        }
                    }
                } else {
                    try {
                        selectedPath = Utility.getFilePath(requireActivity(), selectedImageUri)
                    } catch (e: URISyntaxException) {
                        e.printStackTrace()
                    }

                    if (!TextUtils.isEmpty(selectedPath)) {
                        if (!StringUtils.containsIgnoreCase(selectedPath, ".doc")
                            && !StringUtils.containsIgnoreCase(selectedPath, ".docx")
                            && !StringUtils.containsIgnoreCase(selectedPath, ".pdf")
                            && !StringUtils.containsIgnoreCase(selectedPath, ".mp4")
                        ) {
                           *//* Utility.showToast(
                                requireActivity(),
                                "invalid file",
                                Toast.LENGTH_LONG,
                                Gravity.CENTER
                            )*//*
                            selectedPath = null
                        }
                    }
                }
                if (!TextUtils.isEmpty(selectedPath)) {
                    val file = File(selectedPath!!)
                    val size = file.length() / 1024
                    val file_size =
                        Integer.parseInt(size.toString()) // Dividing by 1024 converts the size from bytes to kilobytes.
                    if (StringUtils.containsIgnoreCase(
                            selectedPath,
                            ".mp4"
                        ) && file_size > 5 * 1000
                    ) {
                        *//*Utility.showToast(
                            activity!!, String.format(
                                getString(com.zl.baseapp.R.string.msg_video_file_size_exceed),
                                resources.getInteger(com.zl.baseapp.R.integer.message_video_size_limitation)
                                    .toString()
                            ), Toast.LENGTH_LONG, Gravity.CENTER
                        )*//*
                    } else if (!StringUtils.containsIgnoreCase(
                            selectedPath,
                            ".mp4"
                        ) && file_size > 5 * 1000
                    ) { // 5 mb = 5000 kb
                       *//* Utility.showToast(
                            activity!!, String.format(
                                getString(com.zl.baseapp.R.string.msg_comman_file_size_exceed),
                                resources.getInteger(com.zl.baseapp.R.integer.message_any_file_type_size_limitation)
                                    .toString()
                            ), Toast.LENGTH_LONG, Gravity.CENTER
                        )*//*
                    } else {
                        val endIndex = selectedPath.lastIndexOf("/")
                        if (endIndex != -1) {
                            val pdfName = selectedPath.substring(endIndex + 1, selectedPath.length)
                         *//*   addAttachmentBox(
                                pdfName,
                                generateReplyAttachmentJson(selectedPath),
                                selectedImageUri
                            )
                            mTvMsgSendIcon.isEnabled = true*//*
                        }
                    }
                }
            }
        }*/

    }

}
