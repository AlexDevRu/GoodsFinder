package com.example.learning_android_goodsfinder_kulakov.ui

import android.content.Context
import com.example.learning_android_goodsfinder_kulakov.models.Good
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object Utils {

    fun getItems(context: Context, query: String? = null) : List<Good> {
        val normalizedQuery = query.orEmpty().trim().lowercase()
        val file = getItemsFile(context)
        val json = file.readText()
        if (json.isBlank()) return emptyList()
        val goods = Gson().fromJson<List<Good>>(json, object : TypeToken<List<Good>>() {}.type)
        return goods.filter { it.name.trim().lowercase().contains(normalizedQuery) }
    }

    fun getItemById(context: Context, id: String) : Good? {
        val items = getItems(context)
        return items.firstOrNull { it.id == id }
    }

    fun saveItem(context: Context, good: Good) {
        val goods = getItems(context).toMutableList()
        goods.add(good)
        val json = Gson().toJson(goods)
        getItemsFile(context).writeText(json)
    }

    fun editItem(context: Context, good: Good) {
        val items = getItems(context)
        val newItems = items.map {
            if (it.id == good.id) good
            else it
        }
        val json = Gson().toJson(newItems)
        getItemsFile(context).writeText(json)
    }

    fun deleteItem(context: Context, good: Good) {
        val goods = getItems(context).toMutableList()
        goods.remove(good)
        val json = Gson().toJson(goods)
        getItemsFile(context).writeText(json)
    }

    fun getItemsFile(context: Context) = File(context.filesDir, "items.json")

    fun formatDate(date: Long) : String {
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return simpleDateFormat.format(date)
    }

}