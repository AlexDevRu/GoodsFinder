package com.example.learning_android_goodsfinder_kulakov.ui.add_good

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.learning_android_goodsfinder_kulakov.R
import com.example.learning_android_goodsfinder_kulakov.databinding.ActivityAddGoodBinding
import com.example.learning_android_goodsfinder_kulakov.ui.Extensions.disable
import com.example.learning_android_goodsfinder_kulakov.ui.Extensions.stringText
import com.example.learning_android_goodsfinder_kulakov.ui.Utils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddGoodFragment : Fragment(), View.OnClickListener, DialogInterface.OnClickListener, DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityAddGoodBinding

    private val viewModel by viewModels<AddGoodViewModel>()

    private lateinit var uri: Uri

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            Glide.with(binding.ivPhoto)
                .load(uri)
                .into(binding.ivPhoto)
            viewModel.saveImage(uri)
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == AppCompatActivity.RESULT_OK) {
            val uri = it.data?.data ?: return@registerForActivityResult
            Glide.with(binding.ivPhoto)
                .load(uri)
                .into(binding.ivPhoto)
            viewModel.saveImage(uri)
        }
    }

    private var editMode = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityAddGoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editMode = requireArguments().getBoolean(EDIT, false) && !requireArguments().getString(ID).isNullOrBlank()
        val addMode = requireArguments().getString(ID).isNullOrBlank()

        binding.btnSave.isVisible = editMode || addMode

        if (!editMode && !addMode) {
            binding.etName.disable()
            binding.etDescription.disable()
            binding.etWhereFound.disable()
            binding.etWhoFound.disable()
            binding.etWhereTake.disable()
        }

        if (editMode || addMode) {
            binding.btnSave.setOnClickListener(this)
            binding.ivPhoto.setOnClickListener(this)
            binding.etDate.setOnClickListener(this)
        }

        observe()
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.finish.collectLatest {
                    setFragmentResult(REQUEST_KEY, bundleOf())
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
        viewModel.imageUri.observe(viewLifecycleOwner) {
            Glide.with(binding.ivPhoto)
                .load(it)
                .error(R.drawable.image_black_24dp)
                .into(binding.ivPhoto)
        }
        viewModel.whenFound.observe(viewLifecycleOwner) {
            binding.etDate.setText(Utils.formatDate(it))
        }
        viewModel.good.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.etName.setText(it.name)
                binding.etDescription.setText(it.description)
                binding.etWhereFound.setText(it.whereFound)
                binding.etWhoFound.setText(it.whoFound)
                binding.etWhereTake.setText(it.whereTake)
            }
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnSave -> {
                if (editMode)
                    AlertDialog.Builder(requireContext())
                        .setTitle(R.string.edit)
                        .setMessage(R.string.edit_confirmation)
                        .setPositiveButton(android.R.string.ok) { _, _ -> save() }
                        .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.dismiss() }
                        .show()
                else
                    save()
            }
            binding.ivPhoto -> {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.select_photo)
                    .setItems(R.array.select_photo, this)
                    .show()
            }
            binding.etDate -> {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = viewModel.whenFound.value ?: System.currentTimeMillis()

                DatePickerDialog(requireContext(), this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
    }

    override fun onDateSet(p0: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        viewModel.saveWhenFound(calendar.timeInMillis)
    }

    override fun onClick(p0: DialogInterface?, position: Int) {
        when (position) {
            0 -> openCamera()
            1 -> openGallery()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(System.currentTimeMillis())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_${timeStamp}_",".jpg", storageDir)
        uri = FileProvider.getUriForFile(requireContext(), "com.example.learning_android_goodsfinder_kulakov.fileprovider", file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        cameraLauncher.launch(intent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun save() {
        val name = binding.etName.stringText()
        val description = binding.etDescription.stringText()
        val whereFound = binding.etWhereFound.stringText()
        val whoFound = binding.etWhoFound.stringText()
        val whereTake = binding.etWhereTake.stringText()
        viewModel.save(name, description, whereFound, whoFound, whereTake)
    }

    companion object {
        const val REQUEST_KEY = "AddGoodFragment"
        const val ID = "ID"
        private const val EDIT = "EDIT"

        fun createInstance(id: String? = null, edit: Boolean = false) : AddGoodFragment {
            val fragment = AddGoodFragment()
            fragment.arguments = bundleOf(
                ID to id,
                EDIT to edit
            )
            return fragment
        }
    }
}