package alex.sin.calculator

import android.widget.Button
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
class RobolectricActivityTest {

    private lateinit var activity: MainActivity

    @Before
    fun setup() {
        activity = Robolectric.buildActivity(MainActivity::class.java).create().start().visible().get()
    }

    @Test
    fun shouldNotBeNull() {
        assertNotNull(activity)
    }

    @Test
    fun shouldContainButtons() {
        assertNotNull(activity.findViewById(R.id._0))
        assertNotNull(activity.findViewById(R.id._plus))
        assertNotNull(activity.findViewById(R.id._ac))
    }

    @Test
    fun buttonsIsClickable() {
        assertNotNull(activity.findViewById<Button>(R.id._0).performClick())
        assertNotNull(activity.findViewById<Button>(R.id._plus).performClick())
        assertNotNull(activity.findViewById<Button>(R.id._ac).performClick())
    }

//    @Test
//    fun buttonsWork() {
//        clickOn(activity.findViewById<Button>(R.id._1))//.performClick()
//        clickOn(activity.findViewById<Button>(R.id._ac))
//
//        assertNotNull(activity.findViewById<TextView>(R.id.input).text)
//        assertNotEquals("Input field is empty, but it should not", "", activity.findViewById<TextView>(R.id.input).text)
//        assertEquals("Button `1` does not add '1' to input field", "1", activity.findViewById<TextView>(R.id.input).text)
//    }
//
//    @Test
//    fun simpleScenario() {
//        assertNotNull(activity.findViewById<TextView>(R.id.input).text)
//        assertEquals("Input field should be empty", "0", activity.findViewById<TextView>(R.id.input).text)
//        assertNotNull(activity.findViewById<TextView>(R.id.result).text)
//        assertEquals("Result field should be empty", "0", activity.findViewById<TextView>(R.id.result).text)
//
//        activity.findViewById<Button>(R.id._1).performClick()
//        activity.findViewById<Button>(R.id._plus).performClick()
//        activity.findViewById<Button>(R.id._1).performClick()
//        activity.findViewById<Button>(R.id._ac).performClick()
//
//        assertNotNull(activity.findViewById<TextView>(R.id.input).text)
//        assertNotEquals("Input field is empty, but it should not", "", activity.findViewById<TextView>(R.id.input).text)
//        assertEquals("Button `1` does not add '1' to input field", "2", activity.findViewById<TextView>(R.id.input).text)
//    }
}