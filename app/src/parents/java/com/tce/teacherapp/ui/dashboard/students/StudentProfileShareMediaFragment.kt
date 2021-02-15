package com.tce.teacherapp.ui.dashboard.students

import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.EXTRA_TEXT
import android.content.pm.ResolveInfo
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tce.teacherapp.BuildConfig
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentGalleryData
import com.tce.teacherapp.api.response.StudentGalleryResponseItem
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.api.response.StudentPortFolioResponseItem
import com.tce.teacherapp.databinding.FragmentShareMediaBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.students.adapter.IStudentGalleryClickListener
import com.tce.teacherapp.ui.dashboard.students.adapter.StudentPortfolioAdapter
import com.tce.teacherapp.ui.dashboard.students.state.StudentStateEvent
import com.tce.teacherapp.util.Utility
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.io.File
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class StudentProfileShareMediaFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_share_media, viewModelFactory),
    AppAdapter.OnItemClickListener, IStudentGalleryClickListener {

    private lateinit var binding: FragmentShareMediaBinding
    private lateinit var studentID: String
    private lateinit var bottomSheetBehavior : com.tce.teacherapp.util.sheets.BottomSheetBehavior<*>
    private lateinit var bottomSheetBehaviorFilterContainer : com.tce.teacherapp.util.sheets.BottomSheetBehavior<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShareMediaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).bottom_navigation_view.visibility = View.GONE
        val studentVo = arguments?.getParcelable("studentdata") as StudentListResponseItem?
        if (studentVo != null) {
            studentID = studentVo.id
        }
        binding.rvPortfolio.layoutManager = GridLayoutManager(activity, 1)
        binding.rvPortfolio.setHasFixedSize(true)
        val myAdapter = StudentPortfolioAdapter(requireContext(), true, this)
        binding.rvPortfolio.adapter = myAdapter

        binding.tvBack.setOnClickListener {
            activity?.onBackPressed()
        }
         bottomSheetBehavior =
            com.tce.teacherapp.util.sheets.BottomSheetBehavior.from(binding.bottomSheet)

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

        binding.tvSend.setOnClickListener {
            val list = ((binding.rvPortfolio.adapter as StudentPortfolioAdapter).modelList).toList()
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
        binding.tvSavePhone.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.rvShareOptions.layoutManager = GridLayoutManager(activity, 4)
        binding.rvShareOptions.setHasFixedSize(true)

        val apps: ArrayList<App> = ArrayList()

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain|application/pdf/*|image|video/*|audio/*"
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
         bottomSheetBehaviorFilterContainer =
            com.tce.teacherapp.util.sheets.BottomSheetBehavior.from(binding.bottomSheetFilterBy)

        bottomSheetBehaviorFilterContainer.state =
            com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehaviorFilterContainer.skipCollapsed = true
        bottomSheetBehaviorFilterContainer.isDraggable = false

        bottomSheetBehaviorFilterContainer.addBottomSheetCallback(object :
            com.tce.teacherapp.util.sheets.BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehaviorFilterContainer.state = BottomSheetBehavior.STATE_EXPANDED
                }

            }
        })

        binding.sortContainer.setOnClickListener {

            if (bottomSheetBehaviorFilterContainer.state == com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehaviorFilterContainer.state =
                    com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_EXPANDED
                binding.maskLayout.visibility = View.VISIBLE
            } else {
                bottomSheetBehaviorFilterContainer.state =
                    com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.visibility = View.GONE
            }
        }
        binding.tvAll.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            viewModel.setStateEvent(StudentStateEvent.GetStudentPortfolio(1))
        }
        binding.tvFeedback.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            viewModel.setStateEvent(StudentStateEvent.GetStudentPortfolio(2))
        }

        binding.tvVideo.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            viewModel.setStateEvent(StudentStateEvent.GetStudentPortfolio(3))
        }

        binding.tvAudio.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            viewModel.setStateEvent(StudentStateEvent.GetStudentPortfolio(4))
        }

        binding.tvImage.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
            viewModel.setStateEvent(StudentStateEvent.GetStudentPortfolio(5))
        }

        binding.maskLayout.setOnClickListener {
            bottomSheetBehaviorFilterContainer.state =
                com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
            bottomSheetBehavior.state =
                com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
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
                    val myAdapter = binding.rvPortfolio.adapter as StudentPortfolioAdapter
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

    override fun onItemClick(app: App?) {
        val dialog = Dialog(requireActivity(), android.R.style.Theme_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_file_upload)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(resources.getColor(android.R.color.transparent))
        )
        dialog.show()
        val btnCancel = dialog.findViewById<TextView>(R.id.tv_cancel)

        val task = MyAsyncTask(requireActivity(), dialog, binding, app,bottomSheetBehavior,bottomSheetBehaviorFilterContainer)
        task.execute()
        btnCancel.setOnClickListener {
            if (task.status != AsyncTask.Status.FINISHED) {
                task.cancel(false)
                dialog.dismiss()
                bottomSheetBehaviorFilterContainer.state =
                    com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
                bottomSheetBehavior.state =
                    com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.visibility = View.GONE
            }
        }

    }

    companion object {
        class MyAsyncTask internal constructor(
            val context: Context,
            val dialog: Dialog,
            val binding: FragmentShareMediaBinding,
            val app: App?,
            val bottomSheetBehavior: com.tce.teacherapp.util.sheets.BottomSheetBehavior<*>,
            private val bottomSheetBehaviorFilterContainer: com.tce.teacherapp.util.sheets.BottomSheetBehavior<*>
        ) : AsyncTask<Void, String, ArrayList<Uri>>() {

            private val txtTitle = dialog.findViewById(R.id.tv_title) as TextView
            private val progressBar = dialog.findViewById(R.id.custom_progress_bar) as ProgressBar


            val myAdapter = binding.rvPortfolio.adapter as StudentPortfolioAdapter

            override fun onPreExecute() {
            }

            override fun doInBackground(vararg params: Void): ArrayList<Uri> {
                val downloadFileList: MutableList<String> = mutableListOf()

                for (portFolio in myAdapter.modelList) {
                    val galleryList = portFolio.Gallery
                    if (galleryList != null && galleryList.isNotEmpty()) {
                        for (galleryItem in galleryList) {
                            if (galleryItem.isSelected) {
                                downloadFileList.add(galleryItem.src)
                            }
                        }
                    }
                }
                publishProgress("0", downloadFileList.size.toString())

                val downloadedFileList: MutableList<String> = mutableListOf()
                for (i in downloadFileList.indices) {
                    publishProgress(i.toString(), downloadFileList.size.toString())
                    val downloadURl = downloadFileList[i]
                    val downloadFilePath = Utility.downloadFile(
                        downloadURl,
                        context.getExternalFilesDir(null)
                            .toString() + File.separator + "sharedItem" + File.separator
                    )
                    if (downloadFilePath.isEmpty() || downloadFilePath.equals("fail", true)) {
                        downloadFileList.clear()
                        break
                    } else {
                        downloadedFileList.add(downloadFilePath)
                    }

                }
                if (downloadedFileList.isEmpty()) {
                    return emptyList<Uri>() as ArrayList<Uri>
                } else {
                    val uriArrayList: ArrayList<Uri> = ArrayList()
                    for (data in downloadedFileList) {
                        //Create a new file for each path
                        val mFile = File(data)
                        //Now we need to grant Uri permissions (kitKat>)
                        val shareFileUri = FileProvider.getUriForFile(
                            context,
                            BuildConfig.APPLICATION_ID + ".provider", mFile
                        )
                        //Add Uri's to the Array that holds the Uri's
                        uriArrayList.add(shareFileUri)

                    }
                    return uriArrayList
                }
            }

            override fun onPostExecute(result: ArrayList<Uri>) {
                dialog.dismiss()
                if(result.size >0) {
                    val activity = app!!.resolveInfo!!.activityInfo
                    val packageName = activity.applicationInfo.packageName
                    val component = ComponentName(packageName, activity.name)

                    val intent = Intent(app.intent)
                    intent.component = component

                    Log.d("SAN", "packageName = $packageName, activityName = ${activity.name}")
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND_MULTIPLE
                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    sendIntent.putExtra(
                        EXTRA_TEXT,
                        "this is imporrtan message . hsd  hdsheh hdsh dhsh  sd"
                    )
                    sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, result)
                    sendIntent.component = component
                    sendIntent.type = "application/pdf/*|image|video/*|audio/*"
                    context.startActivity(sendIntent)
                }else{
                    bottomSheetBehaviorFilterContainer.state =
                        com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
                    bottomSheetBehavior.state =
                        com.tce.teacherapp.util.sheets.BottomSheetBehavior.STATE_HIDDEN
                    binding.maskLayout.visibility = View.GONE
                }

            }

            override fun onProgressUpdate(vararg text: String?) {
                if (text[0]!!.toInt() == 0) {
                    progressBar.max = text[1]!!.toInt()
                }
                txtTitle.text = "Downloading " + text[0]!! + "/" + text[1]!!
                progressBar.progress = text[0]!!.toInt()


            }
        }
    }

    override fun onItemClick(item: StudentGalleryData) {
        val bundle = Bundle()
        bundle.putParcelable("studentGalleryData", item)
        if (item.contenttype.equals("AV", true)) {
            if (item.src.isNotEmpty()) {
                findNavController().navigate(
                    R.id.action_shareMediaFragment_to_studentGalleryVideoPostFragment,
                    bundle
                )
            }
        } else if (item.contenttype.equals("image", true)) {
            if (item.src.isNotEmpty()) {
                findNavController().navigate(
                    R.id.action_shareMediaFragment_to_studentGalleryImagePostFragment,
                    bundle
                )
            }
        } else if (item.contenttype.equals("audio", true)) {
            if (item.src.isNotEmpty()) {
                findNavController().navigate(
                    R.id.action_shareMediaFragment_to_studentGalleryVideoPostFragment,
                    bundle
                )
            }
        }
    }

    override fun onCheckBoxClicked(date: String?,item: StudentGalleryData) {
    }

    override fun onDateCheckBoxClicked(item: StudentGalleryResponseItem) {
    }

}