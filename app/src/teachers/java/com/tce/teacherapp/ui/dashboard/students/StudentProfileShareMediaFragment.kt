package com.tce.teacherapp.ui.dashboard.students

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.*
import com.tce.teacherapp.databinding.FragmentShareMediaBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.students.adapter.IStudentGalleryClickListener
import com.tce.teacherapp.ui.dashboard.students.adapter.StudentPortfolioAdapter
import com.tce.teacherapp.ui.dashboard.students.state.StudentStateEvent
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class StudentProfileShareMediaFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_share_media, viewModelFactory),
    IStudentGalleryClickListener {

    private lateinit var binding : FragmentShareMediaBinding
    private lateinit var studentID: String

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
        val portFolioList:ArrayList<Portfolio>? = arguments?.getParcelableArrayList("portfoliolistdata")
        val studentVo = arguments?.getParcelable("studentdata") as StudentListResponseItem?
        if (studentVo != null) {
            studentID = studentVo.id
        }
        binding.rvPortfolio.layoutManager = GridLayoutManager(activity, 1)
        binding.rvPortfolio.setHasFixedSize(true)
        val myAdapter = StudentPortfolioAdapter(requireContext(), true,this)
        binding.rvPortfolio.adapter = myAdapter
        if (portFolioList != null) {
            myAdapter.modelList =portFolioList.toMutableList()
        }
        myAdapter.notifyDataSetChanged()

        binding.tvBack.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.tvSend.setOnClickListener {
            val bundle = Bundle()
            val list = ((binding.rvPortfolio.adapter as StudentPortfolioAdapter).modelList).toList()
            val arrList = ArrayList<Portfolio>()
            arrList.addAll(list)
            bundle.putParcelableArrayList("portfoliolistdata",arrList)
            bundle.putParcelable("studentdata",studentVo)
            findNavController().navigate(R.id.action_shareMediaFragment_to_studentProfileShareChatFragment,bundle)
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
        subscribeObservers()

    }
    fun subscribeObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.studentportfolioresponse?.let {
                    Log.d("SAN","it.studentPortFolioList.size-->"+it.size)
                    var studentPortFolioDataItem = it[0]
                    for (studentFolio: StudentPortFolioResponseItem in it) {
                        if(studentFolio.id.equals(studentID,true)){
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
        }else if (item.contenttype.equals("audio", true)) {
            if (item.src.isNotEmpty()) {
                findNavController().navigate(
                    R.id.action_shareMediaFragment_to_studentGalleryVideoPostFragment,
                    bundle
                )
            }
        }
    }

    override fun onCheckBoxClicked(item: StudentGalleryData) {
    }

    override fun onDateCheckBoxClicked(item: StudentGalleryResponseItem) {
    }
}