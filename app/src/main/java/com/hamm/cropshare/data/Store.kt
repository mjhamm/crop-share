package com.hamm.cropshare.data

import com.google.firebase.firestore.PropertyName

class Store(
    @PropertyName("storeName")
    var storeName: String = "",
    @PropertyName("storeItems")
    var storeItems: List<StoreItem> = emptyList(),
    @PropertyName("location")
    var location: Location = Location("", "")
)