package com.example.learning_android_goodsfinder_kulakov.ui.good_list

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.example.learning_android_goodsfinder_kulakov.R
import com.example.learning_android_goodsfinder_kulakov.databinding.FragmentGoodListBinding
import com.example.learning_android_goodsfinder_kulakov.models.Good
import com.example.learning_android_goodsfinder_kulakov.models.SortOrder
import com.example.learning_android_goodsfinder_kulakov.ui.adapters.GoodAdapter
import com.example.learning_android_goodsfinder_kulakov.ui.add_good.AddGoodFragment
import com.example.learning_android_goodsfinder_kulakov.ui.main.MainActivity
import com.example.learning_android_goodsfinder_kulakov.ui.photos.PhotosFragment

class GoodListFragment : Fragment(), GoodAdapter.Listener, MenuProvider,
    SearchView.OnQueryTextListener, DialogInterface.OnClickListener, FragmentResultListener {

    private lateinit var binding: FragmentGoodListBinding

    private val viewModel by viewModels<GoodListViewModel>()

    private val goodAdapter = GoodAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGoodListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvItems.setHasFixedSize(true)
        binding.rvItems.adapter = goodAdapter
        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
        parentFragmentManager.setFragmentResultListener(AddGoodFragment.REQUEST_KEY, this, this)
        observe()
    }

    override fun onFragmentResult(requestKey: String, result: Bundle) {
        if (requestKey == AddGoodFragment.REQUEST_KEY)
            viewModel.getItems()
    }

    override fun onItemView(good: Good) {
        openAddGoodFragment(good.id, false)
    }

    override fun onOpenPhoto(good: Good) {
        val fragment = PhotosFragment.createInstance(good.id, viewModel.sort, viewModel.query)
        (requireActivity() as MainActivity).setFragment(fragment)
    }

    override fun onItemEdit(good: Good) {
        openAddGoodFragment(good.id, true)
    }

    override fun onItemDelete(good: Good) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete)
            .setMessage(R.string.delete_confirmation)
            .setPositiveButton(android.R.string.ok) { _, _ -> viewModel.deleteItem(good) }
            .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun observe() {
        viewModel.goods.observe(viewLifecycleOwner) {
            goodAdapter.submitList(it)
        }
    }

    private fun openAddGoodFragment(id: String? = null, edit: Boolean = false) {
        val fragment = AddGoodFragment.createInstance(id, edit)
        (requireActivity() as MainActivity).setFragment(fragment)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchView = menu.findItem(R.id.search)?.actionView as? SearchView
        searchView?.setQuery(viewModel.query, false)
        searchView?.setOnQueryTextListener(this)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.add -> {
                openAddGoodFragment()
                true
            }
            R.id.sort -> {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.sort)
                    .setSingleChoiceItems(R.array.sort, viewModel.sort.ordinal, this)
                    .show()
                true
            }
            else -> false
        }
    }

    override fun onClick(dialog: DialogInterface?, position: Int) {
        viewModel.sort = SortOrder.values()[position]
        dialog?.dismiss()
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.getItems(newText)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean = false
}