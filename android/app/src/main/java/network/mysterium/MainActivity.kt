/*
 * Copyright (C) 2019 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package network.mysterium

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.VpnService
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import network.mysterium.service.core.DeferredNode
import network.mysterium.service.core.MysteriumAndroidCoreService
import network.mysterium.service.core.MysteriumCoreService
import network.mysterium.vpn.R

class MainActivity : AppCompatActivity() {
    private lateinit var appContainer: AppContainer
    private var deferredNode = DeferredNode()
    private var deferredMysteriumCoreService = CompletableDeferred<MysteriumCoreService>()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i(TAG, "Service disconnected")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.i(TAG, "Service connected")
            deferredMysteriumCoreService.complete(service as MysteriumCoreService)
            deferredNode.start(service) {err ->
                if (err != null) {
                    showNodeStarError()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize app DI container.
        appContainer = (application as MainApplication).appContainer
        appContainer.init(applicationContext, deferredNode, deferredMysteriumCoreService)

        // Bind VPN service.
        ensureVpnServicePermission()
        bindMysteriumService()

        // Load initial state without blocking main UI thread.
        CoroutineScope(Dispatchers.Main).launch {
            // Load favorite proposals from local database.
            val favoriteProposals = appContainer.proposalsViewModel.loadFavoriteProposals()

            // Load initial data like current location, statistics, active proposal (if any).
            appContainer.sharedViewModel.load(favoriteProposals)

            // Load initial proposals.
            appContainer.proposalsViewModel.load()
        }

        // Navigate to main vpn screen and check if terms are accepted in separate coroutine
        // so it does not block main thread.
        navigate(R.id.main_vpn_fragment)
        CoroutineScope(Dispatchers.Main).launch {
            val termsAccepted = appContainer.termsViewModel.checkTermsAccepted()
            if (!termsAccepted) {
                navigate(R.id.terms_fragment)
            }
        }
    }

    override fun onDestroy() {
        unbindMysteriumService()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            VPN_SERVICE_REQUEST -> {
                if (resultCode != Activity.RESULT_OK) {
                    Log.w(TAG, "User forbidden VPN service")
                    Toast.makeText(this, "VPN connection has to be granted for MysteriumVPN to work.", Toast.LENGTH_LONG).show()
                    finish()
                    return
                }
                Log.i(TAG, "User allowed VPN service")
            }
        }
    }

    private fun showNodeStarError() {
        Toast.makeText(this, "Failed to initialize. Please relaunch app.", Toast.LENGTH_LONG).show()
    }

    private fun bindMysteriumService() {
        Log.i(TAG, "Binding service")
        Intent(this, MysteriumAndroidCoreService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun unbindMysteriumService() {
        Log.i(TAG, "Unbinding service")
        unbindService(serviceConnection)
    }

    private fun ensureVpnServicePermission() {
        val intent: Intent = VpnService.prepare(this) ?: return
        startActivityForResult(intent, VPN_SERVICE_REQUEST)
    }

    private fun navigate(destination: Int) {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
        navGraph.startDestination = destination
        navController.graph = navGraph
    }

    companion object {
        private const val VPN_SERVICE_REQUEST = 1
        private const val TAG = "MainActivity"
    }
}
