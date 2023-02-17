package com.hamm.cropshare.ui.login

import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavDirections
import com.hamm.cropshare.R

public class LoginFragmentDirections private constructor() {
  public companion object {
    public fun actionNavigationLoginToNavigationRegister(): NavDirections =
        ActionOnlyNavDirections(R.id.action_navigation_login_to_navigation_register)
  }
}
