package com.hamm.cropshare.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.hamm.cropshare.databinding.FragmentAccountBinding
import com.hamm.cropshare.helpers.FirebaseHelper
import com.hamm.cropshare.models.UserViewModel

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null

    private val userViewModel: UserViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAccountBinding.inflate(inflater, container, false)

        setupButtons()
        observeAccountChanges()
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
    }

    private fun setupButtons() {
        binding.logoutButton.setOnClickListener {
            userViewModel.userLogout()
        }
        binding.deleteAccountButton.setOnClickListener {
            userViewModel.userDeleteAccount()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}