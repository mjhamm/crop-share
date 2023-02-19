package com.hamm.cropshare.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.hamm.cropshare.R
import com.hamm.cropshare.databinding.FragmentLoginBinding
import com.hamm.cropshare.extensions.createSnackbar
import com.hamm.cropshare.extensions.createToast
import com.hamm.cropshare.models.UserViewModel
import com.hamm.cropshare.prefs
import com.hamm.cropshare.ui.register.RegisterFragmentDirections

class LoginFragment: Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private var email: String? = null
    private var password: String? = null

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)

        setupButtons()

        observeLiveData()

        return binding.root
    }

    private fun getEmailPassword(): Pair<String, String> {
        val email = binding.loginEmailEdittext.text.toString().trim()
        val password = binding.loginPasswordEdittext.text.toString().trim()
        return Pair(email, password)
    }

    private fun setupButtons() {
        binding.loginButton.setOnClickListener {
            loginUser()
        }

        binding.showHidePassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.loginPasswordEdittext.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.loginPasswordEdittext.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        binding.loginEmailEdittext.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                email = p0.toString()
                binding.loginButton.isEnabled = shouldEnableLoginButton()
            }
        })

        binding.loginPasswordEdittext.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                password = p0.toString()
                binding.loginButton.isEnabled = shouldEnableLoginButton()
            }
        })

        binding.registerTextview.setOnClickListener {
            if (!findNavController().popBackStack(R.id.navigation_register, false)) {
                val action = LoginFragmentDirections.actionNavigationLoginToNavigationRegister()
                findNavController().navigate(action)
            }
        }
    }

    private fun shouldEnableLoginButton(): Boolean {
        if (Patterns.EMAIL_ADDRESS.matcher(getEmailPassword().first).matches() && getEmailPassword().second.isNotEmpty()) {
            return true
        }
        return false
    }

    private fun loginUser() {
        if (Patterns.EMAIL_ADDRESS.matcher(getEmailPassword().first).matches() && getEmailPassword().second.isNotEmpty()) {
            userViewModel.userLogin(getEmailPassword().first, getEmailPassword().second)
        } else {
            createSnackbar(binding.root, "The email address is not valid. Please re-enter and try again.")
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
//        userViewModel.userId.observe(viewLifecycleOwner) {
//            prefs.userUidPref = it
//        }
    }
}