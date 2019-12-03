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

package network.mysterium.service.core

import android.net.VpnService
import android.os.Build
import android.util.Log
import mysterium.WireguardTunnelSetup

class WireguardAndroidTunnelSetup(val vpnService: VpnService) : WireguardTunnelSetup {
    var tunBuilder: VpnService.Builder? = null


    override fun setBlocking(blocking: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tunBuilder?.setBlocking(blocking)
        }
    }

    override fun newTunnel() {
        tunBuilder = vpnService.Builder()
    }

    override fun addRoute(route: String, prefixLenth: Long) {
        tunBuilder?.addRoute(route, prefixLenth.toInt())
    }

    override fun addTunnelAddress(ip: String, prefixLength: Long) {
        tunBuilder?.addAddress(ip, prefixLength.toInt())
    }

    override fun protect(socket: Long) {
        val protected = vpnService.protect(socket.toInt())
        Log.i("[Wg tun setup]", "Protecting: ${socket.toInt()}  Success: $protected")
    }

    override fun setMTU(mtu: Long) {
        tunBuilder?.setMtu(mtu.toInt())
    }

    override fun addDNS(ip: String) {
        tunBuilder?.addDnsServer(ip)
    }


    override fun establish(): Long {
        return tunBuilder?.establish()?.detachFd()?.toLong()!!
    }

    override fun setSessionName(session: String) {
        tunBuilder?.setSession(session)
    }
}
