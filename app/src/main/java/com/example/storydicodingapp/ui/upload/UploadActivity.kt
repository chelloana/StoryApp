package com.example.storydicodingapp.ui.upload

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.core.widget.addTextChangedListener
import android.graphics.BitmapFactory
import android.graphics.Matrix
import com.example.storydicodingapp.utils.Result
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.exifinterface.media.ExifInterface
import com.example.storydicodingapp.data.remote.ApiConfig
import com.example.storydicodingapp.databinding.ActivityUploadBinding
import com.example.storydicodingapp.ui.main.MainActivity.Companion.KEY_TOKEN
import com.example.storydicodingapp.utils.Event
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding

    private val savedToken by lazy { intent.getStringExtra(KEY_TOKEN) }
    private val uploadViewModel by viewModels<UploadViewModel> {
        UploadViewModelFactory.getInstance(
            ApiConfig.getApiService(savedToken),
        )
    }

    private var pathImg: String = ""

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val image = result.data?.data as Uri
            image.let { uri ->
                uploadViewModel.imageFile.postValue(uriToFile(uri))
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val file = File(pathImg)
            file.let { image ->
                val bitmap = BitmapFactory.decodeFile(image.path)
                rotateImage(bitmap, pathImg).compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    FileOutputStream(image)
                )
                uploadViewModel.imageFile.postValue(image)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        observeViewModel()
        setListeners()
    }

    private fun observeViewModel() {
        uploadViewModel.apply {
            canUpload.observe(this@UploadActivity) {
                binding.btnUpload.isEnabled = it
            }

            imageFile.observe(this@UploadActivity) {
                if (it != null) {
                    binding.ivStoryImage.setImageBitmap(BitmapFactory.decodeFile(it.path))
                }
            }

            isLoading.observe(this@UploadActivity) {
                if (it != null) {
                    showLoading(it)
                }
            }

            errorText.observe(this@UploadActivity) { event ->
                if (event != null) {
                    event.getContentIfNotHandled()?.let { message ->
                        showToast(message)
                    }
                }
            }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun setListeners() {
        binding.apply {
            toolbar.setNavigationOnClickListener { finish() }

            edDesc.addTextChangedListener(onTextChanged = { desc, _, _, _ ->
                uploadViewModel.descText.postValue(desc.toString())
            })

            btnCamera.setOnClickListener {
                if (checkImagePermission()) {
                    captureImg()
                } else {
                    ActivityCompat.requestPermissions(
                        this@UploadActivity,
                        REQUIRED_CAMERA_PERMISSION,
                        REQUEST_CODE_PERMISSIONS
                    )

                    if (checkImagePermission()) {
                        captureImg()
                    }
                }
            }

            btnGallery.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                val chooser = Intent.createChooser(intent, "Choose a Picture")
                galleryLauncher.launch(chooser)
            }

            btnUpload.setOnClickListener {
                uploadViewModel.upload(
                    reduceFile(uploadViewModel.imageFile.value!!),
                    uploadViewModel.descText.value!!
                )
                    .observe(this@UploadActivity) { result ->
                        when (result) {
                            is Result.Loading -> {
                                uploadViewModel.isLoading.postValue(true)
                            }

                            is Result.Success -> {
                                uploadViewModel.isLoading.postValue(false)
                                showToast(result.data.message.toString())

                                val intent = Intent()
                                setResult(RESULT_OK, intent)
                                finish()
                            }

                            is Result.Error -> {
                                uploadViewModel.isLoading.postValue(false)
                                uploadViewModel.errorText.postValue(Event(result.error))
                            }
                        }
                    }
            }
        }
    }

    private fun captureImg() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val customTempFile = File.createTempFile(
            SimpleDateFormat(
                "dd-MMM-yyyy",
                Locale.US
            ).format(System.currentTimeMillis()), ".jpg", storageDir
        )
        customTempFile.also {
            pathImg = it.absolutePath
            intent.putExtra(
                MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(
                    this@UploadActivity,
                    "com.example.storydicodingapp",
                    it
                )
            )
            cameraLauncher.launch(intent)
        }
    }

    private fun rotateImage(bitmap: Bitmap, path: String): Bitmap {
        val orientation = ExifInterface(path).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
        }

        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    private fun uriToFile(uri: Uri): File {
        val myFile = File.createTempFile(
            SimpleDateFormat(
                "dd-MMM-yyyy",
                Locale.US
            ).format(System.currentTimeMillis()),
            ".jpg",
            getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )

        val inputStream = contentResolver.openInputStream(uri) as InputStream
        val outputStream: OutputStream = FileOutputStream(myFile)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()

        return myFile
    }

    private fun checkImagePermission() = REQUIRED_CAMERA_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun reduceFile(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLength: Int

        do {
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLength = bmpPicByteArray.size
            compressQuality -= 5
        } while (streamLength > 1000000)

        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
        return file
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            progressbar.isVisible = isLoading
            btnUpload.isVisible = !isLoading
            btnGallery.isEnabled = !isLoading
            btnCamera.isEnabled = !isLoading
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val REQUIRED_CAMERA_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 100
    }
}