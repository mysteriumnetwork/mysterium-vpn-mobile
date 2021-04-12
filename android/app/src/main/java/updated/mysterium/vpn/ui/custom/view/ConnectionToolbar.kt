package updated.mysterium.vpn.ui.custom.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ToolbarBaseConnectBinding

class ConnectionToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var leftIconDrawable: Drawable? = null
    private var rightIconDrawable: Drawable? = null
    private var leftButtonListener: (() -> Unit)? = null
    private var rightButtonListener: (() -> Unit)? = null
    private var connectionClickListener: (() -> Unit)? = null
    private lateinit var binding: ToolbarBaseConnectBinding

    init {
        attrs?.let {
            getIconsAttributes(it)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val toolbarView = LayoutInflater.from(context)
            .inflate(R.layout.toolbar_base_connect, this, false)
        inflateToolbarWithIcons(toolbarView)
        addView(toolbarView)
    }

    fun protectedState(isFill: Boolean) {
        if (isFill) {
            binding.connectionCardView.background = ContextCompat.getDrawable(
                context, R.drawable.shape_toolbar_fill
            )
            binding.connectionCurrentStatus.text = context.getString(R.string.manual_connect_protected)
            binding.connectionCurrentStatus.setTextColor(ContextCompat.getColor(
                context, R.color.onboarding_current_screen_white
            ))
        } else {
            binding.connectionCardView.background = ContextCompat.getDrawable(
                context, R.drawable.shape_toolbar_blur
            )
            binding.connectionCurrentStatus.text = context.getString(R.string.manual_connect_protected)
            binding.connectionCurrentStatus.setTextColor(ContextCompat.getColor(
                context, R.color.manual_connect_connection_state
            ))
        }
    }

    fun unprotectedState() {
        binding.connectionCardView.background = ContextCompat.getDrawable(
            context, R.drawable.shape_toolbar_connect
        )
        binding.connectionCurrentStatus.text = context.getString(R.string.manual_connect_country_status)
        binding.connectionCurrentStatus.setTextColor(ContextCompat.getColor(
            context, R.color.menu_subtitle_light_pink
        ))
    }

    fun setRightView(rightView: View) {
        binding.rightViewFrame.addView(rightView)
    }

    fun setLeftIcon(drawable: Drawable?) {
        if (drawable == null) {
            binding.leftButton.visibility = View.INVISIBLE
        } else {
            binding.leftButton.setImageDrawable(drawable)
            binding.leftButton.visibility = View.VISIBLE
        }
    }

    fun setRightIcon(drawable: Drawable?) {
        if (drawable == null) {
            binding.rightButton.visibility = View.INVISIBLE
        } else {
            binding.rightButton.setImageDrawable(drawable)
            binding.rightButton.visibility = View.VISIBLE
        }
    }

    fun onLeftButtonClicked(action: () -> Unit) {
        leftButtonListener = action
    }

    fun onRightButtonClicked(action: () -> Unit) {
        rightButtonListener = action
    }

    fun onConnectClickListener(action: () -> Unit) {
        connectionClickListener = action
    }

    private fun getIconsAttributes(attrs: AttributeSet) {
        val iconsAttributes = context.obtainStyledAttributes(
            attrs, R.styleable.ConnectionToolbar
        )
        if (iconsAttributes.hasValue(R.styleable.ConnectionToolbar_leftIcon)) {
            leftIconDrawable = iconsAttributes.getDrawable(R.styleable.ConnectionToolbar_leftIcon)
        }
        if (iconsAttributes.hasValue(R.styleable.ConnectionToolbar_rightIcon)) {
            rightIconDrawable = iconsAttributes.getDrawable(R.styleable.ConnectionToolbar_rightIcon)
        }
        iconsAttributes.recycle()
    }

    private fun inflateToolbarWithIcons(toolbarView: View) {
        binding = ToolbarBaseConnectBinding.bind(toolbarView)
        leftIconDrawable?.let {
            binding.leftButton.setImageDrawable(it)
            binding.leftButton.visibility = View.VISIBLE
        }
        rightIconDrawable?.let {
            binding.rightButton.setImageDrawable(it)
            binding.rightButton.visibility = View.VISIBLE
        }
        binding.leftButton.setOnClickListener {
            leftButtonListener?.invoke()
        }
        binding.rightButton.setOnClickListener {
            rightButtonListener?.invoke()
        }
        binding.connectionCardView.setOnClickListener {
            connectionClickListener?.invoke()
        }
    }
}
