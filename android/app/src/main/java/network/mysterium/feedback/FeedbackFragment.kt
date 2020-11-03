package network.mysterium.feedback

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.mysterium.AppContainer
import network.mysterium.navigation.Screen
import network.mysterium.navigation.navigateTo
import network.mysterium.navigation.onBackPress
import network.mysterium.ui.*
import network.mysterium.vpn.R

class FeedbackFragment : Fragment() {
    private lateinit var feedbackViewModel: FeedbackViewModel

    private lateinit var feedbackEmail: EditText
    private lateinit var feedbackMessage: EditText
    private lateinit var feedbackSubmitButton: MaterialButton
    private lateinit var appVersionLabel: TextView
    private lateinit var nodeVersionLabel: TextView
    private lateinit var feedbackToolbar: Toolbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_feedback, container, false)
        val nodeRepository = AppContainer.from(activity).nodeRepository
        feedbackViewModel = FeedbackViewModel(nodeRepository)

        feedbackEmail = root.findViewById(R.id.feedback_email)
        feedbackMessage = root.findViewById(R.id.feedback_message)
        feedbackSubmitButton = root.findViewById(R.id.feedback_submit_button)
        appVersionLabel = root.findViewById(R.id.vpn_app_version_label)
        nodeVersionLabel = root.findViewById(R.id.vpn_node_version_label)
        feedbackToolbar = root.findViewById(R.id.feedback_toolbar)

        updateVersionLabel()

        // Handle back press.
        feedbackToolbar.setNavigationOnClickListener {
            hideKeyboard(root)
            navigateTo(root, Screen.MAIN)
        }

        // Handle text change.
        feedbackEmail.onChange { feedbackViewModel.setEmail(it) }
        feedbackMessage.onChange { feedbackViewModel.setMessage(it) }

        // Handle submit.
        feedbackSubmitButton.setOnClickListener {
            hideKeyboard(root)
            handleFeedbackSubmit(root)
        }

        onBackPress {
            navigateTo(root, Screen.MAIN)
        }

        return root
    }

    private fun handleFeedbackSubmit(root: View) {
        if (!feedbackViewModel.isMessageSet()) {
            showMessage(root.context, getString(R.string.feedback_message_required))
            return
        }

        feedbackSubmitButton.isEnabled = false

        // Do not wait for feedback to send response as it may take some time.
        CoroutineScope(Dispatchers.Main).launch {
            try {
                feedbackViewModel.submit()
                navigateTo(root, Screen.MAIN)
                showMessage(root.context, getString(R.string.feedback_submit_success))
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send user feedback", e)
                showMessage(root.context, getString(R.string.feedback_submit_failed))
                feedbackSubmitButton.isEnabled = true
            }
        }
    }

    private fun updateVersionLabel() {
        val appVersion = feedbackViewModel.appVersion()
        appVersionLabel.text = getString(R.string.feedback_app_version, appVersion)

        CoroutineScope(Dispatchers.Main).launch {
            val nodeVersion = feedbackViewModel.nodeVersion()
            nodeVersionLabel.text = getString(R.string.feedback_node_version, nodeVersion)
        }
    }

    companion object {
        private const val TAG = "FeedbackFragment"
    }
}
