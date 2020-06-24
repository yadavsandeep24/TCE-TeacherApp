package com.tce.teacherapp.ui.login

import android.os.Bundle
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
    : BaseFragment(R.layout.fragment_register) {

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

        binding.tvNext.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_childRelationFragment)
        }
    }
}