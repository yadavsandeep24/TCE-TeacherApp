package com.tce.teacherapp.ui.dashboard

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tce.teacherapp.TCEApplication
import com.tce.teacherapp.R
import com.tce.teacherapp.databinding.ActivityDashboardBinding
import com.tce.teacherapp.ui.BaseActivity
import com.tce.teacherapp.util.BottomNavController
import com.tce.teacherapp.util.setUpNavigation
import javax.inject.Inject
import javax.inject.Named


class DashboardActivity : BaseActivity(), BottomNavController.OnNavigationReselectedListener,
    BottomNavController.OnNavigationGraphChanged {


    @Inject
    @Named("SubjectsFragmentFactory")
    lateinit var subjectsFragmentFactory: FragmentFactory


    @Inject
    @Named("MessagesFragmentFactory")
    lateinit var messageFragmentFactory: FragmentFactory

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory

    private lateinit var bottomNavigationView: BottomNavigationView

    lateinit var binding: ActivityDashboardBinding

    private val bottomNavController by lazy(LazyThreadSafetyMode.NONE) {
        BottomNavController(
            this,
            R.id.main_fragments_container,
            R.id.nav_dashboard,
            this
        )
    }

    override fun inject() {
        (application as TCEApplication).appComponent
            .injectDashBoardActivity(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        setupActionBar()
        setupBottomNavigationView()
    }

    override fun displayProgressBar(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun expandAppBar(value: Boolean) {
       binding.appBar.setExpanded(value)

    }

    private fun setupActionBar(){
        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
    fun setCustomToolbar(resId :Int){
        val logo: View = layoutInflater.inflate(resId, binding.toolBar,false)
        binding.toolBar.addView(logo)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        when (item?.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupBottomNavigationView() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setUpNavigation(bottomNavController, this)
        // if (savedInstanceState == null) {
        bottomNavController.setupBottomNavigationBackStack(null)
        bottomNavController.onNavigationItemSelected()
    }

    override fun onReselectNavItem(navController: NavController, fragment: Fragment) {

    }

    override fun onBackPressed() = bottomNavController.onBackPressed()
    override fun onGraphChange() {

    }
}
