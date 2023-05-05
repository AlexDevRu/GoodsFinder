package com.example.learning_android_goodsfinder_kulakov.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.learning_android_goodsfinder_kulakov.models.Good
import com.example.learning_android_goodsfinder_kulakov.ui.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val app: Application): AndroidViewModel(app) {

    private val _goods = MutableLiveData<List<Good>>()
    val goods : LiveData<List<Good>> = _goods

    init {
        getItems()
    }

    fun getItems() {
        viewModelScope.launch(Dispatchers.IO) {
            val goods = Utils.getItems(app)
            _goods.postValue(goods)
        }
    }

    fun deleteItem(good: Good) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.deleteItem(app, good)
            getItems()
        }
    }
}