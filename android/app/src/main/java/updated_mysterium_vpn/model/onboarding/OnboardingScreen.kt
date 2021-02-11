package updated_mysterium_vpn.model.onboarding

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OnboardingScreen(
        val position: Int,
        val topTitleRes: Int,
        val contentTitleRes: Int,
        val contentDescriptionRes: Int,
        val animationRes: Int
) : Parcelable
