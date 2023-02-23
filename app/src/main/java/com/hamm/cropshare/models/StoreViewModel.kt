package com.hamm.cropshare.models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.hamm.cropshare.data.Location
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

    fun addNewStoreItem(currentItems: List<StoreItem>) {
        prefs.userUidPref?.let { uid ->
            FirebaseHelper().fireStoreDatabase.collection("users")
                .document(uid)
                .update("store.storeItems", currentItems)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        try {
                            _items.value = currentItems
                        } catch (exception: Exception) {
                            Log.d("StoreViewModel", exception.toString())
                        }
                    }
                }
        }
    }
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
        prefs.userStoreZipCodePref?.let {
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
                .get()
                .addOnCompleteListener {
                    try {
                        val store = it.result?.get("store") as Map<*, *>
                        val storeName = store["storeName"] as String
                        val storeItems = store["storeItems"] as ArrayList<Map<*, *>>
                        val items = mutableListOf<StoreItem>()
                        for (item in storeItems) {
                            val price: Long = try {
                                item["itemPrice"] as Long
                            } catch (_: Exception) {
                                val priceAsDouble = item["itemPrice"] as Double
                                priceAsDouble.toLong()
                            }
                            val storeItem = StoreItem(
                                item["itemName"] as String,
                                item["itemQuantityType"] as String,
                                price
                            )
                            items.add(storeItem)
                        }
                        _store.value = Store(storeName, items)
                        _items.value = items
                    } catch (exception: Exception) {
                        Log.d("StoreViewModel", exception.toString())
                    }
                }
        }
    }

}