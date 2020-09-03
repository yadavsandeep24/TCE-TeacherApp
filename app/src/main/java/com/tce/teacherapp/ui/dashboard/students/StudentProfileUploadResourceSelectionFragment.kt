package com.tce.teacherapp.ui.dashboard.students

import android.Manifest
import android.annotation.TargetApi
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentStudentProfileUploadResouceSelectionBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.util.gallerypicker.model.GalleryAlbums
import com.tce.teacherapp.util.gallerypicker.model.GalleryData
import com.tce.teacherapp.util.gallerypicker.presenter.StudentProfilePhotosPresenterImpl
import com.tce.teacherapp.util.gallerypicker.utils.MLog
import com.tce.teacherapp.util.gallerypicker.utils.RunOnUiThread
import com.tce.teacherapp.util.gallerypicker.view.ImagePickerContract
import com.tce.teacherapp.util.gallerypicker.view.OnPhoneImagesObtained
import com.tce.teacherapp.util.gallerypicker.view.adapters.ImageGridAdapter
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.jetbrains.anko.doAsync
import java.io.File
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class StudentProfileUploadResourceSelectionFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_student_profile_upload_resouce_selection, viewModelFactory),
    ImagePickerContract {

    private lateinit var binding: FragmentStudentProfileUploadResouceSelectionBinding
    var photoList: java.util.ArrayList<GalleryData> = java.util.ArrayList()
    var albumList: java.util.ArrayList<GalleryAlbums> = java.util.ArrayList()
    var photoids: java.util.ArrayList<Int> = java.util.ArrayList()
    val imagePickerPresenter: StudentProfilePhotosPresenterImpl = StudentProfilePhotosPresenterImpl(this)
    private val PERMISSIONS_READ_WRITE = 123
    lateinit var glm: GridLayoutManager
    lateinit var listener: OnPhoneImagesObtained
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentProfileUploadResouceSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as DashboardActivity).expandAppBar(true)
        (activity as DashboardActivity).setCustomToolbar(R.layout.student_profile_upload_header)
        initViews(0)
        val tvDone = ((activity as DashboardActivity).binding.toolBar.findViewById(R.id.tvDone) as TextView)
        tvDone.setOnClickListener {
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
        txtTitle.text = "3 Files Added!"

        Handler().postDelayed({
            activity?.onBackPressed()
            dialog.dismiss()
        }, 1000)
        }
        val tvBack = (activity as DashboardActivity).binding.toolBar.findViewById<AppCompatImageView>(R.id.imgBack)
        tvBack.setOnClickListener {
            activity?.onBackPressed()
        }
        val bottomSheetBehaviorFilterContainer = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(binding.bottomSheetFilterBy)

        bottomSheetBehaviorFilterContainer.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehaviorFilterContainer.skipCollapsed = true
        bottomSheetBehaviorFilterContainer.isDraggable = false

        bottomSheetBehaviorFilterContainer.addBottomSheetCallback(object : com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehaviorFilterContainer.state = BottomSheetBehavior.STATE_EXPANDED
                }

            }
        })
        val filterContainer = (activity as DashboardActivity).binding.toolBar.findViewById<LinearLayout>(R.id.ll_filter)
        filterContainer.setOnClickListener {
            if (bottomSheetBehaviorFilterContainer.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehaviorFilterContainer.state =
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.dim_color_dashboard))
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.INVISIBLE
            } else {
                bottomSheetBehaviorFilterContainer.state =
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
                (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            }
        }

        binding.tvPhotos.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
            (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            initViews(1)
        }

        binding.tvVideos.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
            (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            initViews(2)
        }

        binding.tvShowAll.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
            (activity as DashboardActivity).bottom_navigation_view.visibility = View.VISIBLE
            initViews(0)
        }

    }
    fun initViews(type: Int) {
        photoList.clear()
        albumList.clear()
        photoids.clear()
        if (isReadWritePermitted()) initGalleryViews(type) else checkReadWritePermission()
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun checkReadWritePermission(): Boolean {
        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_READ_WRITE)
        return true
    }
    fun initGalleryViews(type: Int) {
        glm = GridLayoutManager(requireContext(), 4)
        binding.imageGrid.itemAnimator = null
        val bundle = this.arguments
        if (bundle != null) photoids = if (bundle.containsKey("photoids")) bundle.getIntegerArrayList("photoids")!! else java.util.ArrayList()
        galleryOperation(type)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_READ_WRITE -> if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED) initGalleryViews(0)
        }
    }

    override fun initRecyclerViews() {
            binding.imageGrid.adapter = ImageGridAdapter(imageList = photoList, threshold = 0)
    }

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

                    RunOnUiThread(requireContext()).safely {
                        binding.imageGrid.layoutManager = glm
                        initRecyclerViews()
                    }
                }

                override fun onError() {
                    MLog.e("CURSOR", "FAILED")
                }
            }

            doAsync {
                getPhoneAlbums(requireContext(), listener,type)
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
        when (type) {
            0 -> {
                imagePickerPresenter.getPhoneAlbums()
            }
            1 -> {
                imagePickerPresenter.getPhonePhotos()
            }
            2 -> {
                imagePickerPresenter.getPhoneVideos()
            }
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
    }

    private fun isReadWritePermitted(): Boolean = (context?.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && context?.checkCallingOrSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

}