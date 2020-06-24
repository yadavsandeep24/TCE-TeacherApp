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
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentProfileBinding
import com.tce.teacherapp.db.entity.Profile
import com.tce.teacherapp.ui.BaseFragment
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.home.DashboardViewModel
import com.tce.teacherapp.ui.dashboard.home.state.DashboardStateEvent
import com.tce.teacherapp.util.RequestTypes
import com.tce.teacherapp.util.StateMessageCallback
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
class ProfileFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseFragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private var PICK_PHOTO_FROM_GALLARY = 0
    val viewModel: DashboardViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
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

        (activity as DashboardActivity).setCustomToolbar(R.layout.parent_profile_header)
        (activity as DashboardActivity).expandAppBar(true)
        (activity as DashboardActivity).showHideUnderDevelopmentLabel(false)

        val tvBack =
            (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.tv_back)
        tvBack.visibility = View.VISIBLE

        val tvTopicTitle =
            (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.toolbar_title)

        val tvSave =
            (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.tvSave)
        tvSave.visibility = View.VISIBLE

        tvBack.setOnClickListener {
            activity?.onBackPressed()
        }


        tvSave.setOnClickListener {
            val profile = Profile(
                binding.edtName.tag.toString(),
                binding.edtName.text.toString(),
                binding.edtRelationChild.text.toString(),
                binding.edtEmergencyContact.text.toString(),
                binding.edtEmail.text.toString(),
                "",
                binding.edtAddress.text.toString(),
                "",
                "",
                fingerPrintMode = false,
                faceIdMode = false
            )
            viewModel.setStateEvent(DashboardStateEvent.UpdateProfile(profile))
        }
        tvTopicTitle.text = resources.getString(R.string.update_profile)
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
                    binding.edtName.tag = it.id
                    binding.edtName.setText(it.name)
                    binding.edtName.isEnabled = false
                    binding.edtEmergencyContact.setText(it.contact)
                    binding.edtEmail.setText(it.email)
                    binding.edtAddress.setText(it.address)
                    binding.edtRelationChild.setText(it.relationship)
                    Glide.with(this)
                        .load(it.imageUrl)
                        .placeholder(R.drawable.ic_user_profile)
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                        .into(binding.imgProfile)
                }
            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer {
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })


        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->
            Log.d("SAN", "LoginFragment-->viewModel.stateMessage")

            stateMessage?.let {
                when(stateMessage.response.serviceTypes){

                    RequestTypes.GENERIC ->{
                        findNavController().navigate(R.id.action_profileFragment_to_settingsFragment)
                    }

                    else -> {

                    }
                }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
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
            }

        } else if (requestCode == UCrop.REQUEST_CROP) {
            handleCropResult(data!!)
        }
    }


    private suspend fun compressAndCopyImage(selectedImagePath: String): String {
        val compressedImageFile = Compressor.compress(requireActivity(), File(selectedImagePath)) {
            quality(100)
            format(Bitmap.CompressFormat.PNG)
        }
        val imageName = requireActivity().getExternalFilesDir(null)
            .toString() + File.separator +
                ".profilepic" + File.separator + "tceuser" + ".PNG"
        val isImageCopied =
            Utility.copyFileFromSourceToDestn(compressedImageFile.path, imageName, false)
        return if (isImageCopied) {
            imageName
        } else {
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
            .start(activity as DashboardActivity, this, UCrop.REQUEST_CROP)
    }


    override fun onDestroy() {
        super.onDestroy()
    }


}