package updated.mysterium.vpn.model.onboarding

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OnboardingScreen(
    val topTitleRes: Int,
    val additionalTopTitleRes: Int? = null,
    val contentTitleRes: Int,
    val contentDescriptionRes: Int,
    val animationRes: Int
) : Parcelable
