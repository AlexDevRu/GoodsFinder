package com.example.learning_android_goodsfinder_kulakov.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.learning_android_goodsfinder_kulakov.models.Good

class MainViewModel(private val app: Application): AndroidViewModel(app) {

    private val _goods = MutableLiveData<List<Good>>()
    val goods : LiveData<List<Good>> = _goods

}