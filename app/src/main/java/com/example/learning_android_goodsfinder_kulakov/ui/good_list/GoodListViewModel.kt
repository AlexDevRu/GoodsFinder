package com.example.learning_android_goodsfinder_kulakov.ui.good_list

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

class GoodListViewModel(private val app: Application): AndroidViewModel(app) {

    private val _goods = MutableLiveData<List<Good>>()
    val goods : LiveData<List<Good>> = _goods

    var query: String? = null
        private set

    var sort = SortOrder.DESC
        set(value) {
            field = value
            getItems(query, field)
        }

    init {
        getItems(query, sort)
    }

    fun getItems(query: String? = this.query, sortOrder: SortOrder = sort) {
        this.query = query
        viewModelScope.launch(Dispatchers.IO) {
            val goods = Utils.getItems(app, query, sortOrder)
            _goods.postValue(goods)
        }
    }

    fun deleteItem(good: Good) {
        viewModelScope.launch(Dispatchers.IO) {
            Utils.deleteItem(app, good)
            getItems(query, sort)
        }
    }
}