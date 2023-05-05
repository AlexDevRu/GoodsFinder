package com.example.learning_android_goodsfinder_kulakov.ui

import android.widget.EditText

object Extensions {

    fun EditText.stringText() = text?.toString().orEmpty()

}