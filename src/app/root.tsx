/*
 * Copyright (C) 2018 The 'MysteriumNetwork/mysterium-vpn-mobile' Authors.
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

import { Root } from 'native-base'
import * as React from 'react'
import { FavoritesStorage } from '../libraries/favorites-storage'
import TequilApiDriver from '../libraries/tequil-api/tequil-api-driver'
import AppState from './app-state'
import ErrorDisplayDelegate from './errors/error-display-delegate'
import Logger from './logger'
import HomeScreen from './screens/home'

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
