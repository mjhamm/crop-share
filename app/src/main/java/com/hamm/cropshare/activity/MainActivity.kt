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

        val navController = navHostFragment.navController
        // Update the UI based on whether the user is logged in or not
        reloadUI(navView, isUserLoggedIn())
        setupBottomNav(navView, navController)
        navView.setupWithNavController(navController)
        // setup our observers from data within our view models
        observeData(navView)

        // Because we use the User ID to make calls to Firebase, we need to check if
        // it is null and if it is, get the UID from their Firebase user if they are logged in
        if (prefs.userUidPref?.isEmpty() == true) {
            FirebaseHelper().firebaseUserUID?.let {
                prefs.userUidPref = it
            }
        }

        userViewModel.getUserZipCode()
    }

    /**
     * Updates the Bottom Navigation Bar to either show/hide the accounts item based
     * on whether or not the user is logged in
     */
    private fun reloadUI(bottomNav: BottomNavigationView, isVisible: Boolean = false) {
        invalidateOptionsMenu()
        bottomNav.menu.findItem(R.id.navigation_account).isVisible = isVisible
    }

    /**
     * A list of all of the observers that are listening for changed in the values that are
     * coming back from the different View Models
     */
    private fun observeData(bottomNav: BottomNavigationView) {
        userViewModel.isLoading.observe(this) { showLoading(it) }

        userViewModel.userId.observe(this) {
            prefs.userUidPref = it
        }
        userViewModel.location.observe(this) {
            prefs.userStoreZipCodePref = it.zipCode
            prefs.userStoreAddressPref = it.streetAddress
        }

        FirebaseHelper().firebaseAuth.addAuthStateListener {
            if (it.currentUser == null) {
                prefs.clearPreferences()
                reloadUI(bottomNav, false)
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