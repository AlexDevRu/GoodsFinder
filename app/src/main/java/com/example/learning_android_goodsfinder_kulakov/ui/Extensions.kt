package com.example.learning_android_goodsfinder_kulakov.ui

import android.text.InputType
import android.widget.EditText

object Extensions {

    fun EditText.stringText() = text?.toString().orEmpty()

    fun EditText.disable() {
        inputType = InputType.TYPE_NULL
        isFocusable = false
    }

}