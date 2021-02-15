package com.tce.teacherapp.ui.dashboard.students

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tce.teacherapp.BuildConfig
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.databinding.FragmentProgressCardBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.students.adapter.ProgressCardAdapter
import com.tce.teacherapp.util.Utility
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.*
import java.io.File
import java.util.*
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class ProgressCardFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_progress_card, viewModelFactory),
    AppAdapter.OnItemClickListener {

    private lateinit var binding: FragmentProgressCardBinding
    private lateinit var reportPath :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProgressCardBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).bottom_navigation_view.visibility = View.GONE
        val studentVo = arguments?.getParcelable("studentdata") as StudentListResponseItem?
        if (studentVo != null) {
            reportPath = studentVo.Term1ReportPDF.toString()
        }
        val bottomSheetBehavior = com.tce.teacherapp.util.sheets.BottomSheetBehavior.from(binding.bottomSheet)

        bottomSheetBehavior.state = com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.isDraggable = false

        bottomSheetBehavior.addBottomSheetCallback(object :
            com.tce.teacherapp.util.sheets.BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }

            }
        })

        binding.imgUpload.setOnClickListener {
            if (bottomSheetBehavior.state == com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state =
                    com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_EXPANDED
                binding.maskLayout.visibility = View.VISIBLE
            } else {
                bottomSheetBehavior.state =
                    com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.visibility = View.GONE
            }
        }



        binding.rvProgress.layoutManager = GridLayoutManager(activity, 1)
        binding.rvProgress.setHasFixedSize(true)
        val myAdapter = ProgressCardAdapter(requireContext())
        binding.rvProgress.adapter = myAdapter
        if (studentVo != null) {
            binding.tvStudentName.text = studentVo.Name
            binding.tvSchoolName.text = studentVo.school
            if(!studentVo.studentClass.isNullOrEmpty()) {
                binding.tvClassName.text = studentVo.studentClass
            }
            binding.tvStudentAge.text = "${Utility.getAge(studentVo.DOB)} years old"
            binding.tvTeacherName.text = studentVo.teacher
            if(studentVo.ProgressCard != null ) {
                myAdapter.modelList = studentVo.ProgressCard[0].ProgressData
            }
        }
        myAdapter.notifyDataSetChanged()

        binding.tvBack.setOnClickListener {
            (activity as DashboardActivity).onBackPressed()
        }

        binding.tvDownloadPdf.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.rvShareOptions.layoutManager = GridLayoutManager(activity, 4)
        binding.rvShareOptions.setHasFixedSize(true)

        val apps: ArrayList<App> = ArrayList()

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/pdf"
        val resInfo: List<ResolveInfo> = requireActivity().packageManager.queryIntentActivities(
            shareIntent,
            0
        )
        if (resInfo.isNotEmpty()) {
            for (resolveInfo in resInfo) {

                val targetIntent = Intent(shareIntent)
                apps.add(App(targetIntent, resolveInfo))
            }

        }
        if (apps.size > 0) {
            binding.rvShareOptions.adapter = AppAdapter(apps, this)
        }
        binding.maskLayout.setOnClickListener {
            bottomSheetBehavior.state = com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
            bottomSheetBehavior.state = com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
        }
    }

    override fun onItemClick(app: App?) {
        GlobalScope.launch(Dispatchers.Main) {
                val downloadedFile = withContext(Dispatchers.IO) {
                    Utility.downloadFile(reportPath,requireActivity().getExternalFilesDir(null).toString() + File.separator + "sharedItem" + File.separator)
                }
            val activity = app!!.resolveInfo!!.activityInfo
            val packageName = activity.applicationInfo.packageName
            val component = ComponentName(packageName, activity.name)

            val intent = Intent(app.intent)
            intent.component = component

            Log.d("SAN", "packageName = $packageName, activityName = ${activity.name}")
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sendIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(requireActivity(),
                BuildConfig.APPLICATION_ID + ".provider",File(downloadedFile)) )
            sendIntent.component = component
            sendIntent.type = "application/pdf"
            startActivity(sendIntent)
        }
    }

}