package com.hamm.cropshare.ui.account

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.hamm.cropshare.R
import com.hamm.cropshare.adapters.AccountListAdapter
import com.hamm.cropshare.data.AccountListItem
import com.hamm.cropshare.data.Constants.Companion.ERROR_GETTING_STORE_INFO
import com.hamm.cropshare.data.Location
import com.hamm.cropshare.data.StoreItem
import com.hamm.cropshare.databinding.FragmentAccountBinding
import com.hamm.cropshare.databinding.LayoutDeleteAccountBottomSheetBinding
import com.hamm.cropshare.databinding.LayoutEditStoreItemBottomSheetBinding
import com.hamm.cropshare.extensions.*
import com.hamm.cropshare.helpers.FirebaseHelper
import com.hamm.cropshare.listeners.AccountListItemListener
import com.hamm.cropshare.models.UserViewModel
import com.hamm.cropshare.prefs

class AccountFragment : Fragment(), AccountListItemListener {

    private var _binding: FragmentAccountBinding? = null
    private var accountAdapter: AccountListAdapter? = null

    private val userViewModel: UserViewModel by activityViewModels()

    private var currentAddress: String? = null
    private var currentZipCode: String? = null
    private var updatedAddress: String? = null
    private var updatedZipCode: String? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAccountBinding.inflate(inflater, container, false)

        binding.updateAddressInfoButton.isEnabled = false
        accountAdapter = AccountListAdapter(this)

        observeAccountChanges()
        setupViews()

        userViewModel.getUserStoreInformation()
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

        userViewModel.streetAddress.observe(viewLifecycleOwner) {
            binding.updateAddressInfoButton.isEnabled = false
            binding.streetAddressEdittext.setText(it)
            prefs.userStoreAddressPref = it
            currentAddress = it
        }

        userViewModel.zipCode.observe(viewLifecycleOwner) {
            binding.updateAddressInfoButton.isEnabled = false
            binding.zipCodeEdittext.setText(it)
            prefs.userStoreZipCodePref = it
            currentZipCode = it
        }

        userViewModel.location.observe(viewLifecycleOwner) {
            binding.zipCodeEdittext.setText(it.zipCode)
            binding.streetAddressEdittext.setText(it.streetAddress)
            prefs.userStoreAddressPref = it.streetAddress
            prefs.userStoreZipCodePref = it.zipCode
            currentAddress = it.streetAddress
            currentZipCode = it.zipCode
            binding.updateAddressInfoButton.isEnabled = false
        }
    }

    private fun setupViews() {
        binding.updateAddressInfoButton.setOnClickListener {
            val streetAddress = binding.streetAddressEdittext.text.toString()
            val zipCode = binding.zipCodeEdittext.text.toString()
            prefs.userStoreZipCodePref = zipCode
            prefs.userStoreAddressPref = streetAddress
            userViewModel.updateUserStoreLocation(Location(streetAddress, zipCode))
            binding.streetAddressEdittext.clearFocus()
            binding.zipCodeEdittext.clearFocus()
            hideKeyboard()
        }

        binding.streetAddressEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                updatedAddress = p0.toString()
                updateAddressButton()
            }

        })

        binding.zipCodeEdittext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                updatedZipCode = if (p0.toString().length > 5) {
                    binding.zipCodeEdittext.setText(p0?.substring(0, 5))
                    p0?.substring(0,5)
                } else {
                    p0.toString()
                }
                updateAddressButton()
            }
        })

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            hideKeyboard()
            binding.streetAddressEdittext.clearFocus()
            binding.zipCodeEdittext.clearFocus()
        }

        with(binding.accountOptionsRecyclerview) {
            accountAdapter?.submitList(AccountListItem.values().toList())
            layoutManager = LinearLayoutManager(this@AccountFragment.context)
            adapter = accountAdapter
        }
    }

    private fun updateAddressButton() {
        binding.updateAddressInfoButton.isEnabled =
            binding.streetAddressEdittext.text.isNotEmpty() &&
                    isZipCodeValid() &&
                    (currentAddress != updatedAddress ||
                    currentZipCode != updatedZipCode)
    }

    private fun isZipCodeValid(): Boolean {
        val zipCode = binding.zipCodeEdittext.text.toString()
        if (zipCode.isNotEmpty() && zipCode.length == 5) {
            return try {
                zipCode.toLong()
                true
            } catch (exception: Exception) {
                false
            }
        }
        return false
    }

    private fun showDeleteAccountDialog() {
        val bottomSheet = BottomSheetDialog(requireContext(), R.style.DialogStyle)
        val dialogBinding = LayoutDeleteAccountBottomSheetBinding.inflate(layoutInflater)
        bottomSheet.setContentView(dialogBinding.root)
        bottomSheet.show()

        dialogBinding.cancelDeleteButton.setOnClickListener {
            bottomSheet.dismiss()
        }
        dialogBinding.deleteAccountButton.setOnClickListener {
            userViewModel.userDeleteAccount()
        }
    }

    private fun navigateToAccountSettings() {
        val action = AccountFragmentDirections.actionNavigationAccountToNavigationAccountSettings()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAccountListItemClicked(accountItem: AccountListItem, position: Int) {
        when(position) {
            0 -> { navigateToAccountSettings()}
            1 -> { userViewModel.userLogout() }
            2 -> { showDeleteAccountDialog() }
        }
    }
}