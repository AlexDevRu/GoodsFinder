package com.example.learning_android_goodsfinder_kulakov.ui.main

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.OnBackStackChangedListener
import com.example.learning_android_goodsfinder_kulakov.R
import com.example.learning_android_goodsfinder_kulakov.databinding.ActivityMainBinding
import com.example.learning_android_goodsfinder_kulakov.ui.good_list.GoodListFragment

class MainActivity : AppCompatActivity(), OnBackStackChangedListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentsSize = supportFragmentManager.fragments.size
        if (fragmentsSize == 0)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerLeft, GoodListFragment())
                .commit()

        supportFragmentManager.addOnBackStackChangedListener(this)
    }

    override fun onBackStackChanged() {
        val backStackEntryCount = supportFragmentManager.backStackEntryCount
        supportActionBar?.setDisplayHomeAsUpEnabled(backStackEntryCount > 0)
    }

    private fun isLandscapeOrTablet() = binding.fragmentContainerRight != null

    fun setFragment(fragment: Fragment) {
        val fragmentContainerId = if (isLandscapeOrTablet()) R.id.fragmentContainerRight else R.id.fragmentContainerLeft
        supportFragmentManager.beginTransaction()
            .replace(fragmentContainerId, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            supportFragmentManager.popBackStack()
            return true
        }
        return false
    }
}
