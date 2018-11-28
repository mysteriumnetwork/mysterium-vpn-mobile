import TequilapiClientFactory from 'mysterium-tequilapi'
import { Root as RootBase } from 'native-base'
import * as React from 'react'
import { CONFIG } from '../config'
import { FavoritesStorage } from '../libraries/favorites-storage'
import TequilApiDriver from '../libraries/tequil-api/tequil-api-driver'
import TequilApiState from '../libraries/tequil-api/tequil-api-state'
import App from './app'
import AppLoader from './app-loader'
import Connection from './core/connection'
import CountryList from './countries/country-list'
import Favorites from './countries/favorites'
import ErrorDisplayDelegate from './errors/error-display-delegate'
import Logger from './logger'
import ConnectionStore from './stores/connection-store'
import ProposalsStore from './stores/proposals-store'
import VpnAppState from './vpn-app-state'

class Root extends React.PureComponent {
  private readonly api = new TequilapiClientFactory(CONFIG.TEQUILAPI_ADDRESS, CONFIG.TEQUILAPI_TIMEOUT).build()
  private readonly tequilApiState = new TequilApiState()
  private readonly vpnAppState = new VpnAppState()
  private errorDisplayDelegate = new ErrorDisplayDelegate()
  private readonly favoritesStore = new FavoritesStorage()
  private readonly connection = new Connection(this.api, this.tequilApiState)
  private readonly connectionStore = new ConnectionStore(this.connection)
  private readonly proposalsStore = new ProposalsStore(this.api)
  private readonly tequilAPIDriver =
    new TequilApiDriver(this.api, this.tequilApiState, this.connectionStore, this.errorDisplayDelegate)
  private readonly countryList = new CountryList(this.proposalsStore, this.favoritesStore)
  private readonly favorites = new Favorites(this.favoritesStore)
  private readonly appLoader = new AppLoader(this.tequilAPIDriver, this.connection, this.proposalsStore)

  public async componentWillMount () {
    const logger = new Logger(this.tequilApiState, this.vpnAppState, this.connectionStore, this.proposalsStore)
    logger.logObservableChanges()
    await this.favoritesStore.fetch()
  }

  public render () {
    return (
      <RootBase>
        <App
          tequilAPIDriver={this.tequilAPIDriver}
          connectionStore={this.connectionStore}
          vpnAppState={this.vpnAppState}
          errorDisplayDelegate={this.errorDisplayDelegate}
          countryList={this.countryList}
          favorites={this.favorites}
          appLoader={this.appLoader}
        />
      </RootBase>
    )
  }
}

export default Root
