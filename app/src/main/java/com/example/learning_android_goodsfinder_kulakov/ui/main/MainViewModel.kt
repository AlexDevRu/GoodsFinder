package com.example.learning_android_goodsfinder_kulakov.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.learning_android_goodsfinder_kulakov.models.Good
import com.example.learning_android_goodsfinder_kulakov.models.SortOrder
import com.example.learning_android_goodsfinder_kulakov.ui.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val app: Application): AndroidViewModel(app) {

    private val _goods = MutableLiveData<List<Good>>()
    val goods : LiveData<List<Good>> = _goods

    private val _query = MutableLiveData<String>()
    val query : LiveData<String> = _query

    var sort = SortOrder.DESC
        set(value) {
            field = value
            getItems(query.value, field)
        }

    init {
        getItems(query.value, sort)
    }

    fun getItems(query: String? = null, sortOrder: SortOrder = sort) {
        viewModelScope.launch(Dispatchers.IO) {
            val goods = Utils.getItems(app, query, sortOrder)
            _goods.postValue(goods)
        }
    }

    fun deleteItem(good: Good) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.deleteItem(app, good)
            getItems(query.value, sort)
        }
    }
}