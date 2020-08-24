package io.github.ameyalaad.rasoee.analyze

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.*
import io.github.ameyalaad.rasoee.network.RecipeProperty
import io.github.ameyalaad.rasoee.network.RecipePuppyApi
import io.github.ameyalaad.rasoee.utils.Classes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val MODEL_NAME = "pth_rasoee_308.pt"

enum class RecipeApiStatus {
    LOADING,
    ERROR,
    DONE
}

class AnalyzeViewModel(private val app: Application) : AndroidViewModel(app) {
    var currentPhotoPath: String? = null
    var module: Module? = null

    // Analyze button visibility
    private val _analyzeVisibility = MutableLiveData<Boolean>()
    val analyzeVisibility: LiveData<Boolean>
        get() = _analyzeVisibility


    // Creating an Live data Bitmap for the ImageView Data binding
    private val _photoBitmap = MutableLiveData<Bitmap>()
    val photoBitmap: LiveData<Bitmap>
        get() = _photoBitmap

    private val _className = MutableLiveData<String>()
    val className: LiveData<String>
        get() = _className

    private val _recipes = MutableLiveData<List<RecipeProperty>>()
    val recipes: LiveData<List<RecipeProperty>>
        get() = _recipes

    private val _status = MutableLiveData<RecipeApiStatus>()
    val status: LiveData<RecipeApiStatus>
        get() = _status

    private val _navigateToSelectedProperty = MutableLiveData<RecipeProperty>()
    val navigateToSelectedProperty: LiveData<RecipeProperty>
        get() = _navigateToSelectedProperty

    fun analyzeImage() {
        if (_photoBitmap.value == null) {
            Toast.makeText(app, "Please load an image first", Toast.LENGTH_SHORT).show()
            return
        } else {
            viewModelScope.launch {
                setupModel()
                _className.value = evaluate(_photoBitmap.value!!)
                _analyzeVisibility.value= false
                _status.value = RecipeApiStatus.LOADING
                try {
                    val res = RecipePuppyApi.retrofitService.getProperties(_className.value!!.toLowerCase())
                    _recipes.value = res.results
                    _status.value =
                        RecipeApiStatus.DONE
                } catch (e: Exception) {
                    _status.value =
                        RecipeApiStatus.ERROR
                }
            }
        }
        
    }


    fun setPhotoFromGallery(imageUri: Uri) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            _photoBitmap.value = getResizedBitmap(
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        app.contentResolver,
                        imageUri
                    )
                ).copy(Bitmap.Config.RGBA_F16, true), 299
            )
        } else {
            _photoBitmap.value = getResizedBitmap(
                MediaStore.Images.Media.getBitmap(app.contentResolver, imageUri),
                299
            )
        }
        _analyzeVisibility.value=true
        _recipes.value = null
        _className.value = null
        _status.value = RecipeApiStatus.LOADING
    }

    fun setPhoto() {
        // get Orientation to view correctly in ImageView
        val exif = currentPhotoPath?.let { ExifInterface(it) }
        val orientation = exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )


        // get Image Bitmap
        var imageBitmap = BitmapFactory.decodeFile(currentPhotoPath)

        // rotate image Bitmap
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> imageBitmap = rotateImage(imageBitmap, 90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> imageBitmap = rotateImage(imageBitmap, 180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> imageBitmap = rotateImage(imageBitmap, 270F)
        }
        _photoBitmap.value = getResizedBitmap(imageBitmap, 299)
        _analyzeVisibility.value=true
        _recipes.value = null
        _className.value = null
        _status.value = RecipeApiStatus.LOADING
    }

    //Utilities for navigation
    fun displayPropertyDetails(recipeProperty: RecipeProperty) {
        _navigateToSelectedProperty.value = recipeProperty
    }

    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }

    // reset to initial state
    fun reset(){
        _photoBitmap.value = null
        _analyzeVisibility.value=false
        _recipes.value = null
        _status.value=null
        _className.value=null
        _navigateToSelectedProperty.value = null
    }

    //    Utilities for Bitmap Processing
    /**
     * reduces the size of the image
     * @param image
     * @param maxSize
     * @return resized bitmap
     */
    @Suppress("SameParameterValue")
    private fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap? {
        var width = image.width
        var height = image.height
        val bitmapRatio = width.toFloat() / height.toFloat()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)
    }

    /**
     * rotates image
     * @param bitmap to rotate
     * @param degrees to rotate
     * @return rotated bitmap
     */
    private fun rotateImage(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = app.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "RASOEE_${timeStamp}_IMAGE", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    //    Utilities for module loading
    @Suppress("SameParameterValue")
    private fun assetFilePath(context: Context, assetName: String): String {
        val file = File(context.getExternalFilesDir(null), assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }

        try {
            val inputStream = context.assets.open(assetName)
            val outputStream = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var read: Int = inputStream.read(buffer)
            while (read != -1) {
                outputStream.write(buffer, 0, read)
                read = inputStream.read(buffer)
            }
            outputStream.flush()
        } catch (e: Exception) {
            Toast.makeText(context, "An Error Occured while loading model", Toast.LENGTH_SHORT)
                .show()
        } finally {
            return file.absolutePath
        }
    }

    // Utilities for model setup and loading
    private suspend fun evaluate(bitmap: Bitmap): String? {
        return withContext(Dispatchers.Default) { // This will run in the default thread only: No IO calls as Edge AI
            val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                bitmap,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB
            )

            module?.let { module ->
                val outputTensor = module.forward(IValue.from(inputTensor)).toTensor()
                val scores = outputTensor.dataAsFloatArray


                var maxScore = -Float.MAX_VALUE
                var maxScoreIdx = -1
                for (i in scores.indices) {
                    if (scores[i] > maxScore) {
                        maxScore = scores[i]
                        maxScoreIdx = i
                    }
                }
                return@withContext Classes.FOOD_CLASSES.get(maxScoreIdx)
            }
        }
    }

    private suspend fun setupModel() {
        withContext(Dispatchers.Default) {
            if (module == null) {
                val moduleFileAbsoluteFilePath: String =
                    File(assetFilePath(app.applicationContext,
                        MODEL_NAME
                    )).absolutePath
                module = Module.load(moduleFileAbsoluteFilePath)
            }
        }
    }

}