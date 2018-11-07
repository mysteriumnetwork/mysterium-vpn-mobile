import * as React from 'react'
import { FavoritesStorage } from '../libraries/favorites-storage'
import TequilApiDriver from '../libraries/tequil-api/tequil-api-driver'
import App from './app'
import AppState from './app-state'
import ErrorDisplayDelegate from './errors/error-display-delegate'
import Logger from './logger'

class Root extends React.PureComponent {
  private readonly appState = new AppState()
  private errorDisplayDelegate = new ErrorDisplayDelegate()
  private readonly tequilAPIDriver = new TequilApiDriver(this.appState, this.errorDisplayDelegate)
  private readonly favoritesStore = new FavoritesStorage()

  public async componentWillMount () {
    const logger = new Logger(this.appState)
    logger.logObservableChanges()
    await this.favoritesStore.fetch()
  }

  public render () {
    return (
      <App
        tequilAPIDriver={this.tequilAPIDriver}
        appState={this.appState}
        errorDisplayDelegate={this.errorDisplayDelegate}
        favoritesStore={this.favoritesStore}
      />
    )
  }
}

export default Root
