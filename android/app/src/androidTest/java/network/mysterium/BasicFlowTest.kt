package network.mysterium


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import kotlinx.coroutines.delay
import network.mysterium.ui.ProposalsListAdapter
import network.mysterium.vpn.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.hamcrest.core.IsInstanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class BasicFlowTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun registeredIdentityFlowTest() {
        val linearLayout = onView(
                allOf(withId(R.id.vpn_account_balance_layout),
                        childAtPosition(
                                allOf(withId(R.id.vpn_top_status_layout),
                                        childAtPosition(
                                                withId(R.id.linearLayout2),
                                                0)),
                                1),
                        isDisplayed()))
        linearLayout.perform(click())

        val appCompatImageButton = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.account_toolbar),
                                childAtPosition(
                                        withClassName(`is`("com.google.android.material.appbar.AppBarLayout")),
                                        0)),
                        1),
                        isDisplayed()))
        appCompatImageButton.perform(click())

        val textView2 = onView(
                allOf(withId(R.id.vpn_status_label), withText("Disconnected"),
                        childAtPosition(
                                allOf(withId(R.id.vpn_top_status_layout),
                                        childAtPosition(
                                                withId(R.id.linearLayout2),
                                                0)),
                                2),
                        isDisplayed()))
        textView2.check(matches(withText("Disconnected")))

        val constraintLayout = onView(
                allOf(withId(R.id.vpn_select_proposal_layout),
                        childAtPosition(
                                allOf(withId(R.id.vpn_proposal_picker_layout),
                                        childAtPosition(
                                                withId(R.id.vpn_picker_and_button_layout),
                                                0)),
                                0),
                        isDisplayed()))
        constraintLayout.perform(click())

        // Wait for proposals to load.
        Thread.sleep(5000)

        val appCompatEditText = onView(
                allOf(withId(R.id.proposals_search_input),
                        childAtPosition(
                                allOf(withId(R.id.proposals_header_layout),
                                        childAtPosition(
                                                withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                                                0)),
                                1),
                        isDisplayed()))
        appCompatEditText.perform(replaceText("0x67"), closeSoftKeyboard())


        onView(withId(R.id.proposals_list))
                .perform(actionOnItemAtPosition<ProposalsListAdapter.ProposalViewHolder>(0, click()))

        val materialButton2 = onView(
                allOf(withId(R.id.vpn_connection_button), withText("Connect"),
                        childAtPosition(
                                allOf(withId(R.id.vpn_picker_and_button_layout),
                                        childAtPosition(
                                                withId(R.id.vpn_center_bg_layout),
                                                1)),
                                1),
                        isDisplayed()))
        materialButton2.perform(click())

        // Wait to connect
        Thread.sleep(5000)

        val textView3 = onView(
                allOf(withId(R.id.vpn_status_label), withText("Connected"),
                        childAtPosition(
                                allOf(withId(R.id.vpn_top_status_layout),
                                        childAtPosition(
                                                withId(R.id.linearLayout2),
                                                0)),
                                2),
                        isDisplayed()))
        textView3.check(matches(withText("Connected")))

        val materialButton3 = onView(
                allOf(withId(R.id.vpn_connection_button), withText("Disconnect"),
                        childAtPosition(
                                allOf(withId(R.id.vpn_picker_and_button_layout),
                                        childAtPosition(
                                                withId(R.id.vpn_center_bg_layout),
                                                1)),
                                1),
                        isDisplayed()))
        materialButton3.perform(click())

        // Wait to disconnect
        Thread.sleep(5000)

        val textView4 = onView(
                allOf(withId(R.id.vpn_status_label), withText("Disconnected"),
                        childAtPosition(
                                allOf(withId(R.id.vpn_top_status_layout),
                                        childAtPosition(
                                                withId(R.id.linearLayout2),
                                                0)),
                                2),
                        isDisplayed()))
        textView4.check(matches(withText("Disconnected")))
    }

    fun onViewWait(viewMatcher: Matcher<View>): ViewInteraction {
        for (i in 1..3) {
            try {
                val v = onView(viewMatcher)
                v.perform(click())
                return v
            } catch (e: Throwable) {
                if (i == 3) {
                    throw e
                }
                Thread.sleep(2000)
            }
        }
        throw Throwable("view not found")
    }

    private fun childAtPosition(
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
}
