package updated.mysterium.vpn.ui.manual.connect

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import network.mysterium.vpn.databinding.ToolbarBaseConnectBinding

abstract class BaseConnectActivity : AppCompatActivity() {

    private lateinit var toolbarBinding: ToolbarBaseConnectBinding

    abstract fun configureToolbar(toolbarBinding: ToolbarBaseConnectBinding)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarBinding = ToolbarBaseConnectBinding.inflate(layoutInflater)
        setContentView(toolbarBinding.root)
        bindsAction()
    }

    override fun onStart() {
        super.onStart()
        configureToolbar(toolbarBinding)
    }

    open fun leftToolbarButtonClicked() {
        finish()
    }

    open fun rightToolbarButtonClicked() {
        // empty
    }

    protected fun changeLeftIcon(drawableResId: Int) {
        val icon = ContextCompat.getDrawable(this, drawableResId)
        toolbarBinding.leftButton.setImageDrawable(icon)
    }

    protected fun changeRightIcon(drawableResId: Int) {
        val icon = ContextCompat.getDrawable(this, drawableResId)
        toolbarBinding.rightButton.visibility = View.VISIBLE
        toolbarBinding.rightButton.setImageDrawable(icon)
    }

    private fun bindsAction() {
        toolbarBinding.leftButton.setOnClickListener {
            leftToolbarButtonClicked()
        }
        toolbarBinding.rightButton.setOnClickListener {
            rightToolbarButtonClicked()
        }
    }
}
