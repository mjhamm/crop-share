package com.hamm.cropshare.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.hamm.cropshare.data.Constants.Companion.USER_ZIPCODE_DEFAULT_VALUE

class DataCache(context: Context) {

    private val USER_STORE_ADDRESS = "user_store_address"
    private val USER_STORE_ZIP_CODE = "user_store_zip_code"
    private val USER_UID = "user_uid"

    private val privatePreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun clearPreferences() {
        privatePreferences.edit().putString(USER_STORE_ADDRESS, null).apply()
        privatePreferences.edit().putString(USER_STORE_ZIP_CODE, null).apply()
        privatePreferences.edit().putString(USER_UID, null).apply()
    }

    var userStoreAddressPref: String?
        get() = privatePreferences.getString(USER_STORE_ADDRESS, null)
        set(value) = privatePreferences.edit().putString(USER_STORE_ADDRESS, value).apply()

    var userStoreZipCodePref: String?
        get() = privatePreferences.getString(USER_STORE_ZIP_CODE, null)
        set(value) = privatePreferences.edit().putString(USER_STORE_ZIP_CODE, value).apply()

    var userUidPref: String?
        get() = privatePreferences.getString(USER_UID, null)
        set(value) = privatePreferences.edit().putString(USER_UID, value).apply()

}