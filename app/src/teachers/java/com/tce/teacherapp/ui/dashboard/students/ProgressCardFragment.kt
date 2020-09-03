package com.tce.teacherapp.ui.dashboard.students

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.Objective
import com.tce.teacherapp.api.response.ProgressData
import com.tce.teacherapp.databinding.FragmentProgressCardBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import com.tce.teacherapp.ui.dashboard.students.adapter.ProgressCardAdapter
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_progress_card.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.util.*
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class ProgressCardFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_progress_card, viewModelFactory){

    private lateinit var binding: FragmentProgressCardBinding

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).bottom_navigation_view.visibility = View.GONE
        val bottomSheetBehavior = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.from(bottom_sheet)

        bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.isDraggable = false

        bottomSheetBehavior.addBottomSheetCallback(object : com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }

            }
        })

        binding.imgUpload.setOnClickListener {
            if (bottomSheetBehavior.state == com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN) {
                bottomSheetBehavior.state =
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_EXPANDED
                binding.maskLayout.visibility = View.VISIBLE
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.dim_color_dashboard))
            } else {
                bottomSheetBehavior.state =
                    com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
                binding.maskLayout.visibility = View.GONE
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
            }
        }
        binding.maskLayout.setOnClickListener {
            bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            bottomSheetBehavior.state = com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN
            binding.maskLayout.visibility = View.GONE
        }
        binding.chatContainer.setOnClickListener {
            val dialog = Dialog(requireActivity(), android.R.style.Theme_Dialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.custom_yesno_dialog)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(resources.getColor(android.R.color.transparent))
            )
            dialog.show()

            bottomSheetBehavior.state =
                com.tce.teacherapp.util.bottomSheet.BottomSheetBehavior.STATE_HIDDEN

            val patternContainer = dialog.findViewById(R.id.pattern_container) as LinearLayout
            patternContainer.visibility = View.GONE

            val txtTitle = dialog.findViewById(R.id.tv_title) as TextView
            txtTitle.visibility = View.VISIBLE
            val txtMessage = dialog.findViewById(R.id.tv_message) as TextView

            txtMessage.gravity = Gravity.CENTER

            txtTitle.text = "Share to Chat"
            txtMessage.text = "Do you want to share Term 1 Progress Card to Amit Boaz chat group?"

            val dataContainer = dialog.findViewById(R.id.data_container) as LinearLayout
            dataContainer.setPadding(10,0,10,0)

            val txtYes = dialog.findViewById(R.id.tvYes) as TextView
            val txtNo = dialog.findViewById(R.id.tvNo) as TextView

            txtYes.setOnClickListener {
                binding.maskLayout.visibility = View.GONE
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
                dialog.dismiss()

                val dialog = Dialog(requireActivity(), android.R.style.Theme_Dialog)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.custom_success_dialog)
                dialog.setCanceledOnTouchOutside(false)
                dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                dialog.window!!.setBackgroundDrawable(
                    ColorDrawable(resources.getColor(android.R.color.transparent))
                )
                dialog.show()

                val txtTitle = dialog.findViewById(R.id.tv_title) as TextView

                txtTitle.text = "Report card sent!"

                Handler().postDelayed({
                    dialog.dismiss()
                }, 1000)

            }

            txtNo.setOnClickListener(View.OnClickListener {
                binding.maskLayout.setBackgroundColor(resources.getColor(R.color.transparent))
                dialog.dismiss()
            })
        }


        binding.rvProgress.layoutManager = GridLayoutManager(activity, 1)
        binding.rvProgress.setHasFixedSize(true)
        val myAdapter = ProgressCardAdapter(requireContext())
        binding.rvProgress.adapter = myAdapter
        myAdapter.modelList = getProgressData()
        myAdapter.notifyDataSetChanged()

        binding.tvBack.setOnClickListener {
            (activity as DashboardActivity).onBackPressed()
        }

    }

    private fun getProgressData(): MutableList<ProgressData> {
        Log.d("TAG", "inside getDummyData")
        val list = ArrayList<ProgressData>()
        val obectiveList = ArrayList<Objective>()
        obectiveList.add(Objective("1", "Identifies letters", 1, ""))
        obectiveList.add(Objective("2", "Tries to associate letter sound with letter", 2, ""))
        obectiveList.add(Objective("3", "Responds to questions when askedr", 2, ""))
        obectiveList.add(Objective("4", "Attempts to speak clearly", 2, ""))
        obectiveList.add(Objective("5", "Attempts to pronounce difficult words", 2, ""))

        list.add(ProgressData("1", "Language Skills", obectiveList, 1, true, 1, 1))
        list.add(ProgressData("2", "Numeracy Skills", obectiveList, 1, true, 1, 1))
        list.add(ProgressData("3", "Cognitive Skills", obectiveList, 1, true, 1, 1))
        list.add(
            ProgressData(
                "4",
                "Personal, Social & Emotional Skills",
                obectiveList,
                1,
                true,
                1,
                1
            )
        )

        Log.d("TAG", "The size is ${list.size}")
        return list
    }

}