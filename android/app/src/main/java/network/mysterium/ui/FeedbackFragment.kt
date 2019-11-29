package network.mysterium.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.button.MaterialButton
import network.mysterium.AppContainer
import network.mysterium.vpn.BuildConfig
import network.mysterium.vpn.R

// TODO: Add keyboard avoiding view.
class FeedbackFragment : Fragment() {
    private lateinit var feedbackViewModel: FeedbackViewModel

    private lateinit var feedbackBackButton: ImageView
    private lateinit var feedbackTypeSpinner: Spinner
    private lateinit var feedbackMessage: EditText
    private lateinit var feedbackSubmitButton: MaterialButton
    private lateinit var versionLabel: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_feedback, container, false)
        val bugReporter = AppContainer.from(activity).bugReporter
        feedbackViewModel = FeedbackViewModel(bugReporter)

        feedbackBackButton = root.findViewById(R.id.feedback_back_button)
        feedbackTypeSpinner = root.findViewById(R.id.feedback_type_spinner)
        feedbackMessage = root.findViewById(R.id.feedback_message)
        feedbackSubmitButton = root.findViewById(R.id.feedback_submit_button)
        versionLabel = root.findViewById(R.id.vpn_version_label)

        updateVersionLabel()

        // Handle back press.
        feedbackBackButton.setOnClickListener {
            hideKeyboard(root)
            root.findNavController().navigate(R.id.action_go_to_vpn_screen)
        }

        // Add feedback types data.
        initFeedbackTypesDropdown(root)

        // Handle text change.
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
        feedbackSubmitButton.isEnabled = false
        feedbackViewModel.submit()
        navigateTo(root, Screen.MAIN)
        showMessage(root.context, getString(R.string.feedback_submit_success))
    }

    private fun initFeedbackTypesDropdown(root: View) {
        ArrayAdapter.createFromResource(
                root.context,
                R.array.feedback_types,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            feedbackTypeSpinner.adapter = adapter
            feedbackTypeSpinner.onItemSelected { feedbackViewModel.setFeedbackType(it) }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateVersionLabel() {
        versionLabel.text = "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"
    }
}
