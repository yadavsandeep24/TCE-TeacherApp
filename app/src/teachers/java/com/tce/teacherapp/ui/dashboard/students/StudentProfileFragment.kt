package com.tce.teacherapp.ui.dashboard.students

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.*
import com.tce.teacherapp.databinding.FragmentStudentProfileBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.students.adapter.IStudentGalleryClickListener
import com.tce.teacherapp.ui.dashboard.students.adapter.StudentPortfolioAdapter
import com.tce.teacherapp.ui.dashboard.students.state.StudentStateEvent
import com.tce.teacherapp.util.Utility
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.*
import java.io.File
import java.text.SimpleDateFormat
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class StudentProfileFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_student_profile, viewModelFactory),
    IStudentGalleryClickListener {

    private lateinit var binding: FragmentStudentProfileBinding
    private lateinit var studentID: String
    private var PICK_PHOTO_FROM_GALLARY = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val studentVo = arguments?.getParcelable("studentdata") as StudentListResponseItem?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        (activity as DashboardActivity).setCustomToolbar(R.layout.student_top_bar)
        (activity as DashboardActivity).expandAppBar(true)

        (activity as DashboardActivity).bottom_navigation_view.visibility = View.GONE
        if (studentVo != null) {
            studentID = studentVo.id
            binding.tvStudentName.text = studentVo.Name
            binding.tvBloodGroup.text = "Blood Group : " + studentVo.BloodGroup

            val pattern = "yyyy-MM-dd"
            val simpleDateFormat = SimpleDateFormat(pattern)
            val date = simpleDateFormat.parse(studentVo.DOB)
            val formattedString: String = SimpleDateFormat("dd MMM yyyy").format(date)
            binding.tvBirthDate.text = formattedString
        }
        binding.iconHome.background == resources.getDrawable(R.drawable.ic_add)
        binding.iconHome.tag = 1
        binding.iconReport.background == resources.getDrawable(R.drawable.ic_add)
        binding.iconReport.tag = 1

        binding.mainHomeDetails.setOnClickListener {
            if (binding.iconHome.tag == 1) {
                binding.iconHome.tag = 2
                binding.iconHome.background = resources.getDrawable(R.drawable.ic_line)
                binding.addressContainer.visibility = View.VISIBLE
                binding.tvAddress.text = studentVo?.Address
                binding.parentDetailsContainer.visibility = View.VISIBLE
                binding.parentDetailsContainer.removeAllViews()
                val list = studentVo?.ParentList
                if (list != null) {
                    for (parent: Parent in list) {
                        val view = LayoutInflater.from(context)
                            .inflate(R.layout.list_item_parent_details, null, false)
                        val tvStudent = view.findViewById(R.id.child_name) as TextView
                        tvStudent.text = parent.FirstName + " " + parent.LastName
                        val tvContactNo = view.findViewById<TextView>(R.id.tv_contact_no)
                        tvContactNo.text = parent.ContactNo
                        val tvRelation = view.findViewById<TextView>(R.id.tv_relation)
                        tvRelation.text = parent.Relation
                        binding.parentDetailsContainer.addView(view)
                    }
                }

            } else {
                binding.iconHome.tag = 1
                binding.iconHome.background = resources.getDrawable(R.drawable.ic_add)
                binding.addressContainer.visibility = View.GONE
                binding.parentDetailsContainer.visibility = View.GONE
            }
        }

        if (studentVo != null) {
            if (!studentVo.Term2ReportStatus && !studentVo.Term1ReportStatus) {
                binding.cardReportDetails.visibility = View.GONE
            } else {
                binding.cardReportDetails.visibility = View.VISIBLE
                binding.mainReportDetails.setOnClickListener {
                    if (binding.iconReport.tag == 1) {
                        binding.iconReport.tag = 2
                        binding.iconReport.background = resources.getDrawable(R.drawable.ic_line)
                        binding.reportContainer.visibility = View.VISIBLE
                        binding.reportContainer.removeAllViews()
                        val list = ArrayList<String>()
                        if (studentVo.Term1ReportStatus!!) {
                            list.add("Term 1 (Jan - May 2020)")
                        }
                        if (studentVo.Term2ReportStatus) {
                            list.add("Term 2 (Jun - Oct 2020)")
                        }
                        for (strLabel: String in list) {
                            val view = LayoutInflater.from(context)
                                .inflate(R.layout.list_item_progress_reports, null, false)
                            val tvTerm = view.findViewById(R.id.tv_term) as TextView
                            tvTerm.text = Html.fromHtml("<u>$strLabel</u>")
                            tvTerm.setOnClickListener {
                                findNavController().navigate(R.id.action_studentProfileFragment_to_progressCardFragment)
                            }
                            binding.reportContainer.addView(view)
                        }

                    } else {
                        binding.iconReport.tag = 1
                        binding.iconReport.background = resources.getDrawable(R.drawable.ic_add)
                        binding.reportContainer.visibility = View.GONE
                    }
                }
            }
        }
        binding.ivChangeProfile.setOnClickListener {
            val i = Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            startActivityForResult(i, PICK_PHOTO_FROM_GALLARY)
        }
        binding.btnShare.setOnClickListener {
            val bundle = Bundle()
            val list = ((binding.rvPortfolio.adapter as StudentPortfolioAdapter).modelList).toList()
            val arrList = ArrayList<Portfolio>()
            arrList.addAll(list)
            bundle.putParcelableArrayList("portfoliolistdata", arrList)
            bundle.putParcelable("studentdata", studentVo)
            findNavController().navigate(
                R.id.action_studentProfileFragment_to_shareMediaFragment,
                bundle
            )
        }

        binding.btnUpload.setOnClickListener {
            findNavController().navigate(R.id.action_studentProfileFragment_to_studentProfileUploadResouceSelectionFragment)
        }

        val tvTitle =
            (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.toolbar_title)
        tvTitle.text = (studentVo?.Name ?: "") + "'s Profile"

        val tvBack =
            (activity as DashboardActivity).binding.toolBar.findViewById<TextView>(R.id.tv_back)
        tvBack.visibility = View.VISIBLE
        tvBack.setOnClickListener {
            (activity as DashboardActivity).onBackPressed()
        }

        val bottomSheetBehaviorFilterContainer =
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(binding.bottomSheetFilterBy)

        bottomSheetBehaviorFilterContainer.state =
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehaviorFilterContainer.skipCollapsed = true
        bottomSheetBehaviorFilterContainer.isDraggable = false

        bottomSheetBehaviorFilterContainer.addBottomSheetCallback(object :
            com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehaviorFilterContainer.state = BottomSheetBehavior.STATE_EXPANDED
                }

            }
        })

        binding.sortContainer.setOnClickListener {

            if (bottomSheetBehaviorFilterContainer.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehaviorFilterContainer.state =
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED
                binding.maskLayout.visibility = View.VISIBLE
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.dim_color_dashboard))
            } else {
                bottomSheetBehaviorFilterContainer.state =
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.visibility = View.GONE
            }
        }
        binding.tvAll.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            viewModel.setStateEvent(StudentStateEvent.GetStudentPortfolio(1))
        }
        binding.tvFeedback.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            viewModel.setStateEvent(StudentStateEvent.GetStudentPortfolio(2))
        }

        binding.tvVideo.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            viewModel.setStateEvent(StudentStateEvent.GetStudentPortfolio(3))
        }

        binding.tvAudio.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            viewModel.setStateEvent(StudentStateEvent.GetStudentPortfolio(4))
        }

        binding.tvImage.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            viewModel.setStateEvent(StudentStateEvent.GetStudentPortfolio(5))
        }

        binding.maskLayout.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
        }
        viewModel.setStateEvent(StudentStateEvent.GetStudentPortfolio(1))
        subscribeObservers()


    }

    fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.studentportfolioresponse?.let {
                    Log.d("SAN", "it.studentPortFolioList.size-->" + it.size)
                    var studentPortFolioDataItem = it[0]
                    for (studentFolio: StudentPortFolioResponseItem in it) {
                        if (studentFolio.id.equals(studentID, true)) {
                            studentPortFolioDataItem = studentFolio
                            break
                        }
                    }

                    if (studentPortFolioDataItem.Note != null && studentPortFolioDataItem.Note!!.isNotEmpty()) {
                        binding.noteContainer.visibility = View.VISIBLE
                        binding.tvNoteText.text = studentPortFolioDataItem.Note
                    }
                    if (studentPortFolioDataItem.DaysPresent.isNotEmpty()) {
                        binding.tvPresentCount.text = studentPortFolioDataItem.DaysPresent
                    }
                    if (studentPortFolioDataItem.DaysAbsent.isNotEmpty()) {
                        binding.tvAbsentCount.text = studentPortFolioDataItem.DaysAbsent
                    }

                    binding.rvPortfolio.layoutManager = GridLayoutManager(activity, 1)
                    binding.rvPortfolio.setHasFixedSize(true)
                    val myAdapter = StudentPortfolioAdapter(requireContext(), false, this)
                    binding.rvPortfolio.adapter = myAdapter
                    myAdapter.modelList = studentPortFolioDataItem.Portfolio.toMutableList()
                    myAdapter.notifyDataSetChanged()
                }

            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer {
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })


        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->
            Log.d("SAN", "SubjectListFragment-->viewModel.stateMessage")

            stateMessage?.let {

                /*uiCommunicationListener.onResponseReceived(
                    response = it.response,
                    stateMessageCallback = object : StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.clearStateMessage()
                        }
                    }
                )*/
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
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data!!)
            }
        }
    }


    private fun compressAndCopyImage(selectedImagePath: String): String {
        val imageName = requireActivity().getExternalFilesDir(null)
            .toString() + File.separator +
                ".profilepic" + File.separator + "tceuser" + File.separator + Utility.getUniqueID("prof") + ".PNG"
        val isImageCopied =
            Utility.copyFileFromSourceToDestn(selectedImagePath, imageName, false)
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
                .into(binding.ivPorfile)
            //viewModel.setStateEvent(DashboardStateEvent.UpdateProfilePic(resultUri.toString()))
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

    override fun onItemClick(item: StudentGalleryData) {
        val bundle = Bundle()
        bundle.putParcelable("studentGalleryData", item)

        if (item.contenttype.equals("AV", true)) {
            if (item.src.isNotEmpty()) {
                findNavController().navigate(
                    R.id.action_studentProfileFragment_to_studentGalleryVideoPostFragment,
                    bundle
                )
            }
        } else if (item.contenttype.equals("image", true)) {
            if (item.src.isNotEmpty()) {
                findNavController().navigate(
                    R.id.action_studentProfileFragment_to_studentGalleryImagePostFragment,
                    bundle
                )
            }
        } else if (item.contenttype.equals("audio", true)) {
            if (item.src.isNotEmpty()) {
                findNavController().navigate(
                    R.id.action_studentProfileFragment_to_studentGalleryVideoPostFragment,
                    bundle
                )
            }
        }
    }

    override fun onCheckBoxClicked(item: StudentGalleryData) {
        TODO("Not yet implemented")
    }

    override fun onDateCheckBoxClicked(item: StudentGalleryResponseItem) {
        TODO("Not yet implemented")
    }

}