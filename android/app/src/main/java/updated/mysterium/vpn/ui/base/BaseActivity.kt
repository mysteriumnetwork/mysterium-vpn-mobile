package updated.mysterium.vpn.ui.base

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import network.mysterium.vpn.databinding.PopUpTopUpAccountBinding
import org.koin.android.ext.android.inject
import updated.mysterium.vpn.ui.top.up.amount.TopUpAmountActivity

abstract class BaseActivity : AppCompatActivity() {

    private val viewModel: BaseViewModel by inject()
    private lateinit var alertDialogBuilder: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alertDialogBuilder = AlertDialog.Builder(this)
        viewModel.balanceRunningOut.observe(this, {
            showTopUpPopUp()
        })
    }

    fun createPopUp(popUpView: View, cancelable: Boolean): AlertDialog {
        alertDialogBuilder.apply {
            setView(popUpView)
            setCancelable(cancelable)
            create().apply {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window?.setLayout(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
            }
        }
        val dialog = alertDialogBuilder.create()
        dialog.apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
        }
        return dialog
    }

    private fun showTopUpPopUp() {
        val bindingPopUp = PopUpTopUpAccountBinding.inflate(layoutInflater)
        val dialog = createPopUp(bindingPopUp.root, true)
        bindingPopUp.topUpButton.setOnClickListener {
            startActivity(Intent(this, TopUpAmountActivity::class.java))
        }
        bindingPopUp.continueButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}
