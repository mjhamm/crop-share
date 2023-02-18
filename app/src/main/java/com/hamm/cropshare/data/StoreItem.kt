package com.hamm.cropshare.data

import com.google.firebase.firestore.PropertyName
import java.util.*

data class StoreItem(
    @PropertyName("name")
    var itemName: String?,
    @PropertyName("quantity type")
    var itemQuantityType: String?,
    @PropertyName("price")
    var itemPrice: Double?
) {
    fun getPrice(): String {
        val correctCents = String.format(Locale.US, "%.2f", itemPrice)
        return "$${correctCents}"
    }
}