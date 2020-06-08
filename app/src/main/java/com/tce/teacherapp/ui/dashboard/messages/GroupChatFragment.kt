package com.tce.teacherapp.ui.dashboard.messages

import android.Manifest
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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.picker.gallery.model.GalleryAlbums
import com.picker.gallery.model.GalleryData
import com.picker.gallery.presenter.PhotosPresenterImpl
import com.picker.gallery.presenter.VideosPresenterImpl
import com.picker.gallery.utils.MLog
import com.picker.gallery.utils.RunOnUiThread
import com.picker.gallery.utils.font.FontsConstants
import com.picker.gallery.utils.font.FontsManager
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
            strFileType = "Video"
            initViews()

        })

        binding.resourceContainer.setOnClickListener(View.OnClickListener {
            findNavController().navigate(
                R.id.action_groupChatFragment_to_resourceFragment
            )
        })

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



}
