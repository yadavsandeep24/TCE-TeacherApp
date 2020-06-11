package com.tce.teacherapp.ui.dashboard.home.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentProfileBinding
import com.tce.teacherapp.databinding.FragmentTeacherProfileBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.util.Utility
import com.yalantis.ucrop.UCrop
import id.zelory.compressor.Compressor
import java.io.File


class TeacherProfileFragment : Fragment() {


    private lateinit var binding : FragmentTeacherProfileBinding
    internal var PICK_PHOTO_FROM_GALLARY = 0
    private var mIsMideaStoreEnabled: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        val i = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        mIsMideaStoreEnabled = activity?.packageManager?.let { i.resolveActivity(it) } != null

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


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_PHOTO_FROM_GALLARY ) {
                var selectedImagePath: String? = null
                var selectedImageUri: Uri? = null
                if (data!!.data == null) {
                    val path = MediaStore.Images.Media.insertImage(requireActivity().contentResolver, data.getExtras()!!.get("data") as Bitmap, "Title", null)
                    if (!TextUtils.isEmpty(path)) {
                        selectedImageUri = Uri.parse(path)
                    }
                } else {
                    selectedImageUri = data.data

                }
                if (selectedImageUri != null) {
                    selectedImagePath = Utility.getRealPathFromURI(requireActivity(), selectedImageUri!!)
                    val compressedImageFile = Compressor.getDefault(requireActivity()).compressToFile(
                        File(selectedImagePath!!)
                    )
                    val imageName =requireActivity().getExternalFilesDir(null).toString() + File.separator +
                            ".profilepic" + File.separator + "profile-"+ Utility.getUniqueID("user") + ".PNG"
                    val isImageCopied = Utility.copyFileFromSourceToDestn(compressedImageFile.getPath(), imageName, false)
                    if (isImageCopied) {
                        callCrop(Uri.fromFile(File(imageName)))
                    }
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