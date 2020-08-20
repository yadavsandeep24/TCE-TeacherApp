package com.tce.teacherapp.ui.dashboard.students

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.tce.teacherapp.R
import com.tce.teacherapp.api.response.StudentGalleryData
import com.tce.teacherapp.databinding.FragmentStudentGalleryImagePostBinding
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.student_gallery_content_bottom_bar.view.*
import kotlinx.android.synthetic.main.student_gallery_content_edit_topbar.view.*
import kotlinx.android.synthetic.main.student_top_bar.view.tv_back
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class StudentGalleryImagePostFragment
@Inject
constructor(
    viewModelFactory: ViewModelProvider.Factory
) : BaseStudentFragment(R.layout.fragment_student_gallery_image_post, viewModelFactory) {

    private lateinit var binding: FragmentStudentGalleryImagePostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        binding = FragmentStudentGalleryImagePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black, null)
        } else {
            activity?.window!!.statusBarColor = resources.getColor(R.color.color_black)
        }
        val studentGallerydata = arguments?.getParcelable("studentGalleryData") as StudentGalleryData?
        (activity as DashboardActivity).expandAppBar(false)
        (activity as DashboardActivity).bottom_navigation_view.visibility = View.GONE

        binding.topBar.tv_back.setOnClickListener {
            activity?.onBackPressed()
        }

        if (studentGallerydata != null) {
            Glide.with(requireContext())
                .load(studentGallerydata.image)
                .placeholder(R.drawable.dummy_video)
                .into(binding.ivGallery)

            if(studentGallerydata.sharedItemList != null  && studentGallerydata.sharedItemList.isNotEmpty()){
                binding.tvSharedName.text = studentGallerydata.sharedItemList[0].Name
                binding.tvSharedCount.text = (studentGallerydata.sharedItemList.size -1).toString()+" others"
            }
        }
        var lblCaption = studentGallerydata?.description

        binding.topBar.tv_tag.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("studentGalleryData",studentGallerydata)
            findNavController().navigate(R.id.action_studentGalleryImagePostFragment_to_studentGalleryTagStudentFragment,bundle)
        }
        binding.flExpandContainer.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("studentGalleryData",studentGallerydata)
            findNavController().navigate(R.id.action_studentGalleryImagePostFragment_to_studentGalleryImagePostDetailFragment,bundle)
        }

        binding.llSharedUserContainer.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable("studentGalleryData",studentGallerydata)
            findNavController().navigate(R.id.action_studentGalleryImagePostFragment_to_studentGalleryTaggedStudentFragment,bundle)
        }
        binding.topBar.tv_edit.setOnClickListener {
            binding.bottomBar.background = resources.getDrawable(R.color.white)
            binding.bottomBar.tv_title.setTextColor(resources.getColor(R.color.dark))
            binding.topBar.top_bar_title.visibility = View.VISIBLE
            binding.topBar.tv_save.visibility = View.VISIBLE
            binding.topBar.tv_tag.visibility = View.GONE
            binding.topBar.tv_edit.visibility = View.GONE
            binding.topBar.tv_delete.visibility = View.GONE
            binding.bottomBar.tv_desc.visibility = View.GONE
            binding.bottomBar.edt_caption.visibility = View.VISIBLE
            binding.bottomBar.edt_caption.setText(lblCaption)
            lblCaption?.length?.let { it1 -> binding.bottomBar.edt_caption.setSelection(it1) }
            binding.bottomBar.edt_caption.requestFocus()
        }

        binding.topBar.tv_save.setOnClickListener {
            lblCaption = binding.bottomBar.edt_caption.text.toString().trim()
            binding.bottomBar.tv_desc.text = lblCaption
            binding.bottomBar.tv_desc.tag = lblCaption
            makeTextViewResizable(binding.bottomBar.tv_desc, 1, "see more", true)
            binding.bottomBar.background = resources.getDrawable(R.color.color_black)
            binding.bottomBar.tv_title.setTextColor(resources.getColor(R.color.white))
            binding.topBar.top_bar_title.visibility = View.GONE
            binding.topBar.tv_save.visibility = View.GONE
            binding.topBar.tv_tag.visibility = View.VISIBLE
            binding.topBar.tv_edit.visibility = View.VISIBLE
            binding.topBar.tv_delete.visibility = View.VISIBLE
            binding.bottomBar.tv_desc.visibility = View.VISIBLE
            binding.bottomBar.edt_caption.visibility = View.GONE
        }
        binding.topBar.tv_delete.setOnClickListener {
            val dialog = Dialog(requireActivity(), android.R.style.Theme_Dialog)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.custom_yesno_dialog)
            dialog.setCanceledOnTouchOutside(false)
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            dialog.window!!.setBackgroundDrawable(
                ColorDrawable(resources.getColor(android.R.color.transparent))
            )
            dialog.show()

            val txtYes = dialog.findViewById(R.id.tvYes) as TextView
            val txtNo = dialog.findViewById(R.id.tvNo) as TextView

            val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
            tvTitle.visibility = View.VISIBLE
            tvTitle.text = "Delete Image"
            val tvMessage = dialog.findViewById<TextView>(R.id.tv_message)
            tvMessage.text = "Are you sure you want to delete\n" +
                    "this image?"

            txtYes.setOnClickListener {
                dialog.dismiss()
            }
            txtNo.setOnClickListener {
                dialog.dismiss()
            }
        }
        binding.bottomBar.tv_desc.text = lblCaption
        makeTextViewResizable(binding.bottomBar.tv_desc, 1, "see more", true)

    }

    fun makeTextViewResizable(tv: TextView, maxLine: Int, expandText: String, viewMore: Boolean) {
        if (tv.tag == null) {
            tv.tag = tv.text
        }
        val vto = tv.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val obs = tv.viewTreeObserver
                obs.removeGlobalOnLayoutListener(this)
                if (maxLine == 0) {
                    val lineEndIndex = tv.layout.getLineEnd(0)
                    val text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                        .toString() + " " + expandText
                    tv.text = text
                    tv.movementMethod = LinkMovementMethod.getInstance()
                    tv.setText(
                        addClickablePartTextViewResizable(
                            Html.fromHtml(tv.text.toString()), tv, maxLine, expandText,
                            viewMore
                        ), TextView.BufferType.SPANNABLE
                    )
                } else if (maxLine > 0 && tv.lineCount >= maxLine) {
                    val lineEndIndex = tv.layout.getLineEnd(maxLine - 1)
                    val text = tv.text.subSequence(0, lineEndIndex - expandText.length + 1)
                        .toString() + " " + expandText
                    tv.text = text
                    tv.movementMethod = LinkMovementMethod.getInstance()
                    tv.setText(
                        addClickablePartTextViewResizable(
                            Html.fromHtml(tv.text.toString()), tv, maxLine, expandText,
                            viewMore
                        ), TextView.BufferType.SPANNABLE
                    )
                } else {
                    val lineEndIndex = tv.layout.getLineEnd(tv.layout.lineCount - 1)
                    val text = tv.text.subSequence(0, lineEndIndex).toString() + " " + expandText
                    tv.text = text
                    tv.movementMethod = LinkMovementMethod.getInstance()
                    tv.setText(
                        addClickablePartTextViewResizable(
                            Html.fromHtml(tv.text.toString()), tv, lineEndIndex, expandText,
                            viewMore
                        ), TextView.BufferType.SPANNABLE
                    )
                }
            }
        })
    }

    private fun addClickablePartTextViewResizable(
        strSpanned: Spanned, tv: TextView,
        maxLine: Int, spanableText: String, viewMore: Boolean
    ): SpannableStringBuilder? {
        val str = strSpanned.toString()
        val ssb = SpannableStringBuilder(strSpanned)
        if (str.contains(spanableText)) {
            ssb.setSpan(object : MySpannable(true, requireContext()) {
                override fun onClick(widget: View) {
                    if (viewMore) {
                        tv.layoutParams = tv.layoutParams
                        tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                        tv.invalidate()
                        makeTextViewResizable(tv, -1, "see less", false)
                    } else {
                        tv.layoutParams = tv.layoutParams
                        tv.setText(tv.tag.toString(), TextView.BufferType.SPANNABLE)
                        tv.invalidate()
                        makeTextViewResizable(tv, 1, ".. see more", true)
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length, 0)
        }
        return ssb
    }

    open class MySpannable(isUnderline: Boolean, val context: Context) : ClickableSpan() {
        private var isUnderline = true
        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = isUnderline
            ds.color = Color.parseColor("#ffffff")
/*
            ds.textSize = context.resources.getDimension(R.dimen.font_size_14_dp)
*/
            ds.isFakeBoldText = true
        }

        override fun onClick(widget: View) {}

        /**
         * Constructor
         */
        init {
            this.isUnderline = isUnderline
        }
    }
}