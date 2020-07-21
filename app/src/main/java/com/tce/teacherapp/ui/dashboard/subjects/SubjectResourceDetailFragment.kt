package com.tce.teacherapp.ui.dashboard.subjects

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentSubjectResourceDetailBinding
import com.tce.teacherapp.db.entity.Resource
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class SubjectResourceDetailFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseSubjectFragment(R.layout.fragment_subject_resource_detail,viewModelFactory) {

    private lateinit var binding: FragmentSubjectResourceDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = FragmentSubjectResourceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).showHideUnderDevelopmentLabel(false)
        (activity as DashboardActivity).showHideBottomBar(true)
        val resourceVo = arguments?.getParcelable("resourceData") as Resource?
        if (resourceVo != null) {
            Glide.with(requireActivity())
                .load(resourceVo.image)
                .placeholder(R.drawable.topic_resource_bg)
                .into(binding.imgVideoThumb)
            binding.tvTitle.text = resourceVo.title
            binding.tvTaggingLevel.text = resourceVo.TaggingLevel
            binding.tvDesc.text = resourceVo.description
            binding.tvResourceTypeLabel.text = resourceVo.contenttype
            when {
                resourceVo.contenttype.equals("av", true) -> {
                    binding.tvResourceTypeLabel.text ="Video"
                    binding.vwResourceIcon.background = resources.getDrawable(R.drawable.ic_video)
                }
                resourceVo.contenttype.equals("Image", true) -> {
                    binding.vwResourceIcon.background = resources.getDrawable(R.drawable.ic_image)
                }
                resourceVo.contenttype.equals("activity", true) -> {
                    binding.vwResourceIcon.background =resources.getDrawable(R.drawable.ic_media_type_doc)
                }
                resourceVo.contenttype.equals("audio", true) -> {
                    binding.vwResourceIcon.background =resources.getDrawable(R.drawable.ic_media_type_audio)
                }
                else ->{

                }
            }
            if(resourceVo.skills.isNotEmpty()) {
                binding.lnrSkills.visibility = View.VISIBLE
                for (skill in resourceVo.skills) {
                    val skillView = LayoutInflater.from(context).inflate(R.layout.list_item_skills, binding.lnrSkills, false)
                    val tvName = skillView.findViewById(R.id.tv_skill_name) as TextView
                    val container = skillView.findViewById(R.id.container) as LinearLayout
                    tvName.text = skill
                    container.setPadding(0,0,10,0)
                    binding.lnrSkills.addView(skillView)
                }
            }
        }
        binding.tvBack.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.imgPlay.setOnClickListener {
            val bundle = Bundle()
            if (resourceVo != null) {
                bundle.putString("title",resourceVo.title)
            }
            if (resourceVo != null) {
                bundle.putString("url",resourceVo.src)
            }

            if (resourceVo != null) {
                when {
                    resourceVo.contenttype.equals("av", true) -> {
                        bundle.putBoolean("isModality",false)
                        bundle.putString("type",resourceVo.type)
                        findNavController().navigate(R.id.action_subjectResourceDetailFragment_to_videoPlayerFragment,bundle)
                    }
                    resourceVo.contenttype.equals("Image", true) -> {
                        findNavController().navigate(R.id.action_subjectResourceDetailFragment_to_imageContentFragment,bundle)
                    }
                    resourceVo.contenttype.equals("activity", true) -> {
                        findNavController().navigate(R.id.action_subjectResourceDetailFragment_to_HTMLContentFragment,bundle)
                    }
                    resourceVo.contenttype.equals("audio", true) -> {
                    }
                }
            }

        }
    }

}