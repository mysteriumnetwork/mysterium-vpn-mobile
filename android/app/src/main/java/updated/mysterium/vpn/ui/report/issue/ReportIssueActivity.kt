package updated.mysterium.vpn.ui.report.issue

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import network.mysterium.vpn.BuildConfig
import network.mysterium.vpn.R
import network.mysterium.vpn.databinding.ActivityReportIssueBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.ui.balance.BalanceViewModel

class ReportIssueActivity : AppCompatActivity() {

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
            checkDescriptionContent()
        }
    }

    private fun checkDescriptionContent() {
        if (binding.issueEditText.text.isNotEmpty()) {
            sendReport()
        } else {
            showMessage(getString(R.string.report_issue_error_empty_issue))
        }
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
