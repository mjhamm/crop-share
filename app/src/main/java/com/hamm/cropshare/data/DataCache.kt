package com.hamm.cropshare.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.hamm.cropshare.data.Constants.Companion.USER_ZIPCODE_DEFAULT_VALUE

class DataCache(context: Context) {

    private val USER_ZIPCODE = "user_zipcode"
    private val USER_UID = "user_uid"

    private val privatePreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun clearPreferences() {
        privatePreferences.edit().putString(USER_ZIPCODE, null).apply()
        privatePreferences.edit().putString(USER_UID, null).apply()
    }

    var zipCodePref: String?
        get() = privatePreferences.getString(USER_ZIPCODE, USER_ZIPCODE_DEFAULT_VALUE)
        set(value) = privatePreferences.edit().putString(USER_ZIPCODE, value).apply()

    var userUidPref: String?
        get() = privatePreferences.getString(USER_UID, "")
        set(value) = privatePreferences.edit().putString(USER_UID, value).apply()

}