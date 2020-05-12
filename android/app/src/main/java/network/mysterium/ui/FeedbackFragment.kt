package network.mysterium.ui

import android.annotation.SuppressLint
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
import network.mysterium.vpn.BuildConfig
import network.mysterium.vpn.R

class FeedbackFragment : Fragment() {
    private lateinit var feedbackViewModel: FeedbackViewModel

    private lateinit var feedbackEmail: EditText
    private lateinit var feedbackMessage: EditText
    private lateinit var feedbackSubmitButton: MaterialButton
    private lateinit var versionLabel: TextView
    private lateinit var feedbackToolbar: Toolbar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_feedback, container, false)
        val nodeRepository = AppContainer.from(activity).nodeRepository
        feedbackViewModel = FeedbackViewModel(nodeRepository)

        feedbackEmail = root.findViewById(R.id.feedback_email)
        feedbackMessage = root.findViewById(R.id.feedback_message)
        feedbackSubmitButton = root.findViewById(R.id.feedback_submit_button)
        versionLabel = root.findViewById(R.id.vpn_version_label)
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
        navigateTo(root, Screen.MAIN)
        showMessage(root.context, getString(R.string.feedback_submit_success))

        // Do not wait for feedback to send response as it may take some time.
        CoroutineScope(Dispatchers.Main).launch {
            try {
                feedbackViewModel.submit()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send user feedback", e)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateVersionLabel() {
        versionLabel.text = "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"
    }

    companion object {
        private const val TAG = "FeedbackFragment"
    }
}
