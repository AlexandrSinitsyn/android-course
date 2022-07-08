package alex.sin.caller

import alex.sin.caller.databinding.CallBinding
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class CallActivity : AppCompatActivity() {

    lateinit var binding: CallBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CallBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.callingName.text = selectedContact?.name ?: defaultName
        binding.callingNumber.text = selectedContact?.number ?: defaultNumber
    }
}