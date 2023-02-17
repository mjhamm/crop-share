package com.hamm.cropshare.listeners

import com.hamm.cropshare.data.StoreItem

interface StoreItemClickListener {

    fun storeItemClicked(storeItem: StoreItem, position: Int)
}