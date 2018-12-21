/*
 * Copyright (C) 2018 The 'mysteriumnetwork/mysterium-vpn-mobile' Authors.
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

import TequilapiClientFactory from 'mysterium-tequilapi'
import { Platform } from 'react-native'
import { BugReporter } from '../bug-reporter/bug-reporter'
import ConsoleReporter from '../bug-reporter/console-reporter'
import { FabricReporter } from '../bug-reporter/fabric-reporter'
import IFeedbackReporter from '../bug-reporter/feedback-reporter'
import { CONFIG } from '../config'
import ElkEventSender from '../libraries/statistics/elk-event-sender'
import TequilApiDriver from '../libraries/tequil-api/tequil-api-driver'
import TequilApiState from '../libraries/tequil-api/tequil-api-state'
import IConnectionAdapter from './adapters/connection-adapter'
import ProposalsAdapter from './adapters/proposals-adapter'
import ReactNativeStorage from './adapters/react-native-storage'
import TequilapiConnectionAdapter from './adapters/tequilapi-connection-adapter'
import AppLoader from './app-loader'
import Connection from './domain/connection'
import Terms from './domain/terms'
import { FavoritesStorage } from './favorites-storage'
import MessageDisplayDelegate from './messages/message-display-delegate'
import Favorites from './proposals/favorites'
import ProposalList from './proposals/proposal-list'
import ConnectionStore from './stores/connection-store'
import ProposalsStore from './stores/proposals-store'
import ScreenStore from './stores/screen-store'
import VpnAppState from './vpn-app-state'

class Container {
  public readonly api = new TequilapiClientFactory(CONFIG.TEQUILAPI_ADDRESS, CONFIG.TEQUILAPI_TIMEOUT).build()
  public readonly tequilApiState = new TequilApiState()
  public readonly vpnAppState = new VpnAppState()
  public readonly messageDisplayDelegate = new MessageDisplayDelegate()
  public readonly favoritesStore = this.buildFavoriteStorage()

  // adapters
  public readonly connectionAdapter: IConnectionAdapter = new TequilapiConnectionAdapter(this.api)
  public readonly proposalsAdapter = new ProposalsAdapter(this.api)
  public readonly eventSender = new ElkEventSender()

  // domain
  public readonly connection = new Connection(this.connectionAdapter, this.tequilApiState, this.eventSender)
  public readonly terms: Terms = this.buildTerms()

  // stores
  public readonly connectionStore = new ConnectionStore(this.connection)
  public readonly proposalsStore = new ProposalsStore(this.proposalsAdapter)
  public readonly screenStore = new ScreenStore()
  public readonly tequilAPIDriver =
    new TequilApiDriver(this.api, this.tequilApiState, this.connection, this.messageDisplayDelegate)

  public readonly proposalList = new ProposalList(this.proposalsStore, this.favoritesStore)
  public readonly favorites = new Favorites(this.favoritesStore)
  public readonly appLoader = new AppLoader(this.tequilAPIDriver, this.connection, this.proposalsStore)
  public readonly bugReporter: BugReporter
  public readonly feedbackReporter: IFeedbackReporter

  constructor () {
    const reporter = this.buildBugReporter()
    this.bugReporter = reporter
    this.feedbackReporter = reporter
  }

  private buildBugReporter () {
    return this.useFabric() ? new FabricReporter() : new ConsoleReporter()
  }

  private buildFavoriteStorage () {
    const FAVORITE_KEY = '@Favorites:KEY'
    return new FavoritesStorage(new ReactNativeStorage(FAVORITE_KEY))
  }

  private buildTerms () {
    const TERMS_KEY = '@MainStore:acceptedTermsVersion'
    const CURRENT_TERMS_VERSION = 1
    return new Terms(new ReactNativeStorage<number>(TERMS_KEY), CURRENT_TERMS_VERSION)
  }

  private useFabric () {
    return Platform.OS === 'android' && !__DEV__
  }
}

export default Container
