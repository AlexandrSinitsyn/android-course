package alex.sin.posts.activity

import alex.sin.posts.databinding.ActivityPostCreationBinding
import alex.sin.posts.domain.Post
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class PostCreationActivity : AppCompatActivity() {

    lateinit var binding: ActivityPostCreationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostCreationBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.postButton.setOnClickListener {
            App.instance.mainActivity!!.addPost(Random.nextInt(1, 10), binding.postTitle.text.toString(), binding.postBody.text.toString())
            finish()
        }
    }
}