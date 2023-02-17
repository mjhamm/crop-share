package com.hamm.cropshare.data

import java.util.*

data class StoreItem(
    var itemName: String?,
    var itemSellAmount: SellAmount?,
    var itemPrice: Double?
) {
    fun getPrice(): String {
        val correctCents = String.format(Locale.US, "%.2f", itemPrice)
        return "$${correctCents}"
    }
}