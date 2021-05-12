package updated.mysterium.vpn.ui.custom.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.airbnb.lottie.LottieAnimationView
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.CryptoAnimationBinding

class CryptoAnimationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val MYST = "MYST"
        const val BTC = "BTC"
        const val ETH = "ETH"
        const val LTC = "LTC"
        const val DAI = "DAI"
        const val T = "T"
        const val DOGE = "DOGE"
    }

    private lateinit var binding: CryptoAnimationBinding

    override fun onFinishInflate() {
        super.onFinishInflate()
        val animationLayout = LayoutInflater.from(context)
            .inflate(R.layout.crypto_animation, this, false)
        addView(animationLayout)
        binding = CryptoAnimationBinding.bind(animationLayout)
    }

    fun changeAnimation(crypto: String) {
        stopAnimation()
        when (crypto) {
            MYST -> playAnimation(binding.mystAnimation)
            BTC -> playAnimation(binding.btcAnimation)
            ETH -> playAnimation(binding.ethAnimation)
            LTC -> playAnimation(binding.ltcAnimation)
            DAI -> playAnimation(binding.daiAnimation)
            T -> playAnimation(binding.tAnimation)
            DOGE -> playAnimation(binding.dogeAnimation)
        }
    }

    private fun stopAnimation() {
        binding.mystAnimation.apply {
            visibility = View.INVISIBLE
            cancelAnimation()
        }
        binding.btcAnimation.apply {
            visibility = View.INVISIBLE
            cancelAnimation()
        }
        binding.ethAnimation.apply {
            visibility = View.INVISIBLE
            cancelAnimation()
        }
        binding.ltcAnimation.apply {
            visibility = View.INVISIBLE
            cancelAnimation()
        }
        binding.daiAnimation.apply {
            visibility = View.INVISIBLE
            cancelAnimation()
        }
        binding.tAnimation.apply {
            visibility = View.INVISIBLE
            cancelAnimation()
        }
        binding.dogeAnimation.apply {
            visibility = View.INVISIBLE
            cancelAnimation()
        }
    }

    private fun playAnimation(animation: LottieAnimationView) {
        animation.apply {
            visibility = View.VISIBLE
            playAnimation()
        }
    }
}
