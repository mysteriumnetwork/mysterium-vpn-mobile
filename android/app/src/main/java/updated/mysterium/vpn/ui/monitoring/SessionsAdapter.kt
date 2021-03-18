package updated.mysterium.vpn.ui.monitoring

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import network.mysterium.ui.Countries
import network.mysterium.ui.UnitFormatter
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ItemSessionBinding
import updated.mysterium.vpn.common.adapters.ContentListAdapter
import updated.mysterium.vpn.common.date.DateUtil
import updated.mysterium.vpn.model.session.Session
import java.util.*

class SessionsAdapter : ContentListAdapter<Session, SessionsAdapter.SessionViewHolder>() {

    private companion object {
        const val UNKNOWN = "Unknown country"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SessionViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_session, parent, false)
    )

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ItemSessionBinding.bind(itemView)

        fun bind(session: Session) {
            binding.apply {
                providerIdTextView.text = session.providerId
                countryTextView.text = Countries
                    .values[session.providerCountry.toLowerCase(Locale.ROOT)]
                    ?.name
                    ?: UNKNOWN
                durationTextView.text = DateUtil.convertToDateType((session.duration) * 1000)
                val dataReceivedText = "${UnitFormatter.bytesDisplay(session.dataReceived).value} " +
                    UnitFormatter.bytesDisplay(session.dataReceived).units
                dataReceivedTextView.text = dataReceivedText
            }
        }
    }
}
