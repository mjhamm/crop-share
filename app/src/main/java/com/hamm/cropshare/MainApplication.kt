package com.hamm.cropshare

import android.app.Application
import com.hamm.cropshare.data.DataCache

val prefs: DataCache by lazy {
    MainApplication.prefs!!
}

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