package com.hamm.cropshare.listeners

import com.hamm.cropshare.data.AccountListItem

interface AccountListItemListener {

    fun onAccountListItemClicked(accountItem: AccountListItem, position: Int)
}
