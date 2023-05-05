package com.example.learning_android_goodsfinder_kulakov.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import com.example.learning_android_goodsfinder_kulakov.R
import com.example.learning_android_goodsfinder_kulakov.databinding.ActivityMainBinding
import com.example.learning_android_goodsfinder_kulakov.models.Good
import com.example.learning_android_goodsfinder_kulakov.ui.adapters.GoodAdapter
import com.example.learning_android_goodsfinder_kulakov.ui.add_good.AddGoodActivity

class MainActivity : AppCompatActivity(), GoodAdapter.Listener, SearchView.OnQueryTextListener {

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
        AlertDialog.Builder(this)
            .setTitle(R.string.delete)
            .setMessage(R.string.delete_confirmation)
            .setPositiveButton(android.R.string.ok) { _, _ -> viewModel.deleteItem(good) }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchView = menu?.findItem(R.id.search)?.actionView as? SearchView
        searchView?.setOnQueryTextListener(this)
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

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.getItems(newText)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false
}