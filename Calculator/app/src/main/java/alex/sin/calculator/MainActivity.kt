package alex.sin.calculator

import android.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException

class MainActivity : AppCompatActivity() {

    private val funMap: Map<String, (Double, Double) -> Double> = mapOf(
        Pair("+", { a, b -> a + b }),
        Pair("-", { a, b -> a - b }),
        Pair("*", { a, b -> a * b }),
        Pair("/", { a, b -> a / b })
    )
    private val keyMap: Map<Int, String> = mapOf(
        Pair(R.id._0, "0"),
        Pair(R.id._1, "1"),
        Pair(R.id._2, "2"),
        Pair(R.id._3, "3"),
        Pair(R.id._4, "4"),
        Pair(R.id._5, "5"),
        Pair(R.id._6, "6"),
        Pair(R.id._7, "7"),
        Pair(R.id._8, "8"),
        Pair(R.id._9, "9"),
        Pair(R.id._ac, "clear"),
        Pair(R.id._dot, "."),
        Pair(R.id._plus, "+"),
        Pair(R.id._sub, "-"),
        Pair(R.id._mull, "*"),
        Pair(R.id._div, "/"),
        Pair(R.id._eq, "="),
    )


    private var isDouble = false
    private var result = java.lang.StringBuilder()
    private var previousResult = 0.0
    private var commandEntered = false
    private var previousCommand = ""

    private lateinit var inputField: TextView
    private lateinit var resultField: TextView

    private fun onClickListener(view: View) =
        parseKey(keyMap[view.id] ?: throw IllegalArgumentException("unsupported operation"))


    private fun parseKey(s: String) {
        if (s[0].isDigit()) {
            appendToResult(s)
            return
        }

        when (s) {
            "clear" -> reset()
            "=" -> updateResult()
            "+", "-", "*", "/" -> saveCommand(s)
            "." -> {
                isDouble = true
                appendToResult(s)
            }
            else -> throw IllegalArgumentException("unsupported operation")
        }
    }

    private fun saveCommand(cmd: String) {
        if (previousCommand.isNotBlank() && result.isNotBlank()) {
            val res = funMap[previousCommand]?.invoke(previousResult, getResult()) ?: throw IllegalArgumentException("Unsupported command")

            result.clear().append(res)
        }

        previousCommand = cmd
        commandEntered = true
    }

    private fun updateResult() {
        saveCommand("")

        previousResult = getResult()

        resultField.text = result.toString()

        reset()
    }

    private fun reset() {
        isDouble = false
        commandEntered = false
        previousCommand = ""
        result.clear()

        inputField.text = ""
    }

    private fun appendToResult(s: String) {
        if (commandEntered) {
            if (result.isNotBlank()) {
                previousResult = getResult()
            }

            result.clear()

            commandEntered = false
        }

        result.append(s)

        inputField.text = result.toString()
    }

    private fun getResult(): Double {
        return try {
            result.toString().toDouble()
        } catch (ignored: NumberFormatException) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show()
            0.0
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val allButtons = keyMap.map { it.key }

        allButtons.forEach { id -> findViewById<Button>(id).setOnClickListener { onClickListener(it) } }

        inputField = findViewById(R.id.input)
        resultField = findViewById(R.id.result)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putDouble("previousResult", previousResult)
        outState.putBoolean("isDouble", isDouble)
        outState.putBoolean("commandEntered", commandEntered)
        outState.putString("previousCommand", previousCommand)
        outState.putString("result", result.toString())

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        previousResult = savedInstanceState.getDouble("previousResult")
        isDouble = savedInstanceState.getBoolean("isDouble")
        previousCommand = savedInstanceState.getString("previousCommand") ?: ""
        commandEntered = savedInstanceState.getBoolean("commandEntered")

        result.clear().append(savedInstanceState.getString("result"))
        inputField.text = if (result.isBlank()) previousResult.toString() else result.toString()
    }
}
