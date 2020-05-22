package com.tce.teacherapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tce.teacherapp.R
import com.tce.teacherapp.ui.dashboard.DashboardActivity
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment @Inject constructor(viewModelFactory: ViewModelProvider.Factory) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnLogin = view.findViewById<Button>(R.id.login)
        btnLogin.setOnClickListener {

            val i = Intent(activity, DashboardActivity::class.java)
            startActivity(i)
        }
    }

}
