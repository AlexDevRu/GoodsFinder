package com.example.learning_android_goodsfinder_kulakov.ui

import android.content.Context
import com.example.learning_android_goodsfinder_kulakov.models.Good
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object Utils {

    fun getItems(context: Context) : List<Good> {
        val file = getItemsFile(context)
        val json = file.readText()
        if (json.isBlank()) return emptyList()
        return Gson().fromJson(json, object : TypeToken<List<Good>>() {}.type)
    }

    fun saveItem(context: Context, good: Good) {
        val goods = getItems(context).toMutableList()
        goods.add(good)
        val json = Gson().toJson(goods)
        getItemsFile(context).writeText(json)
    }

    fun getItemsFile(context: Context) = File(context.filesDir, "items.json")

}