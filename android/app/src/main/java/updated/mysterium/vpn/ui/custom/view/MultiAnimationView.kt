package updated.mysterium.vpn.ui.custom.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.MultiAnimationBinding

class MultiAnimationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var binding: MultiAnimationBinding

    override fun onFinishInflate() {
        super.onFinishInflate()
        val animationLayout = LayoutInflater.from(context)
            .inflate(R.layout.multi_animation, this, false)
        addView(animationLayout)
        binding = MultiAnimationBinding.bind(animationLayout)
    }

    fun disconnectedState() {
        binding.connectingAnimation.apply {
            visibility = View.INVISIBLE
            cancelAnimation()
        }
        binding.connectedAnimation.apply {
            visibility = View.INVISIBLE
            cancelAnimation()
        }
        binding.disconnectedAnimation.apply {
            visibility = View.VISIBLE
            playAnimation()
        }
    }

    fun connectingState() {
        binding.disconnectedAnimation.apply {
            visibility = View.INVISIBLE
            cancelAnimation()
        }
        binding.connectedAnimation.apply {
            visibility = View.INVISIBLE
            cancelAnimation()
        }
        binding.connectingAnimation.apply {
            visibility = View.VISIBLE
            playAnimation()
        }
    }

    fun connectedState() {
        binding.disconnectedAnimation.apply {
            visibility = View.INVISIBLE
            cancelAnimation()
        }
        binding.connectingAnimation.apply {
            visibility = View.INVISIBLE
            cancelAnimation()
        }
        binding.connectedAnimation.apply {
            visibility = View.VISIBLE
            playAnimation()
        }
    }
}
