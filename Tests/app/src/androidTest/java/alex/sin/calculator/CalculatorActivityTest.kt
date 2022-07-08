package alex.sin.calculator

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class UIActivityTest {

    @Rule @JvmField
    var activity = ActivityTestRule(MainActivity::class.java, true, false)

    @Before
    fun presetting() {
        activity.launchActivity(Intent())
    }

    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("alex.sin.ui.tests", appContext.packageName)
    }

    @Test
    fun clickDigit() {
        onView(withId(R.id._1)).perform(click())
        onView(withId(R.id.input)).check(matches(withText("1")))
    }

    @Test
    fun clickMultiDigit() {
        onView(withId(R.id._1)).perform(click())
        onView(withId(R.id._2)).perform(click())
        onView(withId(R.id._3)).perform(click())
        onView(withId(R.id.input)).check(matches(withText("123")))
    }

    @Test
    fun inputDouble() {
        onView(withId(R.id._1)).perform(click())
        onView(withId(R.id._dot)).perform(click())
        onView(withId(R.id._2)).perform(click())
        onView(withId(R.id.input)).check(matches(withText("1.2")))
    }

    @Test
    fun invalidDouble() {
        onView(withId(R.id._1)).perform(click())
        onView(withId(R.id._dot)).perform(click())
        onView(withId(R.id._dot)).perform(click())
        onView(withId(R.id._2)).perform(click())
        onView(withId(R.id.input)).check(matches(withText("1..2")))
    }

    @Test
    fun testEqual() {
        onView(withId(R.id._1)).perform(click())
        onView(withId(R.id.input)).check(matches(withText("1")))
        onView(withId(R.id._eq)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("1")))
    }

    @Test
    fun fulExpression() {
        onView(withId(R.id._1)).perform(click())
        onView(withId(R.id._plus)).perform(click())
        onView(withId(R.id._2)).perform(click())
        onView(withId(R.id._eq)).perform(click())
        onView(withId(R.id.result)).check(matches(withText("3")))
    }
}