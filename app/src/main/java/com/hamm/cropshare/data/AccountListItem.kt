package com.hamm.cropshare.data

import com.hamm.cropshare.R

enum class AccountListItem(
    val itemImage: Int,
    val itemTitle: String
) {
    ACCOUNT_SETTINGS(R.drawable.baseline_person_outline_24, "Account Settings"),
    LOGOUT(R.drawable.baseline_logout_24, "Logout"),
    DELETE_ACCOUNT(R.drawable.baseline_delete_outline_24, "Delete Account")
}