package alex.sin.weather.forecast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    private var nightMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.nightModeButton).setOnClickListener {
            nightMode = !nightMode

            AppCompatDelegate.setDefaultNightMode(if (nightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("nightMode", nightMode)

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        nightMode = savedInstanceState.getBoolean("nightMode")
    }
}