package network.mysterium.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import network.mysterium.MainApplication
import network.mysterium.vpn.R

class ForceUpdateFragment : Fragment() {
    private lateinit var updateBtn: MaterialButton
    private lateinit var currentVersion: TextView
    private lateinit var requiredVersion: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_force_update, container, false)
    }

    override fun onViewCreated(root: View, savedInstanceState: Bundle?) {
        super.onViewCreated(root, savedInstanceState)

        val appContainer = (activity!!.application as MainApplication).appContainer
        val versionViewModel = appContainer.versionViewModel

        updateBtn = root.findViewById(R.id.force_update_btn)
        currentVersion = root.findViewById(R.id.force_update_current_version)
        requiredVersion = root.findViewById(R.id.force_update_required_version)

        updateBtn.setOnClickListener {
            handleUpdatePress(root.context)
        }

        currentVersion.text = versionViewModel.appVersion()
        requiredVersion.text = versionViewModel.remoteVersion

        onBackPress {
            // Do nothing.
        }
    }

    private fun handleUpdatePress(ctx: Context) {
        val appPackageName: String = ctx.packageName

        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (anfe: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }
}
