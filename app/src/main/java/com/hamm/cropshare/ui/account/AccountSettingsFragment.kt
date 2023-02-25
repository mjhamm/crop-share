package com.hamm.cropshare.ui.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hamm.cropshare.databinding.FragmentAccountSettingsBinding
import com.hamm.cropshare.helpers.FirebaseHelper

class AccountSettingsFragment: Fragment() {

    private lateinit var binding: FragmentAccountSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountSettingsBinding.inflate(inflater)

        FirebaseHelper().firebaseAuth.currentUser?.email?.let {
            binding.currentEmailAddress.text = it
        }

        return binding.root
    }
}