package com.tce.teacherapp.ui.dashboard.planner

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyVisibilityTracker
import com.airbnb.epoxy.addGlidePreloader
import com.airbnb.epoxy.glidePreloader
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentPlannerBinding
import com.tce.teacherapp.db.entity.*
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.home.listeners.EventClickListener
import com.tce.teacherapp.ui.dashboard.messages.state.MESSAGE_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.planner.adapter.DailyPlannerRvAdapter
import com.tce.teacherapp.ui.dashboard.planner.adapter.OnLoadMoreListener
import com.tce.teacherapp.ui.dashboard.planner.adapter.RecyclerViewLoadMoreScroll
import com.tce.teacherapp.ui.dashboard.planner.adapter.dailyPlannerEpoxyHolder
import com.tce.teacherapp.ui.dashboard.planner.listeners.LessonPlanClickListener
import com.tce.teacherapp.ui.dashboard.planner.state.PLANNER_VIEW_STATE_BUNDLE_KEY
import com.tce.teacherapp.ui.dashboard.planner.state.PlannerStateEvent
import com.tce.teacherapp.ui.dashboard.planner.state.PlannerViewState
import com.tce.teacherapp.ui.dashboard.subjects.adapter.SubjectListEpoxyHolder
import com.tce.teacherapp.ui.dashboard.subjects.loadImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class PlannerFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BasePlannerFragment(R.layout.fragment_planner, viewModelFactory), EventClickListener,
    LessonPlanClickListener {

    private lateinit var binding: FragmentPlannerBinding
    lateinit var itemsCells: ArrayList<DailyPlanner>
    lateinit var loadMoreItemsCells: ArrayList<DailyPlanner>
    lateinit var adapterLinear: DailyPlannerRvAdapter
    lateinit var scrollListener: RecyclerViewLoadMoreScroll
    lateinit var mLayoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { inState ->
            Log.d(TAG, "PlannerViewState: inState is NOT null")
            (inState[PLANNER_VIEW_STATE_BUNDLE_KEY] as PlannerViewState?)?.let { viewState ->
                Log.d(TAG, "PlannerViewState: restoring view state: $viewState")
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val viewState = viewModel.viewState.value
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        // (activity as DashboardActivity).setCustomToolbar(R.layout.subject_list_top_bar)
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).showHideUnderDevelopmentLabel(false)

        viewModel.setStateEvent(PlannerStateEvent.GetPlannerData(""))

        //** Set the data for our ArrayList
        setItemsData(getPlannerList(1))

        //** Set the adapterLinear of the RecyclerView
        setAdapter()

        //** Set the Layout Manager of the RecyclerView
        setRVLayoutManager()

        //** Set the scrollListerner of the RecyclerView
        setRVScrollListener()


        binding.rvMainList.layoutManager = GridLayoutManager(activity, 1)
        binding.rvMainList.setHasFixedSize(true)
        val epoxyVisibilityTracker = EpoxyVisibilityTracker()
        epoxyVisibilityTracker.attach(binding.rvMainList)
        binding.rvMainList.addGlidePreloader(
            Glide.with(this),
            preloader = glidePreloader { requestManager, model: SubjectListEpoxyHolder, _ ->
                requestManager.loadImage(model.imageUrl)
            }
        )

        binding.dailyContainer.setOnClickListener(View.OnClickListener {
            binding.monthlyContainer.background = null
            binding.dailyContainer.background =
                resources.getDrawable(R.drawable.bg_lessaon_daily_left)
            binding.tvDaily.setTextColor(resources.getColor(R.color.white))
            binding.tvMonthly.setTextColor(resources.getColor(R.color.blue))
            binding.imgDaily.background = resources.getDrawable(R.drawable.ic_day)
            binding.imgMonthly.background = resources.getDrawable(R.drawable.ic_month_blue)


        })

        binding.monthlyContainer.setOnClickListener(View.OnClickListener {
            binding.monthlyContainer.background =
                resources.getDrawable(R.drawable.bg_lessaon_daily_right)
            binding.dailyContainer.background = null
            binding.tvDaily.setTextColor(resources.getColor(R.color.blue))
            binding.tvMonthly.setTextColor(resources.getColor(R.color.white))
            binding.imgDaily.background = resources.getDrawable(R.drawable.ic_day_blue)
            binding.imgMonthly.background = resources.getDrawable(R.drawable.ic_month)
            findNavController().navigate(
                R.id.action_plannerFragment_to_monthlyPlannerFragment
            )
        })

        subscribeObservers()

    }

    private fun setItemsData(list: ArrayList<DailyPlanner>) {
        itemsCells = ArrayList()
        itemsCells.addAll(list)
    }

    private fun setAdapter() {
        adapterLinear = DailyPlannerRvAdapter(itemsCells, this, this)
        adapterLinear.notifyDataSetChanged()
        binding.rvList.adapter = adapterLinear
    }

    private fun setRVLayoutManager() {
        mLayoutManager = LinearLayoutManager(requireContext())
        binding.rvList.layoutManager = mLayoutManager
        binding.rvList.setHasFixedSize(true)
    }

    private fun setRVScrollListener() {
        mLayoutManager = LinearLayoutManager(requireContext())
        scrollListener = RecyclerViewLoadMoreScroll(mLayoutManager as LinearLayoutManager)
        scrollListener.setOnLoadMoreListener(object :
            OnLoadMoreListener {
            override fun onLoadMore() {
                LoadMoreData()
            }
        })
        binding.rvList.addOnScrollListener(scrollListener)
    }

    private fun LoadMoreData() {
        //Add the Loading View
        adapterLinear.addLoadingView()
        //Create the loadMoreItemsCells Arraylist
        loadMoreItemsCells = ArrayList()
        //Get the number of the current Items of the main Arraylist
        val start = adapterLinear.itemCount
        //Load 16 more items
        val end = start + 1
        //Use Handler if the items are loading too fast.
        //If you remove it, the data will load so fast that you can't even see the LoadingView
        Handler().postDelayed({
            loadMoreItemsCells = getPlannerList(end)
            //Remove the Loading View
            adapterLinear.removeLoadingView()
            //We adding the data to our main ArrayList
            adapterLinear.addData(loadMoreItemsCells)
            //Change the boolean isLoading to false
            scrollListener.setLoaded()
            //Update the recyclerView in the main thread
            binding.rvList.post {
                adapterLinear.notifyDataSetChanged()
            }
        }, 3000)

    }

    private fun getPlannerList(count: Int): ArrayList<DailyPlanner> {
        var jsonString = requireActivity().assets.open("json/dailyPlanner.json").bufferedReader()
            .use { it.readText() }
        val gson = Gson()
        val listPlanner = object : TypeToken<ArrayList<DailyPlanner>>() {}.type
        var list3: ArrayList<DailyPlanner> = gson.fromJson(jsonString, listPlanner)


        for (j in 0 until list3.size) {
            val selectedEventList: ArrayList<Event> = ArrayList()

            val isShowLess = false
            var nextEventCount = 4
            if (list3.get(j).eventList.size > 3) {
                for (i in 0 until 3) {
                    selectedEventList.add(list3.get(j).eventList[i])
                }
                if (list3.get(j).eventList.size < 7) {
                    nextEventCount = list3.get(j).eventList.size - 3
                }
            } else {
                for (i in 0 until list3.get(j).eventList.size) {
                    selectedEventList.add(list3.get(j).eventList[i])
                }
                nextEventCount = 0
            }
            val eventData = EventData(isShowLess, nextEventCount, selectedEventList)
            list3.get(j).eventData = eventData
        }

        var plannerList = ArrayList<DailyPlanner>()
        plannerList.add(list3.get(count - 1))

        return plannerList;
    }

    private fun subscribeObservers() {

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewState != null) {

                viewState.dailyPlannerList?.let {
                    Log.d("SAN", "plannerList-->" + it.size)
                    binding.rvMainList.withModels {
                        for (msg in it) {
                            dailyPlannerEpoxyHolder {
                                id(msg.id.toLong())
                                dailyPlanner(msg)
                                evenClickListener(this@PlannerFragment)
                                lessonPLanClickListener(this@PlannerFragment)
                            }
                        }

                    }
                }


            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer {
            // uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
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

    override fun onEventShowMoreClick(isShowLess: Boolean) {
        if (isShowLess) {
            viewModel.setStateEvent(
                PlannerStateEvent.GetPlannerEvent(
                    2,
                    true
                )
            )
        } else {
            viewModel.setStateEvent(
                PlannerStateEvent.GetPlannerEvent(
                    3,
                    false
                )
            )
        }
    }

    override fun onEventItemClick(event: Event) {
        val bundle = Bundle()
        bundle.putParcelable("eventData", event)
        findNavController().navigate(
            R.id.action_plannerFragment_to_eventDisplayFragment,
            bundle
        )
    }

    override fun onLessonPlanClick(lessonPlanPeriod: LessonPlanPeriod) {
        val bundle = Bundle()
        bundle.putParcelable("lessonPlanData", lessonPlanPeriod)
        findNavController().navigate(
            R.id.action_plannerFragment_to_lessonPlanDisplayFragment,
            bundle
        )
    }

    override fun onMarkCompletedClick(lessonPlanPeriod: LessonPlanPeriod) {
        val bundle = Bundle()
        bundle.putParcelable("lessonPlanData", lessonPlanPeriod)
        findNavController().navigate(
            R.id.action_plannerFragment_to_markCompletedFragment,
            bundle
        )
    }

    override fun onResourceMarkCompletedChecked(resource: LessonPlanResource, isChecked : Boolean) {
        TODO("Not yet implemented")
    }

}