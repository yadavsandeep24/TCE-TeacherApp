package com.tce.teacherapp.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.FragmentChildRelationBinding
import com.tce.teacherapp.ui.BaseFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
class ChildRelationFragment
@Inject
constructor(viewModelFactory: ViewModelProvider.Factory)
    : BaseFragment(R.layout.fragment_child_relation) {
    private lateinit var binding: FragmentChildRelationBinding

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
        binding = FragmentChildRelationBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = Bundle()
        bundle.putBoolean("isRegister",true)
        binding.fatherContainer.setOnClickListener {
            bundle.putString("relationType", "father")
            findNavController().navigate(R.id.action_childRelationFragment_to_mobileNumberFragment,bundle)
        }
        binding.motherContainer.setOnClickListener {
            bundle.putString("relationType", "mother")
            findNavController().navigate(R.id.action_childRelationFragment_to_mobileNumberFragment,bundle)
        }

        binding.guardianContainer.setOnClickListener {
            bundle.putString("relationType", "guardian")
            findNavController().navigate(R.id.action_childRelationFragment_to_mobileNumberFragment,bundle)
        }
    }
}