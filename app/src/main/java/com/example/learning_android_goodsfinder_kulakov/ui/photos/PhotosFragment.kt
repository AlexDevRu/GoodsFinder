package com.example.learning_android_goodsfinder_kulakov.ui.photos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.learning_android_goodsfinder_kulakov.R
import com.example.learning_android_goodsfinder_kulakov.databinding.FragmentPhotosBinding
import com.example.learning_android_goodsfinder_kulakov.models.SortOrder

class PhotosFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentPhotosBinding

    private val viewModel by viewModels<PhotosViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhotosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnPrev.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
        observe()
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnPrev -> viewModel.goPrev()
            binding.btnNext -> viewModel.goNext()
        }
    }

    private fun observe() {
        viewModel.selectedIndex.observe(viewLifecycleOwner) {
            binding.btnPrev.isVisible = viewModel.isNotFirst()
            binding.btnNext.isVisible = viewModel.isNotLast()
        }
        viewModel.selectedImage.observe(viewLifecycleOwner) {
            if (it.isNullOrBlank())
                binding.imageView.setImageResource(R.drawable.image_black_24dp)
            else
                Glide.with(binding.imageView)
                    .load(it)
                    .into(binding.imageView)
        }
    }

    companion object {
        const val ID = "ID"
        const val SORT = "SORT"
        const val SEARCH_QUERY = "SEARCH_QUERY"

        fun createInstance(id: String, sort: SortOrder, searchQuery: String?) : PhotosFragment {
            val fragment = PhotosFragment()
            fragment.arguments = bundleOf(
                ID to id, SORT to sort.name, SEARCH_QUERY to searchQuery
            )
            return fragment
        }
    }

}