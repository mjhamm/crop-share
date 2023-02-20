package com.hamm.cropshare.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.hamm.cropshare.data.SellAmount
import com.hamm.cropshare.data.Store
import com.hamm.cropshare.data.StoreItem
import com.hamm.cropshare.helpers.FirebaseHelper
import com.hamm.cropshare.prefs

class StoreViewModel : ViewModel() {

    private var _items = MutableLiveData<List<StoreItem>>()
    val items: LiveData<List<StoreItem>>
        get() = _items

    private var _store = MutableLiveData<Store>()
    val store: LiveData<Store>
        get() = _store

    private var _storeName = MutableLiveData<String>()
    val storeName: LiveData<String>
        get() = _storeName

    private var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

//    fun addNewStoreItem(storeItem: StoreItem) {
//        items.add(storeItem)
//    }
//
//    fun removeStoreItem(position: Int) {
//        items.removeAt(position)
//    }

//    fun updateStoreItem(position: Int, storeItem: StoreItem) {
//        with(items[position]) {
//            itemName = storeItem.itemName
//            itemPrice = storeItem.itemPrice
//            itemQuantityType = storeItem.itemQuantityType
//        }
//    }

    fun createNewStore(store: Store) {
        _isLoading.value = true
        val storeEntry = hashMapOf(
            "store" to store
        )
        prefs.zipCodePref?.let {
            FirebaseHelper().fireStoreDatabase.collection("stores")
                .document(it)
                .set(storeEntry)
                .addOnCompleteListener {
                    _store.value = store
                    _isLoading.value = false
                }
                .addOnFailureListener {
                    _isLoading.value = false
                }
        }

        prefs.userUidPref?.let {
            FirebaseHelper().fireStoreDatabase.collection("users")
                .document(it)
                .set(storeEntry, SetOptions.merge())
        }
    }

    fun updateStoreName(storeName: String) {
        FirebaseHelper().firebaseUserUID?.let { uid ->
            FirebaseHelper().fireStoreDatabase.collection("users")
                .document(uid)
        }
    }

    fun getStore() {
        FirebaseHelper().firebaseUserUID?.let { uid ->
            FirebaseHelper().fireStoreDatabase.collection("users")
                .document(uid)
                .addSnapshotListener { value, _ ->
                    //val store = value?.get("store") as Map<*, *>
                    //val name = store["storeName"] as String
//                    val storeItems = mutableListOf<StoreItem>()
//                    for (items in store["items"] as ArrayList<HashMap<*, *>>) {
//                        val itemName = items["name"] as String
//                        storeItems.add(StoreItem(itemName, "", 0.00))
//                    }
                    _store.value = Store("name", emptyList())
                    //_items.value = storeItems
                }
        }
    }

}