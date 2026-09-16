package updated.mysterium.vpn.core

import android.util.Log

private const val TAG = "NativeCallback"

/**
 * Runs [block] on behalf of a callback that the native (gomobile) node invokes.
 *
 * The generated callback interfaces -- `ConnectionStatusChangeCallback.onChange`,
 * `BalanceChangeCallback.onChange`, `WireguardTunnelSetup.addRoute` and friends --
 * are declared *without* `throws`, because the Go side of those functions returns
 * no error. gomobile therefore does not check for a pending JNI exception after
 * calling them.
 *
 * The consequence is severe: an exception that escapes such a callback stays
 * pending on the JNI thread, and the *next* call Go makes into Java aborts the
 * whole process -- usually as a native `abort()` inside `go_seq_from_refnum`,
 * far away from the code that actually threw. Google Play saw this as
 * `go_seq_from_refnum` crashes under `WireguardTunnelSetup_Protect` and
 * `ConnectionStatusChangeCallback_OnChange`.
 *
 * So nothing may propagate back into native code. This deliberately catches
 * [Throwable] rather than [Exception]: an `Error` escaping is just as fatal.
 *
 * Callbacks whose generated signature *does* declare `throws` -- currently
 * `WireguardTunnelSetup.protect` and `WireguardTunnelSetup.establish` -- must NOT
 * use this. gomobile turns those exceptions into proper Go errors, which the node
 * can act on; swallowing them here would hide real failures instead.
 */
internal inline fun guardNativeCallback(name: String, block: () -> Unit) {
    try {
        block()
    } catch (throwable: Throwable) {
        Log.e(
            TAG,
            "Exception escaped native callback '$name'. Swallowed: letting it reach " +
                "Go would abort the process on the next native call.",
            throwable
        )
    }
}
