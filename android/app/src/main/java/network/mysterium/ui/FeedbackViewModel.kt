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

package network.mysterium.ui

import androidx.lifecycle.ViewModel
import mysterium.SendFeedbackRequest
import network.mysterium.service.core.NodeRepository

class FeedbackViewModel(private val nodeRepository: NodeRepository): ViewModel() {
    private var email = ""
    private var message = ""

    fun setMessage(msg: String) {
        message = msg
    }

    fun isMessageSet(): Boolean {
        return message != ""
    }

    suspend fun submit() {
        val description = "Platform: Android, Message: $message"
        val req = SendFeedbackRequest()
        req.email = email
        req.description = description
        nodeRepository.sendFeedback(req)
    }

    fun setEmail(email: String) {
        this.email = email
    }
}
