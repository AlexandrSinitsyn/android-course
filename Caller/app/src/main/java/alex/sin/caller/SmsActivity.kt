package alex.sin.caller

import alex.sin.caller.databinding.SmsBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SmsActivity : AppCompatActivity() {

    lateinit var binding: SmsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SmsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.callingName.text = selectedContact?.name ?: defaultName
        binding.callingNumber.text = selectedContact?.number ?: defaultNumber
    }
}