package com.example.learning_android_goodsfinder_kulakov.ui.add_good

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.learning_android_goodsfinder_kulakov.models.Good
import com.example.learning_android_goodsfinder_kulakov.ui.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddGoodViewModel(private val app: Application): AndroidViewModel(app) {

    private val _imageUri = MutableLiveData<Uri>()
    val imageUri : LiveData<Uri> = _imageUri

    private val _whenFound = MutableLiveData(System.currentTimeMillis())
    val whenFound : LiveData<Long> = _whenFound

    private val _loading = MutableLiveData(false)
    val loading : LiveData<Boolean> = _loading

    private val _finish = MutableSharedFlow<Unit>()
    val finish = _finish.asSharedFlow()

    fun saveImage(uri: Uri) {
        _imageUri.value = uri
    }

    fun saveWhenFound(whenFound: Long) {
        _whenFound.value = whenFound
    }

    fun save(name: String?, description: String?, whereFound: String?, whoFound: String?, whereTake: String?) {
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

}