package com.tce.teacherapp.ui.dashboard.home.profile

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.*
import android.widget.TextView
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentProfileBinding
import com.tce.teacherapp.util.Utility
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    internal var PICK_PHOTO_FROM_GALLARY = 0
    internal var PICK_PHOTO_FROM_CAMERA = 1
    private var mIsMideaStoreEnabled: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val i = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        if (activity?.packageManager?.let { i.resolveActivity(it) } != null) {
            mIsMideaStoreEnabled = true
        } else {
            mIsMideaStoreEnabled = false

        }

        binding.profileImageContainer.setOnClickListener(View.OnClickListener {
           /* if (mIsMideaStoreEnabled) {
                val dialog = Dialog(requireActivity(), R.style.CustomDialogTheme)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.select_avtar_dialog)
                dialog.setCanceledOnTouchOutside(false)
                dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                dialog.window!!.setBackgroundDrawable(
                    ColorDrawable(
                        resources
                            .getColor(android.R.color.transparent)
                    )
                )
                dialog.show()

                val txtChangeProfileImage =
                    dialog.findViewById(R.id.txtChangeProfileImage) as TextView
                val txtGallary = dialog.findViewById(R.id.txtGallary) as TextView
                val txtTakePicture = dialog.findViewById(R.id.txtTakePicture) as TextView
                val txtClearPic = dialog.findViewById(R.id.txtClearPic) as TextView

                Utility.setSelector(
                    this,
                    txtGallary,
                    0,
                    com.zl.baseapp.R.color.transparent_bg,
                    com.zl.baseapp.R.color.all_selection_color,
                    com.zl.baseapp.R.color.transparent_bg,
                    com.zl.baseapp.R.color.all_selection_color
                )
                Utility.setSelector(
                    this,
                    txtTakePicture,
                    0,
                    com.zl.baseapp.R.color.transparent_bg,
                    com.zl.baseapp.R.color.all_selection_color,
                    com.zl.baseapp.R.color.transparent_bg,
                    com.zl.baseapp.R.color.all_selection_color
                )
                Utility.setSelector(
                    this,
                    txtClearPic,
                    0,
                    com.zl.baseapp.R.color.transparent_bg,
                    com.zl.baseapp.R.color.all_selection_color,
                    com.zl.baseapp.R.color.transparent_bg,
                    com.zl.baseapp.R.color.all_selection_color
                )

                txtGallary.setOnClickListener {
                    val i = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(i, PICK_PHOTO_FROM_GALLARY)
                    dialog.dismiss()
                }

                txtTakePicture.setOnClickListener(View.OnClickListener {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkSelfPermission(
                                requireActivity(),
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            requestPermissions(
                                arrayOf(Manifest.permission.CAMERA),
                                MT_PERMISSIONS_REQUEST_CAMERA
                            )

                            return@OnClickListener
                        } else {
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(intent, PICK_PHOTO_FROM_CAMERA)
                            dialog.dismiss()
                        }
                    } else {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, PICK_PHOTO_FROM_CAMERA)
                        dialog.dismiss()
                    }
                })

                txtClearPic.setOnClickListener {
                    val bitmap = BitmapFactory.decodeResource(
                        resources,
                        R.drawable.ic_user_profile
                    )
                    val imageName =
                        requireActivity().getExternalFilesDir(null).toString() + File.separator +
                                ".profilepic" + File.separator + "profile" + ".PNG"
                    val dir = File(
                        requireActivity().getExternalFilesDir(null).toString() + File.separator +
                                ".profilepic"
                    )
                    if (!dir.exists()) {
                        dir.mkdirs()
                    } else {
                        Utility.deleteDir(dir)
                        dir.mkdirs()
                    }
                    val outfile = File(imageName)
                    outfile.delete()
                    if (!outfile.exists()) {
                        try {
                            outfile.createNewFile()
                            val fos = FileOutputStream(outfile)
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                            fos.flush()
                            fos.close()
                            val resultUri = Uri.fromFile(File(imageName))
                            if (resultUri != null) {
                                Glide.with(requireActivity())
                                    .load(resultUri.toString())
                                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                    .into(binding.imgProfile)
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                    dialog.dismiss()
                }
            }*/
        })

        binding.imgBack.setOnClickListener(View.OnClickListener {
            activity?.onBackPressed()
        })


    }

/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_PHOTO_FROM_GALLARY || requestCode == PICK_PHOTO_FROM_CAMERA) {
                var selectedImagePath: String? = null
                var selectedImageUri: Uri? = null
                if (data!!.data == null) {
                    val path = MediaStore.Images.Media.insertImage(contentResolver, data.getExtras()!!.get("data") as Bitmap, "Title", null)
                    if (!TextUtils.isEmpty(path)) {
                        selectedImageUri = Uri.parse(path)
                    }
                } else {
                    selectedImageUri = data.data

                }
                if (selectedImageUri != null) {
                    selectedImagePath = Utility.getRealPathFromURI(this@UserInfoActivity, selectedImageUri!!)
                    val compressedImageFile = Compressor.getDefault(this@UserInfoActivity).compressToFile(File(selectedImagePath!!))
                    val imageName = getExternalFilesDir(null).toString() + File.separator +
                            ".profilepic" + File.separator + "profile-"+ Utility.getUniqueID(Utility.getUserCode(this)!!) + ".PNG"
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
                .into(mImgProfilePic)
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
            .start(this)
    }
*/

    companion object {

        private val MT_PERMISSIONS_REQUEST_CAMERA = 1002
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}