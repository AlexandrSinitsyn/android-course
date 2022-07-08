package alex.sin.animations

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.LinearInterpolator
import android.widget.TextView
//import alex.sin.animations.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

//    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.from(layoutInflater)
        setContentView(R.layout.activity_main)

        /*binding.loading*/findViewById<TextView>(R.id.loading).startAnimation(AlphaAnimation(1f, 0f).apply {
            duration = 500L
            repeatCount = AlphaAnimation.INFINITE
            repeatMode = AlphaAnimation.REVERSE
        })
    }
}