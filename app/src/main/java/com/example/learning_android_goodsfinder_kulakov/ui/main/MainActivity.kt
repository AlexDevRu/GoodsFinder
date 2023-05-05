package com.example.learning_android_goodsfinder_kulakov.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.learning_android_goodsfinder_kulakov.R
import com.example.learning_android_goodsfinder_kulakov.databinding.ActivityMainBinding
import com.example.learning_android_goodsfinder_kulakov.models.Good
import com.example.learning_android_goodsfinder_kulakov.ui.adapters.GoodAdapter
import com.example.learning_android_goodsfinder_kulakov.ui.add_good.AddGoodActivity

class MainActivity : AppCompatActivity(), GoodAdapter.Listener {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel>()

    private val goodAdapter = GoodAdapter(this)

    private val addGoodResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK)
            viewModel.getItems()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvItems.setHasFixedSize(true)

        binding.rvItems.adapter = goodAdapter

        observe()
    }

    private fun observe() {
        viewModel.goods.observe(this) {
            goodAdapter.submitList(it)
        }
    }

    override fun onItemView(good: Good) {
        openAddGoodActivity(good.id, false)
    }

    override fun onItemEdit(good: Good) {
        openAddGoodActivity(good.id, true)
    }

    override fun onItemDelete(good: Good) {
        viewModel.deleteItem(good)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add -> {
                openAddGoodActivity()
                true
            }
            else -> false
        }
    }

    private fun openAddGoodActivity(id: String = "", edit: Boolean = false) {
        val intent = AddGoodActivity.getIntent(this, id, edit)
        addGoodResultLauncher.launch(intent)
    }
}