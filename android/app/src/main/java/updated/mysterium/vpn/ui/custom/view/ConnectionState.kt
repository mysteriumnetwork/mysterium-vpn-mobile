package updated.mysterium.vpn.ui.custom.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import network.mysterium.proposal.NodeType
import network.mysterium.service.core.ProposalPaymentMoney
import network.mysterium.ui.DisplayMoneyOptions
import network.mysterium.ui.PriceUtils
import network.mysterium.ui.StatisticsModel
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ConnectionStateLayoutBinding
import updated.mysterium.vpn.model.manual.connect.ProposalModel

class ConnectionState @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var binding: ConnectionStateLayoutBinding

    override fun onFinishInflate() {
        super.onFinishInflate()
        val connectionState = LayoutInflater.from(context)
            .inflate(R.layout.connection_state_layout, this, false)
        addView(connectionState)
        binding = ConnectionStateLayoutBinding.bind(connectionState)
    }

    fun initListeners(
        selectNodeManually: () -> Unit,
        disconnect: () -> Unit
    ) {
        binding.selectNodeLayout.selectNodeManuallyButton.setOnClickListener {
            selectNodeManually.invoke()
        }
        binding.connectedLayout.disconnectButton.setOnClickListener {
            disconnect.invoke()
        }
    }

    fun showDisconnectedState() {
        binding.connectedLayout.cardConnectedLayout.visibility = View.INVISIBLE
        binding.selectNodeLayout.cardSelectNodeLayout.visibility = View.VISIBLE
        binding.connectingLayout.cardConnectingLayout.visibility = View.INVISIBLE
    }

    fun showConnectionState(proposalModel: ProposalModel) {
        binding.connectedLayout.cardConnectedLayout.visibility = View.INVISIBLE
        binding.selectNodeLayout.cardSelectNodeLayout.visibility = View.INVISIBLE
        binding.connectingLayout.apply {
            cardConnectingLayout.visibility = View.VISIBLE
            countryNameTextView.text = proposalModel.countryName
            Glide.with(context)
                .load(proposalModel.countryFlagImage)
                .circleCrop()
                .into(countryFlagImageView)
            nodeProviderCodeTextView.text = proposalModel.providerID
            val nodeTypeDrawable = if (proposalModel.nodeType == NodeType.RESIDENTIAL) {
                R.drawable.item_residential
            } else {
                R.drawable.item_non_residential
            }
            Glide.with(context)
                .load(nodeTypeDrawable)
                .circleCrop()
                .into(nodeTypeImageView)
        }
    }

    fun showConnectedState() {
        binding.connectedLayout.cardConnectedLayout.visibility = View.VISIBLE
        binding.selectNodeLayout.cardSelectNodeLayout.visibility = View.INVISIBLE
        binding.connectingLayout.cardConnectingLayout.visibility = View.INVISIBLE
    }

    fun updateConnectedStatistics(statistics: StatisticsModel, currency: String) {
        val tokensSpent = PriceUtils.displayMoney(
            ProposalPaymentMoney(
                amount = statistics.tokensSpent,
                currency = currency
            ),
            DisplayMoneyOptions(fractionDigits = 4, showCurrency = false)
        )
        binding.connectedLayout.durationValueTextView.text = statistics.duration
        binding.connectedLayout.dataTypeTextView.text = statistics.bytesSent.units
        binding.connectedLayout.paidMystValueTextView.text = tokensSpent
        binding.connectedLayout.dataSendValueTextView.text = statistics.bytesSent.value
        binding.connectedLayout.dataReceiveValueTextView.text = context.getString(
            R.string.manual_connect_data_received, statistics.bytesReceived.value
        )
    }
}
