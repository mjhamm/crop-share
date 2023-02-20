package com.hamm.cropshare.ui.register

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hamm.cropshare.R
import com.hamm.cropshare.databinding.FragmentRegisterBinding
import com.hamm.cropshare.extensions.hide
import com.hamm.cropshare.extensions.show
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
            email?.let {email ->
                password?.let {pass ->
                    confirmPassword?.let {
                        userViewModel.userRegister(email, pass)
                    }
                }
            }
        }

        binding.loginTextview.setOnClickListener {
            if (!findNavController().popBackStack(R.id.navigation_login, false)) {
                val action = RegisterFragmentDirections.actionNavigationRegisterToNavigationLogin()
                findNavController().navigate(action)
            }
        }

        binding.registerConfirmPasswordEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                confirmPassword = p0.toString()
                updateRegisterButton()
            }
        })

        binding.registerPasswordEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                password = p0.toString()
                updateRegisterButton()
            }
        })

        binding.registerEmailEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                email = p0.toString()
                updateRegisterButton()
            }
        })
    }

    private fun shouldEnableRegisterButton(): Boolean {
        email?.let { email ->
            password?.let { password ->
                confirmPassword?.let {confirm ->
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password == confirm) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun updateRegisterButton() {
        password?.let { pass ->
            confirmPassword?.let {confirm ->
                if (confirm == pass) {
                    binding.registerPasswordEdittext.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_edittext)
                    binding.registerConfirmPasswordEdittext.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_edittext)
                    binding.passwordsNoMatch.hide()
                } else {
                    binding.registerPasswordEdittext.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_edittext_error)
                    binding.registerConfirmPasswordEdittext.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_edittext_error)
                    binding.passwordsNoMatch.show()
                }
            }
        }
        binding.registerButton.isEnabled = shouldEnableRegisterButton()
    }

    private fun observeLiveData() {
        userViewModel.isLoggedIn.observe(
            viewLifecycleOwner
        ) {
            if (it) {
                findNavController().popBackStack()
            }
        }
    }
}