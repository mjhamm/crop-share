package com.hamm.cropshare.data

data class Store(
    var storeName: String,
    var storeItems: List<StoreItem>,
    var location: Location
)