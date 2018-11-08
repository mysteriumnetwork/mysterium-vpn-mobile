import * as React from 'react'
import { FavoritesStorage } from '../libraries/favorites-storage'
import TequilApiDriver from '../libraries/tequil-api/tequil-api-driver'
import HomeScreen from './screens/home'
import AppState from './app-state'
import ErrorDisplayDelegate from './errors/error-display-delegate'
import Logger from './logger'
import { Root } from 'native-base'

class RootComponent extends React.PureComponent {
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
      <Root>
        <HomeScreen
          tequilAPIDriver={this.tequilAPIDriver}
          appState={this.appState}
          errorDisplayDelegate={this.errorDisplayDelegate}
          favoritesStore={this.favoritesStore}
        />
      </Root>
    )
  }
}

export default RootComponent
