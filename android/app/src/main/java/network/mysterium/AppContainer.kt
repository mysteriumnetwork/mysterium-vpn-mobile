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

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.room.Room
import kotlinx.coroutines.CompletableDeferred
import network.mysterium.db.AppDatabase
import network.mysterium.logging.BugReporter
import network.mysterium.service.core.DeferredNode
import network.mysterium.service.core.MysteriumCoreService
import network.mysterium.service.core.NodeRepository
import network.mysterium.ui.ProposalsViewModel
import network.mysterium.ui.SharedViewModel
import network.mysterium.ui.TermsViewModel

class AppContainer {
    lateinit var appDatabase: AppDatabase
    lateinit var nodeRepository: NodeRepository
    lateinit var sharedViewModel: SharedViewModel
    lateinit var proposalsViewModel: ProposalsViewModel
    lateinit var termsViewModel: TermsViewModel
    lateinit var bugReporter: BugReporter
    lateinit var deferredMysteriumCoreService: CompletableDeferred<MysteriumCoreService>

    fun init(ctx: Context, deferredNode: DeferredNode, mysteriumCoreService: CompletableDeferred<MysteriumCoreService>) {
        appDatabase = Room.databaseBuilder(
                ctx,
                AppDatabase::class.java, "mysteriumvpn"
        ).build()

        deferredMysteriumCoreService = mysteriumCoreService
        bugReporter = BugReporter()
        nodeRepository = NodeRepository(deferredNode)
        sharedViewModel = SharedViewModel(nodeRepository, bugReporter, deferredMysteriumCoreService)
        proposalsViewModel = ProposalsViewModel(sharedViewModel, nodeRepository, appDatabase)
        termsViewModel = TermsViewModel(appDatabase)
    }

    companion object {
        fun from(activity: FragmentActivity?): AppContainer {
            return (activity!!.application as MainApplication).appContainer
        }
    }
}
