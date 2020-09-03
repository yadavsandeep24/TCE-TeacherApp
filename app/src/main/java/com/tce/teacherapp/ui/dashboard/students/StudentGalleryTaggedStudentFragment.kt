package com.tce.teacherapp.ui.dashboard.students

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentGalleryData
import com.tce.teacherapp.api.response.StudentListResponseItem
import com.tce.teacherapp.databinding.FragmentStudentGalleryTaggedStudentBinding
import com.tce.teacherapp.ui.dashboard.students.adapter.TaggedStudentAdapter
import com.tce.teacherapp.ui.dashboard.students.interfaces.ViewHolderClickListener
import kotlinx.android.synthetic.main.student_gallery_content_edit_topbar.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class StudentGalleryTaggedStudentFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_student_gallery_tagged_student, viewModelFactory),
    ViewHolderClickListener {

    private lateinit var myAdapter: TaggedStudentAdapter
    private lateinit var binding: FragmentStudentGalleryTaggedStudentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStudentGalleryTaggedStudentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        val studentGallerydata =
            arguments?.getParcelable("studentGalleryData") as StudentGalleryData?

        binding.topBar.top_bar_title.visibility = View.VISIBLE
        binding.topBar.top_bar_title.text = "Tag People"
        binding.topBar.tv_save.visibility = View.GONE
        binding.topBar.tv_tag.visibility = View.GONE
        binding.topBar.tv_edit.visibility = View.VISIBLE
        binding.topBar.tv_delete.visibility = View.GONE

        binding.topBar.tv_back.setOnClickListener {
            activity?.onBackPressed()
        }

        val studentList: MutableList<StudentListResponseItem> = mutableListOf()
        if (studentGallerydata != null) {
            for (sharedItem in studentGallerydata.sharedItemList) {
                val student = StudentListResponseItem(
                    emptyList(),
                    "", "", "", "", sharedItem.Name, emptyList(),
                    emptyList(), emptyList(),
                    Term1ReportStatus = false,
                    Term2ReportStatus = false,
                    studentClass = "",
                    grade_division_id = "",
                    id = sharedItem.id,
                    school = "",
                    schooladdress = "",
                    schoolcontact = "",
                    teacher = "",
                    Term1ReportPDF = "",
                    Term2ReportPDF = "",
                    isSelected = false
                )
                studentList.add(student)
            }
        }

        binding.topBar.tv_edit.setOnClickListener {
            it.visibility = View.GONE
            myAdapter.setIsShowCancelIcon(true)
        }

        binding.rvTaggedStudent.layoutManager = GridLayoutManager(activity, 1)
        binding.rvTaggedStudent.setHasFixedSize(true)
        myAdapter = TaggedStudentAdapter(requireContext(), this)
        binding.rvTaggedStudent.adapter = myAdapter
        myAdapter.modelList = studentList
        myAdapter.notifyDataSetChanged()
    }

    override fun onLongTap(index: Int) {
    }

    override fun onTap(index: Int, item: StudentListResponseItem) {
        activity?.onBackPressed()
    }

    override fun onCheckBoxClicked(item: StudentListResponseItem) {

    }
}