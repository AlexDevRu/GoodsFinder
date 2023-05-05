package com.example.learning_android_goodsfinder_kulakov.ui.add_good

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.DatePicker
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.learning_android_goodsfinder_kulakov.R
import com.example.learning_android_goodsfinder_kulakov.databinding.ActivityAddGoodBinding
import com.example.learning_android_goodsfinder_kulakov.ui.Extensions.stringText
import com.example.learning_android_goodsfinder_kulakov.ui.Utils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AddGoodActivity : AppCompatActivity(), View.OnClickListener, DialogInterface.OnClickListener, DatePickerDialog.OnDateSetListener {

    private lateinit var binding: ActivityAddGoodBinding

    private val viewModel by viewModels<AddGoodViewModel>()

    private lateinit var uri: Uri

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            Glide.with(binding.ivPhoto)
                .load(uri)
                .into(binding.ivPhoto)
            viewModel.saveImage(uri)
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val uri = it.data?.data ?: return@registerForActivityResult
            Glide.with(binding.ivPhoto)
                .load(uri)
                .into(binding.ivPhoto)
            viewModel.saveImage(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddGoodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener(this)
        binding.ivPhoto.setOnClickListener(this)
        binding.etDate.setOnClickListener(this)

        observe()
    }

    private fun observe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.finish.collectLatest {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
        viewModel.imageUri.observe(this) {
            Glide.with(binding.ivPhoto)
                .load(it)
                .error(R.drawable.image_black_24dp)
                .into(binding.ivPhoto)
        }
        viewModel.whenFound.observe(this) {
            binding.etDate.setText(Utils.formatDate(it))
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnSave -> save()
            binding.ivPhoto -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.select_photo)
                    .setItems(R.array.select_photo, this)
                    .show()
            }
            binding.etDate -> {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = viewModel.whenFound.value ?: System.currentTimeMillis()

                DatePickerDialog(this, this,
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
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_${timeStamp}_",".jpg", storageDir)
        uri = FileProvider.getUriForFile(this, "com.example.learning_android_goodsfinder_kulakov.fileprovider", file)
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

}