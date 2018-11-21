package network.mysterium.service.core

import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import mysterium.Openvpn3TunnelSetup

class Openvpn3AndroidTunnelSetupBridge(private val vpnService: VpnService) : Openvpn3TunnelSetup {
  override fun socketProtect(socket: Long): Boolean {
    val succeeded = vpnService.protect(socket.toInt())
    Log.i(TAG, "Protecting socket: ${socket.toInt()}  res: $succeeded")
    return succeeded
  }

  private var builder: VpnService.Builder? = null

  private var tunnelFd: Int? = null

  override fun addAddress(
      address: String,
      prefixLength: Long,
      gateway: String,
      ipv6: Boolean,
      net30: Boolean
  ): Boolean {

    builder?.addAddress(address, prefixLength.toInt())
    return builder != null
  }

  override fun addDnsServer(address: String, ipv6: Boolean): Boolean {
    builder?.addDnsServer(address)
    return builder != null
  }

  override fun addProxyBypass(bypassHost: String): Boolean {
    return false
  }

  override fun addRoute(address: String, prefixLength: Long, metric: Long, ipv6: Boolean): Boolean {
    builder?.addRoute(address, prefixLength.toInt())
    return builder != null
  }

  override fun addSearchDomain(domain: String): Boolean {
    builder?.addSearchDomain(domain)
    return builder != null
  }

  override fun addWinsServer(address: String): Boolean {
    return false
  }

  @Throws(Exception::class)
  override fun establish(): Long {
    tunnelFd = builder?.establish()?.detachFd()
    return tunnelFd?.toLong() ?: -1
  }

  override fun establishLite() {
    //whatever that means
  }

  override fun excludeRoute(address: String, prefixLength: Long, metric: Long, ipv6: Boolean): Boolean {
    return false
  }

  override fun newBuilder(): Boolean {
    builder = vpnService.Builder()
    return true
  }

  override fun persist(): Boolean {
    return false
  }

  override fun rerouteGw(ipv4: Boolean, ipv6: Boolean, flags: Long): Boolean {
    Log.i(TAG, "Flags for gw reroute: " + flags.toString(16))
    builder?.addRoute("0.0.0.0", 1)
    builder?.addRoute("128.0.0.0", 1)
    return builder != null
  }

  override fun setAdapterDomainSuffix(name: String): Boolean {
    return false
  }

  override fun setBlockIpv6(ipv6Block: Boolean): Boolean {
    return false
  }

  override fun setLayer(layer: Long): Boolean {
    return layer == 3L
  }

  override fun setMtu(mtu: Long): Boolean {
    builder?.setMtu(mtu.toInt())
    return builder != null
  }

  override fun setProxyAutoConfigUrl(url: String): Boolean {
    return false
  }

  override fun setProxyHttp(host: String, port: Long): Boolean {
    return false
  }

  override fun setProxyHttps(host: String, port: Long): Boolean {
    return false
  }

  override fun setRemoteAddress(ipAddress: String, ipv6: Boolean): Boolean {
    //look into internet
    return true
  }

  override fun setRouteMetricDefault(metric: Long): Boolean {
    return false
  }

  override fun setSessionName(name: String): Boolean {
    builder?.setSession(name)
    return builder != null
  }

  override fun teardown(disconnect: Boolean) {
    tunnelFd?.let {
      ParcelFileDescriptor.adoptFd(it).close()
    }
  }

  companion object {
    private const val TAG = "Openvpn3 setup bridge"
  }
}
