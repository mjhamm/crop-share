package com.hamm.cropshare.ui.register

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hamm.cropshare.R
import com.hamm.cropshare.databinding.FragmentRegisterBinding
import com.hamm.cropshare.helpers.FirebaseHelper
import com.hamm.cropshare.models.UserViewModel
import com.hamm.cropshare.prefs

class RegisterFragment: Fragment() {

    private lateinit var binding: FragmentRegisterBinding

    private var email: String? = null
    private var password: String? = null
    private var confirmPassword: String? = null

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater)

        setupButtons()
        observeLiveData()

        return binding.root
    }

    private fun setupButtons() {
        binding.registerButton.setOnClickListener {
            checkLoginInformation()
        }

        binding.loginTextview.setOnClickListener {
            if (!findNavController().popBackStack(R.id.navigation_login, false)) {
                val action = RegisterFragmentDirections.actionNavigationRegisterToNavigationLogin()
                findNavController().navigate(action)
            }
        }
    }

    private fun checkLoginInformation() {
        if (Patterns.EMAIL_ADDRESS.matcher(binding.registerEmailEdittext.text.toString()).matches()) {
            email = binding.registerEmailEdittext.text.toString()
        }
        if (binding.registerPasswordEdittext.text.isNotEmpty()) {
            password = binding.registerPasswordEdittext.text.toString()
        }
        if (binding.registerConfirmPasswordEdittext.text.isNotEmpty() && binding.registerConfirmPasswordEdittext.text.toString() == binding.registerPasswordEdittext.text.toString()) {
            confirmPassword = binding.registerConfirmPasswordEdittext.text.toString()
        }
        email?.let {
            password?.let { password ->
                confirmPassword?.let { _ ->
                    userViewModel.userRegister(it, password)
                }
            }
        }
    }

    private fun observeLiveData() {
        userViewModel.isLoggedIn.observe(
            viewLifecycleOwner
        ) {
            if (it) {
                findNavController().popBackStack()
            }
        }

        userViewModel.userId.observe(
            viewLifecycleOwner
        ) {
            userViewModel.userCreateInDB(it)
        }
    }
}