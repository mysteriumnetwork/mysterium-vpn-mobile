package network.mysterium.node.utils

import kotlinx.coroutines.Job

fun Job?.cancelCatching() = runCatching {
    this?.cancel()
}
