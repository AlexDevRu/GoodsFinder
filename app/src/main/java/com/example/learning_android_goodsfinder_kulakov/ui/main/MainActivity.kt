package com.example.learning_android_goodsfinder_kulakov.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.learning_android_goodsfinder_kulakov.R
import com.example.learning_android_goodsfinder_kulakov.databinding.ActivityMainBinding
import com.example.learning_android_goodsfinder_kulakov.ui.good_list.GoodListFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerLeft, GoodListFragment())
            .commit()
    }

    fun isLandscapeOrTablet() = binding.fragmentContainerRight != null
}
