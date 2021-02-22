package updated.mysterium.vpn.model.onboarding

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OnboardingScreen(
    val position: Int,
    val topTitleRes: Int,
    val additionalTopTitleRes: Int? = null,
    val contentTitleRes: Int,
    val contentDescriptionRes: Int,
    val animationRes: Int
) : Parcelable
