package alex.sin.calculator

import androidx.test.filters.SmallTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner
import java.lang.NumberFormatException

@SmallTest
@RunWith(MockitoJUnitRunner::class)
class CalculatorUnitTest {

    private val mainCalculations: MainCalculations = MainCalculations({
        try { it.toDouble() } catch (ignored: NumberFormatException) { 0.0 }
    }, {  }, {  }, {  })

    private lateinit var resultBefore: String

    private fun getResult() = mainCalculations.checkResultField()

    @Before
    fun setUp() {
        mainCalculations.hardReset()

        resultBefore = getResult()
    }

    @Before
    fun isResultFieldEmpty() {
        assertEquals("Result field is not empty", "", getResult())
    }

    @Test
    fun addEmptyString() {
        mainCalculations.appendSymbol("", true)

        assertEquals("Incorrect addition of empty string", resultBefore, getResult())
    }

    @Test
    fun addDigitString() {
        mainCalculations.appendSymbol("1", true)

        assertFalse("Incorrect addition of digit string", resultBefore == getResult())
        assertTrue("Incorrect addition of digit string", resultBefore.plus("1") == getResult())
    }

    @Test
    fun addMultiString() {
        mainCalculations.appendSymbol("12345", true)

        assertFalse("Incorrect addition of multi string", resultBefore == getResult())
        assertTrue("Incorrect addition of multi string: $resultBefore != ${getResult()}", resultBefore.plus("12345") == getResult())
    }

    @Test
    fun addInvalidString() {
        mainCalculations.appendSymbol("*", true)

        try {
            mainCalculations.checkInput()

            assertTrue("Expected error", false)
        } catch (e: NumberFormatException) {
            assertTrue("Expected error", true)
        }
    }

    @Test
    fun addCommand() {
        mainCalculations.addCommand("+")

        assertTrue("Incorrect addition of a command", mainCalculations.checkIsCommandEntered())
    }

    @Test
    fun addSymbol1() {
        mainCalculations.appendSymbol("+", false)

        assertTrue("Incorrect addition of a command", mainCalculations.checkIsCommandEntered())
    }

    @Test
    fun addSymbol2() {
        mainCalculations.appendSymbol("1", false)

        assertFalse("Incorrect addition of digit string", resultBefore == getResult())
        assertTrue("Incorrect addition of digit string", resultBefore.plus("1") == getResult())
    }

    @Test
    fun doubleInput() {
        mainCalculations.appendSymbol(".", false)

        assertTrue("Double input expected", mainCalculations.checkIsDouble())
    }

    @Test
    fun invalidDoubleInput() {
        mainCalculations.appendSymbol("1", false)
        mainCalculations.appendSymbol(".", false)
        mainCalculations.appendSymbol(".", false)

        assertTrue("Double input expected", mainCalculations.checkIsDouble())

        mainCalculations.update()

        assertEquals("Invalid input expected", 0.0, mainCalculations.previousResult, 0.0)
    }

    @Test
    fun fulExpression() {
        mainCalculations.appendSymbol("1", false)
        mainCalculations.appendSymbol("+", false)
        mainCalculations.appendSymbol("1", false)

        mainCalculations.update()

        assertEquals("Invalid calculations", 2.0, mainCalculations.previousResult, 0.0)
    }
}