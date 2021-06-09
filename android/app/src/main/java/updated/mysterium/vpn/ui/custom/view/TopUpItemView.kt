package updated.mysterium.vpn.ui.custom.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.CustomTopUpItemBinding

class TopUpItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var binding: CustomTopUpItemBinding
    private var icon: Drawable? = null
    private var value: String? = null
    private var type: String? = null
    private var title: String? = null

    init {
        attrs?.let {
            getItemAttributes(it)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.custom_top_up_item, this, false)
        inflateItem(itemView)
        addView(itemView)
    }

    fun setData(data: String) {
        binding.itemValueTextView.text = data
    }

    fun setType(type: String) {
        binding.itemDataTypeTextView.text = type
    }

    private fun getItemAttributes(attrs: AttributeSet) {
        val iconsAttributes = context.obtainStyledAttributes(
            attrs, R.styleable.TopUpItemView
        )
        if (iconsAttributes.hasValue(R.styleable.TopUpItemView_topUpIcon)) {
            icon = iconsAttributes.getDrawable(R.styleable.TopUpItemView_topUpIcon)
        }
        if (iconsAttributes.hasValue(R.styleable.TopUpItemView_topUpValue)) {
            value = iconsAttributes.getString(R.styleable.TopUpItemView_topUpValue)
        }
        if (iconsAttributes.hasValue(R.styleable.TopUpItemView_topUpType)) {
            type = iconsAttributes.getString(R.styleable.TopUpItemView_topUpType)
        }
        if (iconsAttributes.hasValue(R.styleable.TopUpItemView_topUpTitle)) {
            title = iconsAttributes.getString(R.styleable.TopUpItemView_topUpTitle)
        }
        iconsAttributes.recycle()
    }

    private fun inflateItem(item: View) {
        binding = CustomTopUpItemBinding.bind(item)
        binding.iconSrcImageView.setImageDrawable(icon)
        binding.itemValueTextView.text = value
        binding.itemDataTypeTextView.text = type
        binding.itemTitleTextView.text = title
    }
}
