package com.tce.teacherapp.ui.dashboard

import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tce.teacherapp.R
import com.tce.teacherapp.TCEApplication
import com.tce.teacherapp.databinding.ActivityDashboardBinding
import com.tce.teacherapp.ui.BaseActivity
import com.tce.teacherapp.util.BottomNavController
import com.tce.teacherapp.util.setUpNavigation
import javax.inject.Inject
import javax.inject.Named


class DashboardActivity : BaseActivity(), BottomNavController.OnNavigationReselectedListener,
    BottomNavController.OnNavigationGraphChanged {

    @Inject
    @Named("DashboardFragmentFactory")
    lateinit var dashboardFragmentFactory: FragmentFactory

    @Inject
    @Named("SubjectsFragmentFactory")
    lateinit var subjectsFragmentFactory: FragmentFactory

    @Inject
    @Named("MessagesFragmentFactory")
    lateinit var messageFragmentFactory: FragmentFactory

    @Inject
    @Named("StudentsFragmentFactory")
    lateinit var studentsFragmentFactory: FragmentFactory

    @Inject
    @Named("PlannerFragmentFactory")
    lateinit var plannerFragmentFactory: FragmentFactory

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
        if (value) {
            binding.appBar.visibility = View.VISIBLE
        } else {
            binding.appBar.visibility = View.GONE
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    fun showHideBottomBar(value: Boolean){
        if(value){
            binding.bottomNavigationView.visibility = View.VISIBLE
        }else{
            binding.bottomNavigationView.visibility = View.GONE
        }
    }

    fun setCustomToolbar(resId: Int) {
        val logo: View = layoutInflater.inflate(resId, binding.toolBar, false)
        binding.toolBar.removeAllViews()
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

        val mbottomNavigationMenuView =
            bottomNavigationView.getChildAt(0) as BottomNavigationMenuView

        val view = mbottomNavigationMenuView.getChildAt(1)

        val itemView = view as BottomNavigationItemView

        val cart_badge: View = LayoutInflater.from(this)
            .inflate(
                R.layout.bottom_icon_msg_notification,
                mbottomNavigationMenuView, false
            )

        itemView.addView(cart_badge)

        bottomNavigationView.setUpNavigation(bottomNavController, this)
        // if (savedInstanceState == null) {
        bottomNavController.setupBottomNavigationBackStack(null)
        bottomNavController.onNavigationItemSelected()
    }

    override fun onReselectNavItem(navController: NavController, fragment: Fragment) {

    }

    override fun onBackPressed() {
        Handler().postDelayed({
            binding.bottomNavigationView.visibility = View.VISIBLE
        }, 200)

        bottomNavController.onBackPressed()
    }

     fun onBackPressed(isVisible:Boolean){
         if(isVisible) {
             Handler().postDelayed({
                 binding.bottomNavigationView.visibility = View.VISIBLE
             }, 200)
         }
        bottomNavController.onBackPressed()
    }
    override fun onGraphChange() {

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}
