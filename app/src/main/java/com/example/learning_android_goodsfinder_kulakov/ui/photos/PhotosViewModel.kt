package com.example.learning_android_goodsfinder_kulakov.ui.photos

import android.app.Application
import androidx.lifecycle.*
import com.example.learning_android_goodsfinder_kulakov.models.SortOrder
import com.example.learning_android_goodsfinder_kulakov.ui.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PhotosViewModel(
    private val app: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(app) {

    private val images = mutableListOf<String?>()

    private val _selectedIndex = MutableLiveData(0)
    val selectedIndex : LiveData<Int> = _selectedIndex

    val selectedImage = selectedIndex.map {
        if (it < images.size)
            images[it]
        else
            null
    }

    fun isNotFirst() = selectedIndex.value!! > 0
    fun isNotLast() = selectedIndex.value!! < images.size - 1

    fun goNext() {
        if (isNotLast())
            _selectedIndex.value = selectedIndex.value!! + 1
    }

    fun goPrev() {
        if (isNotFirst())
            _selectedIndex.value = selectedIndex.value!! - 1
    }

    init {
        val id = savedStateHandle.get<String>(PhotosFragment.ID)
        val sort = savedStateHandle.get<String>(PhotosFragment.SORT)!!
        val query = savedStateHandle.get<String>(PhotosFragment.SEARCH_QUERY)
        viewModelScope.launch(Dispatchers.IO) {
            val items = Utils.getItems(app, query, SortOrder.valueOf(sort))
            items.forEach { images.add(it.photo) }
            _selectedIndex.postValue(items.indexOfFirst { it.id == id })
        }
    }

}