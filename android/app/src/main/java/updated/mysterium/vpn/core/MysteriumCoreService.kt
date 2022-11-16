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

package updated.mysterium.vpn.core

import android.content.Context
import android.os.IBinder
import mysterium.MobileNode
import updated.mysterium.vpn.model.proposal.parameters.ProposalViewItem
import updated.mysterium.vpn.notification.NotificationFactory

interface MysteriumCoreService : IBinder {

    suspend fun startNode(): MobileNode
    suspend fun startProviderNode(): MobileNode
    fun isProviderActive(): Boolean
    fun setProviderActive(provider: Boolean)

    fun stopNode()

    fun getActiveProposal(): ProposalViewItem?

    fun setActiveProposal(proposal: ProposalViewItem?)

    fun getDeferredNode(): DeferredNode?

    fun setDeferredNode(node: DeferredNode?)

    fun subscribeToListeners()

    fun manualDisconnect()

    fun getContext(): Context

    fun startForegroundWithNotification(id: Int, notificationFactory: NotificationFactory)

    fun stopForeground()
}
