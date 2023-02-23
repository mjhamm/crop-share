package com.hamm.cropshare

import android.app.Application
import com.hamm.cropshare.data.DataCache

val prefs: DataCache by lazy {
    MainApplication.prefs!!
}

/**
 * Our main application that is specified in the manifest
 *
 * Here we set up our [DataCache] which holds a reference to our SharedPreferences
 *
 * Inside we keep user information that is easier to retrieve than sending out to Firebase
 * for a query
 */
class MainApplication: Application() {

    companion object {
        var prefs: DataCache? = null
        lateinit var instance: MainApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        prefs = DataCache(applicationContext)
    }

}