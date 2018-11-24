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
import ConnectionState from './core/connection-state'
import CountryList from './countries/country-list'
import Favorites from './countries/favorites'
import ErrorDisplayDelegate from './errors/error-display-delegate'
import Logger from './logger'
import VpnAppState from './vpn-app-state'

class Root extends React.PureComponent {
  private readonly api = new TequilapiClientFactory(CONFIG.TEQUILAPI_ADDRESS, CONFIG.TEQUILAPI_TIMEOUT).build()
  private readonly tequilApiState = new TequilApiState()
  private readonly connectionState = new ConnectionState()
  private readonly vpnAppState = new VpnAppState()
  private errorDisplayDelegate = new ErrorDisplayDelegate()
  private readonly tequilAPIDriver =
    new TequilApiDriver(this.api, this.tequilApiState, this.connectionState, this.errorDisplayDelegate)
  private readonly favoritesStore = new FavoritesStorage()
  private readonly connection = new Connection(this.api, this.tequilApiState, this.connectionState)
  private readonly countryList = new CountryList(this.tequilApiState, this.favoritesStore)
  private readonly favorites = new Favorites(this.favoritesStore)
  private readonly appLoader = new AppLoader(this.tequilAPIDriver, this.connection)

  public async componentWillMount () {
    const logger = new Logger(this.tequilApiState, this.vpnAppState, this.connectionState)
    logger.logObservableChanges()
    await this.favoritesStore.fetch()
  }

  public render () {
    return (
      <RootBase>
        <App
          tequilAPIDriver={this.tequilAPIDriver}
          connectionState={this.connectionState}
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
