package network.mysterium

import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

fun onViewReady(viewMatcher: Matcher<View>, count: Int = 3, sleepMillis: Long = 2000): ViewInteraction {
    for (i in 1..count) {
        try {
            return Espresso.onView(viewMatcher).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        } catch (e: NoMatchingViewException) {
            if (i == count) {
                throw e
            }
            Thread.sleep(sleepMillis)
        }
    }
    throw Throwable("view not found")
}

fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int): Matcher<View> {

    return object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("Child at position $position in parent ")
            parentMatcher.describeTo(description)
        }

        public override fun matchesSafely(view: View): Boolean {
            val parent = view.parent
            return parent is ViewGroup && parentMatcher.matches(parent)
                    && view == parent.getChildAt(position)
        }
    }
}
