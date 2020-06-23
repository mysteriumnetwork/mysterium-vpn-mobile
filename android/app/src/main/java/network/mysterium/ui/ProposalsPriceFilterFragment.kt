package network.mysterium.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import network.mysterium.MainApplication
import network.mysterium.ui.list.BaseListAdapter
import network.mysterium.vpn.R

class ProposalsPriceFilterFragment : Fragment() {

    private lateinit var listAdapter: BaseListAdapter
    private lateinit var feedbackToolbar: Toolbar
    private lateinit var proposalsViewModel: ProposalsViewModel
    private lateinit var proposalsCountryFilterResetBtn: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_proposals_country_filter, container, false)
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)

        val appContainer = (activity!!.application as MainApplication).appContainer
        proposalsViewModel = appContainer.proposalsViewModel

        feedbackToolbar = root.findViewById(R.id.proposals_country_filter_toolbar)
        proposalsCountryFilterResetBtn = root.findViewById(R.id.proposals_country_filter_reset_btn)

        feedbackToolbar.setNavigationOnClickListener {
            hideKeyboard(root)
            navigateTo(root, Screen.PROPOSALS)
        }

        proposalsCountryFilterResetBtn.setOnClickListener {
            proposalsViewModel.applyCountryFilter(ProposalFilterCountry("", "", null))
            navigateTo(root, Screen.PROPOSALS)
        }

        onBackPress {
            navigateTo(root, Screen.PROPOSALS)
        }
    }
}
