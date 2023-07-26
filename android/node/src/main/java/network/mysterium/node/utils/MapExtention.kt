package network.mysterium.node.utils

fun <K, V> Map<out K, V>.toPairs(): Array<Pair<K, V>> =
    this.entries.map { Pair(it.key, it.value) }.toTypedArray()
