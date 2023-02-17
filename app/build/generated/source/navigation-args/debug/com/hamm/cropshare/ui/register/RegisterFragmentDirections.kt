package com.hamm.cropshare.ui.register

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.hamm.cropshare.R

public class RegisterFragmentDirections private constructor() {
  public companion object {
    public fun actionNavigationRegisterToNavigationLogin(): NavDirections =
        ActionOnlyNavDirections(R.id.action_navigation_register_to_navigation_login)
  }
}
