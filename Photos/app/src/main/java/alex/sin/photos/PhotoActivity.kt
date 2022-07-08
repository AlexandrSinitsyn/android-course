package alex.sin.photos

import alex.sin.photos.databinding.ActivityPhotoBinding
import alex.sin.photos.view.PhotoPreview
import alex.sin.photos.view.getSmallString
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


var currentPhoto = PhotoPreview("default name", "default url", 0)

fun getFilename(url: String, id: Int) = "${url.split("-")[1]}-$id"

class PhotoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.name.text = currentPhoto.name
        binding.url.text = getSmallString(currentPhoto.url)

        val bitmap = BitmapFactory.decodeFile("${cacheDir.absolutePath}/${getFilename(currentPhoto.url, currentPhoto.id)}.jpg")

        Log.i("Cache", "no file found")

        binding.image.setImageBitmap(bitmap)
    }
}
