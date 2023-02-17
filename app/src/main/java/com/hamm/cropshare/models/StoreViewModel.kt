package com.hamm.cropshare.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hamm.cropshare.data.SellAmount
import com.hamm.cropshare.data.StoreItem
import com.hamm.cropshare.helpers.FirebaseHelper

class StoreViewModel : ViewModel() {

    private var items = mutableListOf<StoreItem>()

    private var _storeName = MutableLiveData<String>()
    val storeName: LiveData<String>
        get() = _storeName

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

    fun createNewStore(userId: String) {
        FirebaseHelper().fireStoreDatabase.collection("stores")
            .document(userId)
    }

}