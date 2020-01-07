package network.mysterium

import androidx.test.espresso.action.ViewActions.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
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
        Views.checkStatusLabel("Disconnected")
        Views.selectProposalLayout.perform(click())

        Views.proposalSearchInput.perform(replaceText("0xfbf"), closeSoftKeyboard())

        Views.selectProposalItem(0)

        Views.connectionButton.perform(click())

        Views.checkStatusLabel("Connected")

        Thread.sleep(5000)

        Views.connectionButton.perform(click())

        Views.checkStatusLabel("Disconnected")
    }
}
