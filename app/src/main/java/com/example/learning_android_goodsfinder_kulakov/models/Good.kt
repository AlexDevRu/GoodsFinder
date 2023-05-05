package com.example.learning_android_goodsfinder_kulakov.models

data class Good(
    val id: String,
    val name: String,
    val description: String,
    val whereFound: String,
    val whenFound: Long,
    val whoFound: String,
    val whereTake: String,
    val photo: String?
)
