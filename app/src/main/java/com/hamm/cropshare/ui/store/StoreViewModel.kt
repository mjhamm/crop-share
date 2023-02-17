package com.hamm.cropshare.ui.store

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hamm.cropshare.data.SellAmount
import com.hamm.cropshare.data.StoreItem

class StoreViewModel : ViewModel() {

    private var items = mutableListOf<StoreItem>()

    fun getMyStoreItems() = items

    fun addNewStoreItem(storeItem: StoreItem) {
        items.add(storeItem)
    }

    fun removeStoreItem(position: Int) {
        items.removeAt(position)
    }

    fun updateStoreItem(position: Int, storeItem: StoreItem) {
        with(items[position]) {
            itemName = storeItem.itemName
            itemPrice = storeItem.itemPrice
            itemSellAmount = storeItem.itemSellAmount
        }
    }


}