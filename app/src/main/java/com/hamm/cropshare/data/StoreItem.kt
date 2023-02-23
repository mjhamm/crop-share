package com.hamm.cropshare.data

import com.google.firebase.firestore.PropertyName
import com.hamm.cropshare.extensions.convertToCurrency
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

data class StoreItem(
    @PropertyName("itemName")
    var itemName: String?,
    @PropertyName("itemQuantityType")
    var itemQuantityType: String?,
    @PropertyName("itemPrice")
    var itemPrice: Long?
) {
    fun getPrice(): String {
        val dec = itemPrice?.let { BigDecimal(it) }
        dec?.setScale(2, RoundingMode.FLOOR)
        return "$${String.format(Locale.US, "%.2f", dec)}"
    }
}