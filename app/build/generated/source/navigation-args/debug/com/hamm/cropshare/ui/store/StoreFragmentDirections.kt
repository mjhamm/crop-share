package com.hamm.cropshare.ui.store

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.hamm.cropshare.R

public class StoreFragmentDirections private constructor() {
  public companion object {
    public fun actionNavigationStoreToNavigationLogin(): NavDirections =
        ActionOnlyNavDirections(R.id.action_navigation_store_to_navigation_login)

    public fun actionNavigationStoreToNavigationRegister(): NavDirections =
        ActionOnlyNavDirections(R.id.action_navigation_store_to_navigation_register)
  }
}
