package com.hamm.cropshare.ui.account

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hamm.cropshare.databinding.FragmentAccountBinding
import com.hamm.cropshare.extensions.createSnackbar
import com.hamm.cropshare.extensions.hideKeyboard
import com.hamm.cropshare.helpers.FirebaseHelper
import com.hamm.cropshare.models.UserViewModel

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null

    private val userViewModel: UserViewModel by activityViewModels()
    private var currentZipCode: Long? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAccountBinding.inflate(inflater, container, false)

        binding.updateZipcodeButton.isEnabled = false
        observeAccountChanges()
        userViewModel.getUserZipCode()
        setupButtons()
        return binding.root
    }

    private fun observeAccountChanges() {
        userViewModel.isLoggedIn.observe(
            viewLifecycleOwner
        ) {
            if (!it) {
                val action = AccountFragmentDirections.actionNavigationAccountToNavigationSearch()
                findNavController().navigate(action)
            }
        }

        userViewModel.zipCodeChange.observe(viewLifecycleOwner) {
            currentZipCode = it
            binding.updateZipEdittext.setText(it.toString())
            binding.updateZipcodeButton.isEnabled = false
        }
    }

    private fun setupButtons() {
        binding.logoutButton.setOnClickListener {
            userViewModel.userLogout()
        }
        binding.deleteAccountButton.setOnClickListener {
            userViewModel.userDeleteAccount()
        }

        binding.updateZipcodeButton.setOnClickListener {
            updateZipCode()
        }

        binding.updateZipEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                binding.updateZipcodeButton.isEnabled =
                    !(p0?.isEmpty() == true || p0?.toString() == currentZipCode.toString())
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            hideKeyboard()
            binding.updateZipEdittext.clearFocus()
        }

    }

    private fun updateZipCode() {
        val newZipCode = binding.updateZipEdittext.text.toString()
        if (newZipCode.isNotEmpty() && newZipCode.length == 5) {
            try {
                newZipCode.toLong()
                if (currentZipCode != newZipCode.toLong()) {
                    userViewModel.userUpdateZipCode(newZipCode.toLong())
                }
            } catch (exception: NumberFormatException) {
                binding.updateZipEdittext.setText(currentZipCode?.toString())
                createSnackbar(binding.root, "There was an error updating your zip code.", 0)
            }
        } else {
            binding.updateZipEdittext.setText(currentZipCode?.toString())
            createSnackbar(binding.root, "There was an error updating your zip code.", 0)
        }
        binding.updateZipEdittext.clearFocus()
        binding.updateZipcodeButton.isEnabled = false
        hideKeyboard()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}