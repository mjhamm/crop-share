package com.hamm.cropshare.ui.account

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.hamm.cropshare.R

public class AccountFragmentDirections private constructor() {
  public companion object {
    public fun actionNavigationAccountToNavigationSearch(): NavDirections =
        ActionOnlyNavDirections(R.id.action_navigation_account_to_navigation_search)

    public fun actionNavigationAccountToNavigationAccountSettings(): NavDirections =
        ActionOnlyNavDirections(R.id.action_navigation_account_to_navigation_account_settings)
  }
}
