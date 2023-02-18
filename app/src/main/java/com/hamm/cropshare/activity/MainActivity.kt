package com.hamm.cropshare.activity

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.hamm.cropshare.R
import com.hamm.cropshare.data.DataCache
import com.hamm.cropshare.databinding.ActivityMainBinding
import com.hamm.cropshare.extensions.hide
import com.hamm.cropshare.extensions.isUserLoggedIn
import com.hamm.cropshare.extensions.show
import com.hamm.cropshare.helpers.FirebaseHelper
import com.hamm.cropshare.models.StoreViewModel
import com.hamm.cropshare.models.UserViewModel
import com.hamm.cropshare.prefs

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val userViewModel: UserViewModel by viewModels()
    private val storeViewModel: StoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        // Weird code that has to be implemented this way to find main container when switching
        // from <fragment> tag in xml to <FragmentContainerView>
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val navController = navHostFragment.navController
        reloadUI(navView, isUserLoggedIn())
        setupBottomNav(navView, navController)
        navView.setupWithNavController(navController)
        observeData(navView)

        userViewModel.getUserZipCode()
        userViewModel.doesUserStoreExist()
    }

    private fun reloadUI(bottomNav: BottomNavigationView, isVisible: Boolean = false) {
        invalidateOptionsMenu()
        bottomNav.menu.findItem(R.id.navigation_account).isVisible = isVisible
    }

    private fun observeData(bottomNav: BottomNavigationView) {
        userViewModel.isLoading.observe(this) { showLoading(it) }
        userViewModel.storeExists.observe(this) {
            if (it) {
                storeViewModel.getStore()
            }
        }

        userViewModel.zipCodeChange.observe(this) {
            DataCache(this).zipCodePref = it.toString()
        }

        FirebaseHelper().firebaseAuth.addAuthStateListener {
            if (it.currentUser == null) {
                reloadUI(bottomNav, false)
                prefs.zipCodePref = null
            } else {
                reloadUI(bottomNav, true)
            }
        }
    }

    private fun setupBottomNav(bottomNav: BottomNavigationView, navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.navigation_register -> bottomNav.hide()
                R.id.navigation_login -> bottomNav.hide()
                else -> bottomNav.show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean = false) {
        if (isLoading) {
            binding.loadingIndicator.visibility = View.VISIBLE
        } else {
            binding.loadingIndicator.visibility = View.GONE
        }
    }
}