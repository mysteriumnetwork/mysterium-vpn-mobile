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
import io.intercom.android.sdk.Intercom
import network.mysterium.logging.BugReporter
import network.mysterium.ui.Countries

class MainApplication : MultiDexApplication() {

    val appContainer = AppContainer()

    override fun onCreate() {
        BugReporter.init()
        super.onCreate()
        Countries.loadBitmaps()
        setupIntercom()
        Log.i(TAG, "Application started")
    }

    private fun setupIntercom() {
        Intercom.initialize(this, "android_sdk-e480f3fce4f2572742b13c282c453171c1715516", "h7hlm9on")
    }

    companion object {
        private const val TAG = "MainApplication"
    }
}

