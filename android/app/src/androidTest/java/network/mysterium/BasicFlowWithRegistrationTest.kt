package network.mysterium

import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class BasicFlowWithRegistrationTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    // TODO: For some reasons this fails with Failed to grant permissions, see logcat for details error.
    // @Rule
    // @JvmField
    // val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.BIND_VPN_SERVICE)

    @Test
    fun basicFlowWithRegistrationTest() {
        Views.acceptTermsButton.perform(click())

        Views.balanceLabel.perform(click())

        Views.registrationPleaseWaitLabel.check(matches(withText("Please wait")))

        Views.topUpButton.check(matches(isDisplayed()))

        Views.navBackButton.perform(click())

        Views.selectProposalLayout.perform(click())

        Views.proposalSearchInput.perform(replaceText("0xfbf"), closeSoftKeyboard())

        Views.selectProposalItem(0)

        Views.connectionButton.perform(click())

        Views.checkStatusLabel("Connected")

        Views.connectionButton.perform(click())

        Views.checkStatusLabel("Disconnected")
    }
}
