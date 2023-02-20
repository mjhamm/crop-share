package com.hamm.cropshare.data

import com.google.firebase.firestore.PropertyName

data class Location(
    @PropertyName("streetAddress")
    var streetAddress: String,
    @PropertyName("zipCode")
    var zipCode: String
) {
    private fun validateZipCode(zipCode: String): Boolean {
        if (zipCode.length != 5) {
            return try {
                zipCode.toLong()
                true
            } catch (exception: Exception) {
                false
            }
        }
        return false
    }

    fun getLocationForMap(zipCode: String): String? {
        return if (validateZipCode(zipCode)) {
            "$streetAddress $zipCode"
        } else {
            null
        }
    }
}