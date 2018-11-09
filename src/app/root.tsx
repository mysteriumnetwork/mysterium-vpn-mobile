import * as React from 'react'
import { FavoritesStorage } from '../libraries/favorites-storage'
import TequilApiDriver from '../libraries/tequil-api/tequil-api-driver'
import TequilApiState from '../libraries/tequil-api/tequil-api-state'
import App from './app'
import ErrorDisplayDelegate from './errors/error-display-delegate'
import Logger from './logger'

class Root extends React.PureComponent {
  private readonly tequilApiState = new TequilApiState()
  private errorDisplayDelegate = new ErrorDisplayDelegate()
  private readonly tequilAPIDriver = new TequilApiDriver(this.tequilApiState, this.errorDisplayDelegate)
  private readonly favoritesStore = new FavoritesStorage()

  public async componentWillMount () {
    const logger = new Logger(this.tequilApiState)
    logger.logObservableChanges()
    await this.favoritesStore.fetch()
  }

  public render () {
    return (
      <App
        tequilAPIDriver={this.tequilAPIDriver}
        tequilApiState={this.tequilApiState}
        errorDisplayDelegate={this.errorDisplayDelegate}
        favoritesStore={this.favoritesStore}
      />
    )
  }
}

export default Root
