package updated.mysterium.vpn.ui.report.issue

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import network.mysterium.vpn.BuildConfig
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityReportIssueBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.common.extensions.isEmail
import updated.mysterium.vpn.ui.balance.BalanceViewModel
import updated.mysterium.vpn.ui.base.BaseActivity
import updated.mysterium.vpn.ui.menu.MenuActivity
import updated.mysterium.vpn.ui.wallet.WalletActivity

class ReportIssueActivity : BaseActivity() {

    private lateinit var binding: ActivityReportIssueBinding
    private val viewModel: ReportIssueViewModel by inject()
    private val balanceViewModel: BalanceViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportIssueBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configure()
        subscribeViewModel()
        bindsAction()
        balanceViewModel.getCurrentBalance()
    }

    private fun configure() {
        val version = "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}"
        binding.appVersionValueTextView.text = getString(
            R.string.report_issue_app_version_template,
            version
        )
        binding.nodeVersionValueTextView.text = BuildConfig.NODE_VERSION
    }

    private fun subscribeViewModel() {
        balanceViewModel.balanceLiveData.observe(this, {
            binding.manualConnectToolbar.setBalance(it)
        })
    }

    private fun bindsAction() {
        binding.sendReportButton.setOnClickListener {
            checkCorrectInputData()
        }
        binding.manualConnectToolbar.onBalanceClickListener {
            startActivity(Intent(this, WalletActivity::class.java))
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
        binding.issueEditText.setHintTextColor(getColor(R.color.primary))
        binding.issueEditText.text?.clear()
        binding.issueEditText.gravity = Gravity.START or Gravity.TOP
    }

    private fun emailDefaultState() {
        binding.emailEditText.background = ContextCompat.getDrawable(
            this,
            R.drawable.shape_edit_text
        )
        binding.emailEditText.hint = ""
        binding.emailEditText.requestFocus()
        binding.emailEditText.setHintTextColor(getColor(R.color.primary))
        binding.emailEditText.text?.clear()
        binding.emailEditText.gravity = Gravity.START
    }

    private fun issueErrorState() {
        binding.issueEditText.background = ContextCompat.getDrawable(this, R.drawable.shape_big_edit_text_error)
        binding.issueEditText.hint = getString(R.string.report_issue_error_empty_issue)
        binding.issueEditText.setHintTextColor(getColor(R.color.primary))
        binding.issueEditText.gravity = Gravity.CENTER
    }

    private fun emailErrorState() {
        binding.emailEditText.background = ContextCompat.getDrawable(
            this,
            R.drawable.shape_edit_text_error
        )
        binding.emailEditText.hint = getString(R.string.report_issue_error_incorrect_email)
        binding.emailEditText.setHintTextColor(getColor(R.color.primary))
        binding.emailEditText.text?.clear()
        binding.emailEditText.clearFocus()
        binding.emailEditText.gravity = Gravity.CENTER
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
