package updated.mysterium.vpn.ui.report.issue

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import network.mysterium.vpn.BuildConfig
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityReportIssueBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.extensions.isEmail
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.manual.connect.home.HomeActivity
import updated.mysterium.vpn.ui.menu.MenuActivity

class ReportIssueActivity : BaseActivity() {

    private lateinit var binding: ActivityReportIssueBinding
    private val viewModel: ReportIssueViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportIssueBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        bindsAction()
    }

    private fun configure() {
        val version = "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"
        binding.appVersionValueTextView.text = getString(
            R.string.report_issue_app_version_template,
            version
        )
        binding.nodeVersionValueTextView.text = BuildConfig.NODE_VERSION
    }

    private fun bindsAction() {
        binding.sendReportButton.setOnClickListener {
            checkCorrectInputData()
        }
        binding.manualConnectToolbar.onConnectClickListener {
            val intent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(intent)
        }
        binding.manualConnectToolbar.onLeftButtonClicked {
            val intent = Intent(this, MenuActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(intent)
        }
        binding.emailEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                emailDefaultState()
            }
        }
        binding.issueEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                issueDefaultState()
            }
        }
    }

    private fun checkCorrectInputData() {
        val isEmailCorrect = checkEmail()
        checkDescriptionContent(isEmailCorrect)
    }

    private fun checkEmail() = if (
        !binding.emailEditText.text.toString().isEmail() &&
        binding.emailEditText.text.toString() != ""
    ) {
        emailErrorState()
        false
    } else {
        true
    }

    private fun checkDescriptionContent(isEmailCorrect: Boolean) {
        if (binding.issueEditText.text != null && binding.issueEditText.text.toString() != "") {
            if (isEmailCorrect) {
                sendReport()
                binding.sendReportButton.isClickable = false
            }
        } else {
            issueErrorState()
        }
    }

    private fun issueDefaultState() {
        binding.issueEditText.background = ContextCompat.getDrawable(
            this,
            R.drawable.shape_big_edit_text
        )
        binding.issueEditText.hint = ""
        binding.issueEditText.gravity = Gravity.CENTER_VERTICAL or Gravity.TOP
        binding.issueErrorIcon.visibility = View.INVISIBLE
    }

    private fun emailDefaultState() {
        binding.emailEditText.background = ContextCompat.getDrawable(
            this,
            R.drawable.shape_edit_text
        )
        binding.emailEditText.hint = ""
        binding.emailEditText.requestFocus()
        binding.emailEditText.gravity = Gravity.CENTER_VERTICAL
        binding.emailErrorIcon.visibility = View.INVISIBLE
    }

    private fun issueErrorState() {
        binding.issueEditText.background = ContextCompat.getDrawable(
            this, R.drawable.shape_big_edit_text_error
        )
        binding.issueEditText.hint = getString(R.string.report_issue_error_empty_issue)
        binding.issueEditText.setHintTextColor(getColor(R.color.menu_subtitle_light_pink))
        binding.issueEditText.gravity = Gravity.CENTER
        binding.issueEditText.text?.clear()
        binding.issueEditText.clearFocus()
        binding.issueErrorIcon.visibility = View.VISIBLE
    }

    private fun emailErrorState() {
        binding.emailEditText.background = ContextCompat.getDrawable(
            this, R.drawable.shape_edit_text_error
        )
        binding.emailEditText.hint = getString(R.string.report_issue_error_incorrect_email)
        binding.emailEditText.setHintTextColor(getColor(R.color.menu_subtitle_light_pink))
        binding.emailEditText.text?.clear()
        binding.emailEditText.clearFocus()
        binding.emailEditText.gravity = Gravity.CENTER
        binding.emailErrorIcon.visibility = View.VISIBLE
    }

    private fun sendReport() {
        viewModel.sendReport(
            email = binding.emailEditText.text.toString(),
            content = binding.issueEditText.text.toString()
        ).observe(this, { result ->
            result.onSuccess {
                showMessage(getString(R.string.report_issue_success))
                finish()
            }
            result.onFailure {
                binding.sendReportButton.isClickable = true
                showMessage(getString(R.string.report_issue_error_request))
            }
        })
    }

    private fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}
