package alex.sin.posts.activity

import alex.sin.posts.databinding.ActivityFailureBinding
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class FailureActivity : AppCompatActivity() {

    lateinit var binding: ActivityFailureBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFailureBinding.inflate(layoutInflater)

        binding.exceptionText.text = App.instance.exception?.message ?: "Exception is unknown"

        setContentView(binding.root)
    }
}