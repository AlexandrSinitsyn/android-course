@file:Suppress("BlockingMethodInNonBlockingContext")
package alex.sin.photos

import alex.sin.photos.databinding.ActivityMainBinding
import alex.sin.photos.view.PhotoPreview
import alex.sin.photos.view.PhotosAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import androidx.core.graphics.scale
import androidx.lifecycle.coroutineScope
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream
import java.lang.StringBuilder
import java.net.URL

import kotlinx.coroutines.*
import org.json.JSONObject


const val IMAGES_COUNT = 5

var photos: MutableList<PhotoPreview> = mutableListOf()
var currentVisibleListSize = 0

private suspend fun getPhotoDescription(obj: JSONObject) = coroutineScope {
    launch {
        val name = "${obj.getJSONObject("location").getString("name")} | ${obj.getString("id")}"
        val urlSmall = obj.getJSONObject("urls").getString("small")

        photos.add(PhotoPreview(name, urlSmall, photos.size))
    }
}

private suspend fun getByURL() {
    coroutineScope {
        val input = URL("https://api.unsplash.com/photos/random?client_id=IUfizx4eBtvLJS1H7GrGJXWdHWzSL-PV7dQcOAH-HS4&count=$IMAGES_COUNT")
            .openStream().bufferedReader()

        val request = StringBuilder()
        input.lines().forEach { request.append(it) }

        val outerArray = JSONArray(request.toString())

        for (i in 0 until outerArray.length()) {
            val obj = outerArray.getJSONObject(i)

            getPhotoDescription(obj)
        }
    }
}


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (currentVisibleListSize == photos.size) {
            findPhotos()
        }

        val viewManager = LinearLayoutManager(this)
        binding.photosList.apply {
            layoutManager = viewManager
            adapter = PhotosAdapter {
                Log.i("Cache", "onClick")

                currentPhoto = it

                cachePhoto(currentPhoto.url, it.id)

                Log.i("Cache", "cached the photo")
            }
        }

        binding.findNewPhotos.setOnClickListener {
            if (findPhotos()) {
                binding.photosList.adapter?.notifyItemRangeInserted(currentVisibleListSize, photos.size)
                currentVisibleListSize = photos.size
            }
        }
    }

    private fun findPhotos(): Boolean {
        interruptedConnection() ?: return false

        lifecycle.coroutineScope.launch {
            withContext(Dispatchers.IO) {
                getByURL()
            }
        }

        return true
    }

    private fun cachePhoto(url: String, id: Int) {
        Log.i("Cache", ">>> $url")

        val path = "${cacheDir.absolutePath}/${getFilename(url, id)}.jpg"
        val file = File(path)

        Log.i("Cache", "got the path: $path")


        if (!file.exists()) {
            Log.i("Cache", "no file found")

            interruptedConnection() ?: return

            lifecycle.coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    Log.i("Cache", "new thread started")

                    val bytes = URL(url).openStream().readBytes()/*use { input ->
                        FileOutputStream(file).use { output ->
                            Log.i("Cache", "streams are opened")

                            input.copyTo(output)
                        }
                    }*/

                    save(squeeze(BitmapFactory.decodeByteArray(bytes, 0, bytes.size)), FileOutputStream(file))

                    Log.i("Cache", "got the data")

                    startActivity(Intent(this@MainActivity, PhotoActivity::class.java))

                    Log.i("Cache", "new activity has been started")
                }
            }
        } else {
            Log.i("Cache", "file was found")

            startActivity(Intent(this@MainActivity, PhotoActivity::class.java))
        }
    }

    private fun interruptedConnection(): Any? {
        if (isNetworkNotConnected()) {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            return null
        }

        return false
    }

    private fun isNetworkNotConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return connectivityManager.activeNetworkInfo == null || !connectivityManager.activeNetworkInfo!!.isConnected
    }


    private fun squeeze(bitmap: Bitmap): Bitmap {
        var width = (bitmap.width * resources.displayMetrics.density).toInt()
        var height = (bitmap.height * resources.displayMetrics.density).toInt()

        val expectedWidth = resources.getDimension(R.dimen.imageWidth)
        val expectedHeight = resources.getDimension(R.dimen.imageHeight)

        Log.i("Squeeze", "$width : $expectedWidth | $height : $expectedHeight")

        if (width > expectedWidth) {
            val percent = width.toDouble() / expectedWidth
            height = (height * percent).toInt()
        }
        if (height > expectedHeight) {
            val percent = height.toDouble() / expectedHeight
            width = (width * percent).toInt()
        }

        return bitmap.scale(width, height)
    }

    private fun save(bitmap: Bitmap, file: FileOutputStream) = bitmap.compress(Bitmap.CompressFormat.WEBP, 50, file)
}