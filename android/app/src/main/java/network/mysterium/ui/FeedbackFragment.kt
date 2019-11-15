package network.mysterium.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.tiper.MaterialSpinner
import network.mysterium.AppContainer
import network.mysterium.vpn.R

class FeedbackFragment : Fragment() {
    private lateinit var feedbackViewModel: FeedbackViewModel

    private lateinit var feedbackBackButton: ImageView
    private lateinit var feedbackTypeSpinner: MaterialSpinner
    private lateinit var feedbackMessage: TextInputEditText
    private lateinit var feedbackSubmitButton: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val root = inflater.inflate(R.layout.fragment_feedback, container, false)
        val bugReporter = AppContainer.from(activity).bugReporter
        feedbackViewModel = FeedbackViewModel(bugReporter)

        feedbackBackButton = root.findViewById(R.id.feedback_back_button)
        feedbackTypeSpinner = root.findViewById(R.id.feedback_type_spinner)
        feedbackMessage = root.findViewById(R.id.feedback_message)
        feedbackSubmitButton = root.findViewById(R.id.feedback_submit_button)

        // Handle back press.
        feedbackBackButton.setOnClickListener {
            hideKeyboard(root)
            root.findNavController().navigate(R.id.action_go_to_vpn_screen)
        }

        // Add feedback types data.
        initFeedbackTypesDropdown(root, inflater)

        // Handle text change.
        feedbackMessage.onChange { feedbackViewModel.setMessage(it) }

        // Handle submit.
        feedbackSubmitButton.setOnClickListener {
            hideKeyboard(root)
            handleFeedbackSubmit(root)
        }

        return root
    }

    private fun handleFeedbackSubmit(root: View) {
        feedbackSubmitButton.isEnabled = false
        feedbackViewModel.submit()
        root.findNavController().navigate(R.id.action_go_to_vpn_screen)
        showMessage(root.context, getString(R.string.feedback_submit_success))
    }

    private fun initFeedbackTypesDropdown(root: View, inflater: LayoutInflater) {
        val spinner: MaterialSpinner = root.findViewById(R.id.feedback_type_spinner)
        ArrayAdapter.createFromResource(
                inflater.context,
                R.array.feedback_types,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            spinner.selection = 0
            spinner.onItemSelected { feedbackViewModel.setFeedbackType(it) }
        }
    }
}
