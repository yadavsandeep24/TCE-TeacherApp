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
import com.tce.teacherapp.ui.login.LoginViewModel
import com.tce.teacherapp.util.Utility
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
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
    private var mIsMideaStoreEnabled: Boolean = false


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

       /* val i = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        mIsMideaStoreEnabled = activity?.packageManager?.let { i.resolveActivity(it) } != null*/

        binding.profileImageContainer.setOnClickListener(View.OnClickListener {
            val i = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(i, PICK_PHOTO_FROM_GALLARY)
        })

        binding.imgBack.setOnClickListener(View.OnClickListener {
            activity?.onBackPressed()
        })

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
                }
            }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_PHOTO_FROM_GALLARY ) {
                var selectedImagePath: String? = null
                var selectedImageUri: Uri? = null
                if (data!!.data == null) {
                    val path = MediaStore.Images.Media.insertImage(requireActivity().contentResolver, data.extras!!.get("data") as Bitmap, "Title", null)
                    if (!TextUtils.isEmpty(path)) {
                        selectedImageUri = Uri.parse(path)
                    }
                } else {
                    selectedImageUri = data.data

                }
                if (selectedImageUri != null) {
                    selectedImagePath = Utility.getRealPathFromURI(requireActivity(), selectedImageUri)


              /*      val compressedImageFile = Compressor.compress(requireActivity(),
                        File(selectedImagePath)
                    )
                    val imageName =requireActivity().getExternalFilesDir(null).toString() + File.separator +
                            ".profilepic" + File.separator + "profile-"+ Utility.getUniqueID("user") + ".PNG"
                    val isImageCopied = Utility.copyFileFromSourceToDestn(compressedImageFile.path, imageName, false)
                    if (isImageCopied) {
                        callCrop(Uri.fromFile(File(imageName)))
                    }*/
                }

            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data!!)
            }
        }
    }

    private fun handleCropResult(result: Intent) {
        val resultUri = UCrop.getOutput(result)
        if (resultUri != null) {
            Glide.with(this)
                .load(resultUri!!.toString())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .into(binding.imgProfile)
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