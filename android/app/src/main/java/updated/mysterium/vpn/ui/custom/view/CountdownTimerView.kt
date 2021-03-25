package updated.mysterium.vpn.ui.custom.view

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.CustomCountdownTimerBinding
import updated.mysterium.vpn.common.date.DateUtil

class CountdownTimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private companion object {
        private const val TIMER_IN_MS = 20 * 60 * 1000L // 20 minutes
        private const val ONE_SECOND_INTERVAL = 1000L // 1 second
    }

    private lateinit var binding: CustomCountdownTimerBinding

    override fun onFinishInflate() {
        super.onFinishInflate()
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.custom_countdown_timer, this, false)
        inflateItem(itemView)
        addView(itemView)
    }

    private fun inflateItem(item: View) {
        binding = CustomCountdownTimerBinding.bind(item)
        startTimer()
    }

    private fun startTimer() {
        object : CountDownTimer(TIMER_IN_MS, ONE_SECOND_INTERVAL) {

            override fun onTick(millisUntilFinished: Long) {
                updateTimerUI(DateUtil.convertTimeToStringMinutesFormat(millisUntilFinished))
            }

            override fun onFinish() {
                // TODO("Not yet implemented")
            }
        }.apply {
            start()
        }
    }

    private fun updateTimerUI(time: String) {
        binding.min1.text = time[0].toString()
        binding.min2.text = time[1].toString()
        binding.sec1.text = time[2].toString()
        binding.sec2.text = time[3].toString()
    }
}
