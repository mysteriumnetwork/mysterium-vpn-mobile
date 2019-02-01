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

package network.mysterium.logging

import cat.ereza.logcatreporter.LogcatReporter
import com.crashlytics.android.Crashlytics
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class FeedbackException(message: String) : Exception(message)

class BugReporter(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    override fun getName(): String {
        return "BugReporter"
    }

    @ReactMethod
    fun logException(value: String) {
        LogcatReporter.reportExceptionWithLogcat(RuntimeException(value))
    }

    @ReactMethod
    fun setUserIdentifier(userIdentifier: String) {
        Crashlytics.setUserIdentifier(userIdentifier)
    }

    @ReactMethod
    fun sendFeedback(type: String, message: String) {
        LogcatReporter.reportExceptionWithLogcat(FeedbackException(type + ":" + message))
    }
}
