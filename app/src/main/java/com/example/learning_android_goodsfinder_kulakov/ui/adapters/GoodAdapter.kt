package com.example.learning_android_goodsfinder_kulakov.ui.adapters

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.learning_android_goodsfinder_kulakov.R
import com.example.learning_android_goodsfinder_kulakov.databinding.ListItemGoodBinding
import com.example.learning_android_goodsfinder_kulakov.models.Good
import com.example.learning_android_goodsfinder_kulakov.ui.Utils

class GoodAdapter(
    private val listener: Listener
): ListAdapter<Good, GoodAdapter.ContactViewHolder>(DIFF_UTIL) {

    companion object {
        private val DIFF_UTIL = object : ItemCallback<Good>() {
            override fun areItemsTheSame(oldItem: Good, newItem: Good): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Good, newItem: Good): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface Listener {
        fun onItemView(good: Good)
        fun onItemEdit(good: Good)
        fun onItemDelete(good: Good)
        fun onOpenPhoto(good: Good)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ListItemGoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ContactViewHolder(
        private val binding: ListItemGoodBinding
    ) : RecyclerView.ViewHolder(binding.root), PopupMenu.OnMenuItemClickListener, View.OnClickListener {

        private var good: Good? = null

        private val popupMenu = PopupMenu(binding.root.context, binding.btnMore)

        init {
            popupMenu.inflate(R.menu.popup_good)
            popupMenu.setOnMenuItemClickListener(this)
            binding.btnMore.setOnClickListener(this)
            binding.root.setOnClickListener(this)
        }

        fun bind(good: Good) {
            this.good = good
            binding.tvName.text = good.name
            binding.tvWhenFound.text = Utils.formatDate(good.whenFound)
            Glide.with(binding.ivPhoto)
                .load(good.photo)
                .error(R.drawable.image_black_24dp)
                .into(binding.ivPhoto)
        }

        override fun onClick(view: View?) {
            when (view) {
                binding.btnMore -> popupMenu.show()
                binding.root -> good?.let { listener.onItemView(it) }
            }
        }

        override fun onMenuItemClick(item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.view -> {
                    good?.let { listener.onItemView(it) }
                    true
                }
                R.id.edit -> {
                    good?.let { listener.onItemEdit(it) }
                    true
                }
                R.id.delete -> {
                    good?.let { listener.onItemDelete(it) }
                    true
                }
                R.id.openPhoto -> {
                    good?.let { listener.onOpenPhoto(it) }
                    true
                }
                else -> false
            }
        }

    }
}