package com.example.learning_android_goodsfinder_kulakov

import android.app.Application
import com.example.learning_android_goodsfinder_kulakov.ui.Utils

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        val file = Utils.getItemsFile(this)
        if (!file.exists())
            file.createNewFile()
    }

}