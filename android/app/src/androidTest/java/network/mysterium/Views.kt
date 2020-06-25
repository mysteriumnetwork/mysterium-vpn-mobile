package network.mysterium

import android.widget.BaseAdapter
import android.widget.FrameLayout
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import network.mysterium.vpn.R
import org.hamcrest.Matchers
import org.hamcrest.core.IsInstanceOf

object Views {
    val acceptTermsButton: ViewInteraction
        get() {
            return onViewReady(
                    Matchers.allOf(ViewMatchers.withId(R.id.terms_accept_button), ViewMatchers.withText("Accept"),
                            childAtPosition(
                                    Matchers.allOf(ViewMatchers.withId(R.id.frameLayout),
                                            childAtPosition(
                                                    ViewMatchers.withId(R.id.nav_host_fragment),
                                                    0)),
                                    1),
                            ViewMatchers.isDisplayed()))
        }

    val balanceLabel: ViewInteraction
        get() {
            return onViewReady(
                    Matchers.allOf(ViewMatchers.withId(R.id.vpn_account_balance_layout),
                            childAtPosition(
                                    Matchers.allOf(ViewMatchers.withId(R.id.vpn_top_status_layout),
                                            childAtPosition(
                                                    ViewMatchers.withId(R.id.linearLayout2),
                                                    0)),
                                    1),
                            ViewMatchers.isDisplayed()))
        }

    val registrationPleaseWaitLabel: ViewInteraction
        get() {
            return onViewReady(
                    Matchers.allOf(ViewMatchers.withId(R.id.account_identity_registration_card_title), ViewMatchers.withText("Please wait"),
                            childAtPosition(
                                    childAtPosition(
                                            IsInstanceOf.instanceOf(FrameLayout::class.java),
                                            0),
                                    0),
                            ViewMatchers.isDisplayed()))
        }

    val topUpButton: ViewInteraction
        get() {
            return onViewReady(
                    Matchers.allOf(ViewMatchers.withId(R.id.wallet_topup_free_tokens),
                            childAtPosition(
                                    childAtPosition(
                                            ViewMatchers.withId(R.id.wallet_balance_card),
                                            0),
                                    3),
                            ViewMatchers.isDisplayed()), 20, 5000)
        }

    val navBackButton: ViewInteraction
        get() {
            return onViewReady(
                    Matchers.allOf(childAtPosition(
                            Matchers.allOf(ViewMatchers.withId(R.id.wallet_toolbar),
                                    childAtPosition(
                                            ViewMatchers.withClassName(Matchers.`is`("com.google.android.material.appbar.AppBarLayout")),
                                            0)),
                            1),
                            ViewMatchers.isDisplayed()))
        }


    val selectProposalLayout: ViewInteraction
        get() {
            return onViewReady(
                    Matchers.allOf(ViewMatchers.withId(R.id.vpn_select_proposal_layout),
                            childAtPosition(
                                    Matchers.allOf(ViewMatchers.withId(R.id.vpn_proposal_picker_layout),
                                            childAtPosition(
                                                    ViewMatchers.withId(R.id.vpn_picker_and_button_layout),
                                                    0)),
                                    0),
                            ViewMatchers.isDisplayed()))
        }

    val proposalSearchInput: ViewInteraction
        get() {
            return onViewReady(
                    Matchers.allOf(ViewMatchers.withId(R.id.proposals_search_input),
                            childAtPosition(
                                    Matchers.allOf(ViewMatchers.withId(R.id.proposals_header_layout),
                                            childAtPosition(
                                                    ViewMatchers.withClassName(Matchers.`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                                                    0)),
                                    1),
                            ViewMatchers.isDisplayed()))
        }


    val connectionButton: ViewInteraction
        get() {
            return onViewReady(
                    Matchers.allOf(ViewMatchers.withId(R.id.vpn_connection_button),
                            childAtPosition(
                                    Matchers.allOf(ViewMatchers.withId(R.id.vpn_picker_and_button_layout),
                                            childAtPosition(
                                                    ViewMatchers.withId(R.id.vpn_center_bg_layout),
                                                    1)),
                                    1),
                            ViewMatchers.isDisplayed()))
        }

    fun checkStatusLabel(status: String): ViewInteraction {
        return onViewReady(
                Matchers.allOf(ViewMatchers.withId(R.id.vpn_status_label), ViewMatchers.withText(status),
                        childAtPosition(
                                Matchers.allOf(ViewMatchers.withId(R.id.vpn_top_status_layout),
                                        childAtPosition(
                                                ViewMatchers.withId(R.id.linearLayout2),
                                                0)),
                                2),
                        ViewMatchers.isDisplayed()))
    }

    fun selectProposalItem(position: Int) {
        // Sleep a bit for item to appear after search.
        Thread.sleep(1000)
        // TODO: Implement.
    }
}