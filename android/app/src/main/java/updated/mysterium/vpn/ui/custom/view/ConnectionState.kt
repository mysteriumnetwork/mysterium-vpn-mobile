package updated.mysterium.vpn.ui.custom.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ConnectionStateLayoutBinding
import updated.mysterium.vpn.common.data.DisplayMoneyOptions
import updated.mysterium.vpn.common.data.PriceUtils
import updated.mysterium.vpn.model.connection.ConnectionType
import updated.mysterium.vpn.model.manual.connect.ConnectionStatistic
import updated.mysterium.vpn.model.manual.connect.Proposal
import updated.mysterium.vpn.model.nodes.ProposalPaymentMoney
import updated.mysterium.vpn.model.proposal.parameters.NodeType

class ConnectionState @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private companion object {
        const val DOUBLE_FORMAT_TEMPLATE = "%.3f"
    }

    private lateinit var binding: ConnectionStateLayoutBinding

    override fun onFinishInflate() {
        super.onFinishInflate()
        val connectionState = LayoutInflater.from(context)
            .inflate(R.layout.connection_state_layout, this, false)
        addView(connectionState)
        binding = ConnectionStateLayoutBinding.bind(connectionState)
    }

    fun initListeners(disconnect: () -> Unit) {
        binding.connectedLayout.disconnectButton.setOnClickListener {
            disconnect.invoke()
        }
    }

    fun showConnectionState(connectionType: ConnectionType?, proposal: Proposal?) {
        if (connectionType == ConnectionType.SMART_CONNECT) {
            showConnectionType(ConnectionType.SMART_CONNECT)
        } else if (connectionType == ConnectionType.MANUAL_CONNECT) {
            proposal?.let {
                showConnectionType(ConnectionType.MANUAL_CONNECT)
                showConnectionProposal(it)
            }
        }
        binding.connectedLayout.cardConnectedLayout.visibility = View.INVISIBLE
        binding.connectingLayout.cardConnectingLayout.visibility = View.VISIBLE
    }

    private fun showConnectionType(type: ConnectionType?) {
        if (type == ConnectionType.SMART_CONNECT) {
            binding.connectingLayout.connectionTypeTextView.text =
                context.getString(R.string.smart_connect_connecting_title)
            binding.connectingLayout.connectingNodeInfo.visibility = View.GONE
            binding.connectingLayout.smartConnectLoadingAnimation.visibility = View.VISIBLE
        } else {
            binding.connectingLayout.connectionTypeTextView.text =
                context.getString(R.string.manual_connect_connecting_title)
            binding.connectingLayout.connectingNodeInfo.visibility = View.VISIBLE
            binding.connectingLayout.smartConnectLoadingAnimation.visibility = View.GONE
        }
    }

    private fun showConnectionProposal(proposal: Proposal) {
        binding.connectingLayout.apply {
            countryNameTextView.text = proposal.countryName
            Glide.with(context)
                .load(proposal.countryFlagImage)
                .circleCrop()
                .into(countryFlagImageView)
            nodeProviderCodeTextView.text = proposal.providerID
            val nodeTypeDrawable = if (proposal.nodeType == NodeType.RESIDENTIAL) {
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
        binding.connectingLayout.cardConnectingLayout.visibility = View.INVISIBLE
        binding.connectedLayout.disconnectButton.text = context.getString(
            R.string.manual_connect_disconnect
        )
        binding.connectedLayout.disconnectButton.isClickable = true
    }

    fun showDisconnectingState() {
        binding.connectedLayout.cardConnectedLayout.visibility = View.VISIBLE
        binding.connectingLayout.cardConnectingLayout.visibility = View.INVISIBLE
        binding.connectedLayout.disconnectButton.text = context.getString(
            R.string.manual_connect_disconnecting
        )
        binding.connectedLayout.disconnectButton.isClickable = false
    }

    fun updateConnectedStatistics(statistics: ConnectionStatistic, currency: String) {
        val tokensSpent = PriceUtils.displayMoney(
            ProposalPaymentMoney(
                amount = statistics.tokensSpent,
                currency = currency
            ),
            DisplayMoneyOptions(fractionDigits = 4, showCurrency = false)
        )
        showDataType(statistics)
        binding.connectedLayout.durationValueTextView.text = statistics.duration
        binding.connectedLayout.paidMystValueTextView.text = tokensSpent
        binding.connectedLayout.dataSendValueTextView.text = statistics.bytesSent.value
        binding.connectedLayout.paidEurValueTextView.text =
            DOUBLE_FORMAT_TEMPLATE.format(statistics.currencySpent)
        binding.connectedLayout.dataReceiveValueTextView.text = context.getString(
            R.string.manual_connect_data_received, statistics.bytesReceived.value
        )
    }

    private fun showDataType(statistics: ConnectionStatistic) {
        if (statistics.bytesReceived.units == statistics.bytesSent.units) {
            binding.connectedLayout.dataTypeTextView.text = statistics.bytesSent.units
        } else {
            binding.connectedLayout.dataTypeTextView.text = context.getString(
                R.string.manual_connect_multi_data_type,
                statistics.bytesReceived.units,
                statistics.bytesSent.units
            )
        }
    }
}
