package com.tce.teacherapp.ui.dashboard.home.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentTeacherProfileBinding
import com.tce.teacherapp.ui.BaseFragment
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.home.DashboardViewModel
import com.tce.teacherapp.ui.dashboard.home.state.DASHBOARD_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.home.state.DashboardStateEvent
import com.tce.teacherapp.ui.dashboard.home.state.DashboardViewState
import com.tce.teacherapp.util.Utility
import com.yalantis.ucrop.UCrop
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import kotlinx.coroutines.*
import java.io.File
import javax.inject.Inject


@ExperimentalCoroutinesApi
@FlowPreview
class TeacherProfileFragment
@Inject
constructor(viewModelFactory: ViewModelProvider.Factory
) : BaseFragment(R.layout.fragment_teacher_profile) {

    val viewModel: DashboardViewModel by viewModels {
        viewModelFactory
    }
    private lateinit var binding : FragmentTeacherProfileBinding
    private var PICK_PHOTO_FROM_GALLARY = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { inState ->
            Log.d(TAG, "DashboardViewState: inState is NOT null")
            (inState[DASHBOARD_VIEW_STATE_BUNDLE_KEY] as DashboardViewState?)?.let { viewState ->
                Log.d(TAG, "DashboardViewState: restoring view state: $viewState")
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val viewState = viewModel.viewState.value

        //clear the list. Don't want to save a large list to bundle.
        viewState?.profile = null


        outState.putParcelable(
            DASHBOARD_VIEW_STATE_BUNDLE_KEY,
            viewState
        )
        super.onSaveInstanceState(outState)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTeacherProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiCommunicationListener.isStoragePermissionGranted()
        binding.profileImageContainer.setOnClickListener {
            val i = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(i, PICK_PHOTO_FROM_GALLARY)
        }
        (activity as DashboardActivity).setCustomToolbar(R.layout.subject_list_top_bar)
        (activity as DashboardActivity).expandAppBar(true)
        (activity as DashboardActivity).showHideUnderDevelopmentLabel(false)
        val topBar = (activity as DashboardActivity).binding.toolBar.findViewById<RelativeLayout>(R.id.top_container)
        topBar.setBackgroundColor(resources.getColor(R.color.setting_actionbar_color))

        val spnDivision =  (activity as DashboardActivity).binding.toolBar.findViewById<AppCompatSpinner>(R.id.spn_division)
        spnDivision.visibility = View.GONE

        val tvBack = (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.tv_back)
        tvBack.visibility = View.VISIBLE

        val tvTopicTitle = (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.toolbar_title)

        tvBack.setOnClickListener {
            activity?.onBackPressed()
        }
        tvTopicTitle.text = resources.getString(R.string.title_update_profile)
        viewModel.setStateEvent(DashboardStateEvent.GetProfileEvent)
        subscribeObservers()
    }

    override fun setupChannel() {
        viewModel.setupChannel()
    }

    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {
                viewState.profile?.let {
                    binding.tvUserName.text = it.name
                    binding.tvContact.text = it.contact
                    binding.tvEmail.text = it.email
                    binding.tvAddress.text = it.address
                    binding.tvSubject.text = it.subjects
                   Glide.with(this)
                       .load(it.imageUrl)
                       .placeholder(R.drawable.ic_user_profile)
                           .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                           .into(binding.imgProfile)
                }
            }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("SAN", "TeacherProfileFragment-->onActivityResult-->$requestCode")
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_PHOTO_FROM_GALLARY) {
                var selectedImagePath: String? = null
                var selectedImageUri: Uri? = null
                if (data!!.data == null) {
                    val path = MediaStore.Images.Media.insertImage(
                        requireActivity().contentResolver,
                        data.extras!!.get("data") as Bitmap,
                        "Title",
                        null
                    )
                    if (!TextUtils.isEmpty(path)) {
                        selectedImageUri = Uri.parse(path)
                    }
                } else {
                    selectedImageUri = data.data

                }
                if (selectedImageUri != null) {
                    selectedImagePath =
                        Utility.getRealPathFromURI(requireActivity(), selectedImageUri)
                    GlobalScope.launch(Dispatchers.Main) {
                        val imageCopied = async(Dispatchers.IO) {
                            compressAndCopyImage(selectedImagePath)
                        }
                        callCrop(Uri.fromFile(File(imageCopied.await()))) // back on UI thread
                    }
                }

            }else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data!!)
            }
        }
    }
    private fun compressAndCopyImage(selectedImagePath :String): String {
        val imageName = requireActivity().getExternalFilesDir(null)
            .toString() + File.separator +
                ".profilepic" + File.separator + "tceuser"+File.separator+ Utility.getUniqueID("prof")+ ".PNG"
        val isImageCopied = Utility.copyFileFromSourceToDestn(selectedImagePath, imageName, false)
        return if(isImageCopied) {
            imageName
        }else{
            ""
        }
    }
    private fun handleCropResult(result: Intent) {
        val resultUri = UCrop.getOutput(result)
        Log.d("SAN", "resultUri-->$resultUri")
        if (resultUri != null) {
            Glide.with(this)
                .load(resultUri.toString())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(binding.imgProfile)
            viewModel.setStateEvent(DashboardStateEvent.UpdateProfilePic(resultUri.toString()))
        }
    }

    private fun callCrop(sourceImage: Uri) {
        val options = UCrop.Options()
        options.setCompressionFormat(Bitmap.CompressFormat.PNG)
        options.setFreeStyleCropEnabled(true)
        options.setCircleDimmedLayer(true)
        UCrop.of(sourceImage, sourceImage)
            .withAspectRatio(1F, 1F)
            .withMaxResultSize(768, 1024)
            .withOptions(options)
            .start(activity as DashboardActivity, this, UCrop.REQUEST_CROP )
    }

    override fun onDestroy() {
        super.onDestroy()
    }



}