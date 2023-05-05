package com.example.learning_android_goodsfinder_kulakov.ui.add_good

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.example.learning_android_goodsfinder_kulakov.models.Good
import com.example.learning_android_goodsfinder_kulakov.ui.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddGoodViewModel(
    private val app: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(app) {

    private val _imageUri = MutableLiveData<Uri>()
    val imageUri : LiveData<Uri> = _imageUri

    private val _whenFound = MutableLiveData(System.currentTimeMillis())
    val whenFound : LiveData<Long> = _whenFound

    private val _loading = MutableLiveData(false)
    val loading : LiveData<Boolean> = _loading

    private val _finish = MutableSharedFlow<Unit>()
    val finish = _finish.asSharedFlow()

    private val goodId = savedStateHandle.get<String>(AddGoodActivity.ID)

    private val _good = MutableLiveData<Good>()
    val good : LiveData<Good> = _good

    init {
        goodId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                _loading.postValue(true)
                val good = Utils.getItemById(app, it)
                _good.postValue(good)
                _imageUri.postValue(Uri.parse(good?.photo))
                _whenFound.postValue(good?.whenFound)
                _loading.postValue(false)
            }
        }
    }

    fun saveImage(uri: Uri) {
        _imageUri.value = uri
    }

    fun saveWhenFound(whenFound: Long) {
        _whenFound.value = whenFound
    }

    fun save(name: String?, description: String?, whereFound: String?, whoFound: String?, whereTake: String?) {
        if (goodId.isNullOrBlank())
            add(name, description, whereFound, whoFound, whereTake)
        else
            edit(name, description, whereFound, whoFound, whereTake)
    }

    private fun add(name: String?, description: String?, whereFound: String?, whoFound: String?, whereTake: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            val good = Good(
                id = UUID.randomUUID().toString(),
                name = name.orEmpty(),
                description = description.orEmpty(),
                whereFound = whereFound.orEmpty(),
                whenFound = whenFound.value ?: 0,
                whoFound = whoFound.orEmpty(),
                whereTake = whereTake.orEmpty(),
                photo = imageUri.value?.toString()
            )
            Utils.saveItem(app, good)
            _loading.postValue(false)
            _finish.emit(Unit)
        }
    }

    private fun edit(name: String?, description: String?, whereFound: String?, whoFound: String?, whereTake: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            val good = Good(
                id = goodId.orEmpty(),
                name = name.orEmpty(),
                description = description.orEmpty(),
                whereFound = whereFound.orEmpty(),
                whenFound = whenFound.value ?: good.value!!.whenFound,
                whoFound = whoFound.orEmpty(),
                whereTake = whereTake.orEmpty(),
                photo = imageUri.value?.toString() ?: good.value!!.photo
            )
            Utils.editItem(app, good)
            _loading.postValue(false)
            _finish.emit(Unit)
        }
    }

}