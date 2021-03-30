package updated.mysterium.vpn.ui.base

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import network.mysterium.vpn.databinding.PopUpTopUpAccountBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.ui.top.up.amount.TopUpAmountActivity

abstract class BaseActivity : AppCompatActivity() {

    private val viewModel: BaseViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.balanceRunningOut.observe(this, {
            showTopUpPopUp()
        })
    }

    private fun showTopUpPopUp() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val bindingPopUp = PopUpTopUpAccountBinding.inflate(layoutInflater)
        alertDialogBuilder.setView(bindingPopUp.root)
        alertDialogBuilder.setCancelable(true)
        val dialog = alertDialogBuilder.create()
        dialog.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            show()
        }
        bindingPopUp.topUpButton.setOnClickListener {
            startActivity(Intent(this, TopUpAmountActivity::class.java))
        }
        bindingPopUp.continueButton.setOnClickListener {
            dialog.dismiss()
        }
    }
}
