package alex.sin.calculator

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException

class MainActivity : Activity() {

    private val mainCalculations = MainCalculations(this::getResult, { inputField.text = it }, { resultField.text = it }, { inputField.text = "" })


    private lateinit var inputField: TextView
    private lateinit var resultField: TextView

    private fun onClickListener(view: View) =
        mainCalculations.parseKey(mainCalculations.keyMap[view.id] ?: throw IllegalArgumentException("unsupported operation"))


    private fun getResult(s: String): Double {
        return try {
            s.toDouble()
        } catch (ignored: NumberFormatException) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show()
            0.0
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val allButtons = mainCalculations.keyMap.map { it.key }

        allButtons.forEach { id -> findViewById<Button>(id).setOnClickListener { onClickListener(it) } }

        inputField = findViewById(R.id.input)
        resultField = findViewById(R.id.result)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putDouble("previousResult", mainCalculations.previousResult)
        outState.putBoolean("isDouble", mainCalculations.isDouble)
        outState.putBoolean("commandEntered", mainCalculations.commandEntered)
        outState.putString("previousCommand", mainCalculations.previousCommand)
        outState.putString("result", mainCalculations.result.toString())

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mainCalculations.previousResult = savedInstanceState.getDouble("previousResult")
        mainCalculations.isDouble = savedInstanceState.getBoolean("isDouble")
        mainCalculations.previousCommand = savedInstanceState.getString("previousCommand") ?: ""
        mainCalculations.commandEntered = savedInstanceState.getBoolean("commandEntered")

        mainCalculations.result.clear().append(savedInstanceState.getString("result"))
        inputField.text = if (mainCalculations.result.isBlank()) mainCalculations.previousResult.toString() else mainCalculations.result.toString()
    }
}
