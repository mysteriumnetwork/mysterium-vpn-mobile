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

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric
import network.mysterium.ui.Countries
import network.mysterium.vpn.BuildConfig

class MainApplication : MultiDexApplication() {
    val appContainer = AppContainer()

    override fun onCreate() {
        setupLogging()
        super.onCreate()
        Countries.loadBitmaps()
        Log.i(TAG, "Application started")
    }

    private fun setupLogging() {
        // https://docs.fabric.io/android/crashlytics/build-tools.html?highlight=crashlyticscore
        // Set up Crashlytics, disabled for debug builds
        val crashlyticsKit = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build()

        // Initialize Fabric with the debug-disabled crashlytics.
        Fabric.with(this, crashlyticsKit)

        Crashlytics.setInt("android_sdk_int", android.os.Build.VERSION.SDK_INT)
    }

    companion object {
        private const val TAG = "MainApplication"
    }
}

