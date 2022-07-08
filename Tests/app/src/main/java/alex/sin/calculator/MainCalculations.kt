package alex.sin.calculator

import java.lang.IllegalArgumentException
import java.lang.NumberFormatException
import kotlin.jvm.Throws

class MainCalculations(
    val getResult: (String) -> Double,
    private val updateInpField: (String) -> Unit,
    private val updateResField: (String) -> Unit,
    private val resetInputField: () -> Unit,
) {

    private val funMap: Map<String, (Double, Double) -> Double> = mapOf(
        Pair("+", { a, b -> a + b }),
        Pair("-", { a, b -> a - b }),
        Pair("*", { a, b -> a * b }),
        Pair("/", { a, b -> a / b })
    )
    val keyMap: Map<Int, String> = mapOf(
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

    var isDouble = false
    var result = java.lang.StringBuilder()
    var previousResult = 0.0
    var commandEntered = false
    var previousCommand = ""

    fun parseKey(s: String) {
        if (s[0].isDigit()) {
            appendToInput(s)
            return
        }

        when (s) {
            "clear" -> reset()
            "=" -> updateResult()
            "+", "-", "*", "/" -> saveCommand(s)
            "." -> {
                isDouble = true
                appendToInput(s)
            }
            else -> throw IllegalArgumentException("unsupported operation")
        }
    }

    private fun saveCommand(cmd: String) {
        if (previousCommand.isNotBlank() && result.isNotBlank()) {
            val res = funMap[previousCommand]?.invoke(previousResult, getResult(result.toString())) ?: throw IllegalArgumentException("Unsupported command")

            result.clear().append(res)
        }

        previousCommand = cmd
        commandEntered = true
    }

    private fun updateResult() {
        saveCommand("")

        previousResult = getResult(result.toString())

        updateResField(result.toString())

        reset()
    }

    private fun reset() {
        isDouble = false
        commandEntered = false
        previousCommand = ""
        result.clear()

        resetInputField()
    }

    private fun resetAll() {
        reset()

        previousResult = 0.0
    }

    private fun appendToInput(s: String) {
        if (commandEntered) {
            if (result.isNotBlank()) {
                previousResult = getResult(result.toString())
            }

            result.clear()

            commandEntered = false
        }

        result.append(s)

        updateInpField(result.toString())
    }


    fun appendSymbol(s: String, toResult: Boolean) = if (toResult) {
            appendToInput(s)
        } else {
            parseKey(s)
        }

    fun addCommand(s: String) = saveCommand(s)

    fun update() = updateResult()

    fun checkResultField() = result.toString()

    @Throws(NumberFormatException::class)
    fun checkInput() = result.toString().toDouble()

    fun checkIsDouble() = isDouble

    fun checkIsCommandEntered() = commandEntered

    fun hardReset() = resetAll()
}
