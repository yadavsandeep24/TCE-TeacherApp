package com.tce.teacherapp.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentRegisterBinding
import com.tce.teacherapp.ui.BaseFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class RegisterFragment
@Inject
constructor(viewModelFactory: ViewModelProvider.Factory)
    : BaseFragment(R.layout.fragment_register),BaseFragment.OnKeyboardVisibilityListener  {

    private lateinit var binding: FragmentRegisterBinding

    val viewModel: LoginViewModel by viewModels {
        viewModelFactory
    }

    override fun setupChannel() {
        viewModel.setupChannel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setKeyboardVisibilityListener(binding.root,this)

        binding.tvNext.isEnabled = false
        binding.tvNext.alpha = 0.4f

        binding.edtCode.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.length<8) {
                    binding.tvNext.isEnabled = false
                    binding.tvNext.alpha = 0.4f
                } else {
                    binding.tvNext.isEnabled = true
                    binding.tvNext.alpha = 1.0f
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        binding.tvNext.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_childRelationFragment)
        }
    }

    override fun onVisibilityChanged(visible: Boolean) {
        if (visible) {
            binding.flBottom.visibility = View.GONE
        }else{
            binding.flBottom.visibility = View.VISIBLE
        }
    }
}