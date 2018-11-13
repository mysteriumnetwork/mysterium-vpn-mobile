package network.mysterium.service.core

import android.os.IBinder

interface MysteriumCoreService : IBinder {
    fun StartTequila()

    fun StopTequila()
}
