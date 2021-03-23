package updated.mysterium.vpn.ui.custom.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.CustomWalletItemBinding

class WalletItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var binding: CustomWalletItemBinding
    private var icon: Drawable? = null
    private var value: String? = null
    private var title: String? = null

    init {
        attrs?.let {
            getItemAttributes(it)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.custom_wallet_item, this, false)
        inflateItem(itemView)
        addView(itemView)
    }

    private fun getItemAttributes(attrs: AttributeSet) {
        val iconsAttributes = context.obtainStyledAttributes(
            attrs, R.styleable.WalletItemView
        )
        if (iconsAttributes.hasValue(R.styleable.WalletItemView_walletIcon)) {
            icon = iconsAttributes.getDrawable(R.styleable.WalletItemView_walletIcon)
        }
        if (iconsAttributes.hasValue(R.styleable.WalletItemView_walletValue)) {
            value = iconsAttributes.getString(R.styleable.WalletItemView_walletValue)
        }
        if (iconsAttributes.hasValue(R.styleable.WalletItemView_walletTitle)) {
            title = iconsAttributes.getString(R.styleable.WalletItemView_walletTitle)
        }
        iconsAttributes.recycle()
    }

    private fun inflateItem(item: View) {
        binding = CustomWalletItemBinding.bind(item)
        binding.iconImageView.setImageDrawable(icon)
        binding.dataValueTextView.text = value
        binding.descriptionTextView.text = title
    }
}
