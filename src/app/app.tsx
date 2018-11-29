/*
 * Copyright (C) 2018 The 'MysteriumNetwork/mysterion' Authors.
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

import { observable } from 'mobx'
import { observer } from 'mobx-react/native'
import React, { ReactNode } from 'react'
import TequilApiDriver from '../libraries/tequil-api/tequil-api-driver'
import AppLoader from './app-loader'
import CountryList from './countries/country-list'
import Favorites from './countries/favorites'
import ErrorDisplayDelegate from './errors/error-display-delegate'
import Home from './screens/home'
import Loading from './screens/loading'
import ConnectionStore from './stores/connection-store'
import VpnAppState from './vpn-app-state'

type AppProps = {
  tequilAPIDriver: TequilApiDriver,
  connectionStore: ConnectionStore,
  vpnAppState: VpnAppState,
  errorDisplayDelegate: ErrorDisplayDelegate,
  countryList: CountryList,
  favorites: Favorites,
  appLoader: AppLoader
}

@observer
export default class App extends React.Component<AppProps> {
  private readonly tequilAPIDriver: TequilApiDriver
  private readonly connectionStore: ConnectionStore
  private readonly errorDisplayDelegate: ErrorDisplayDelegate
  private readonly vpnAppState: VpnAppState
  private readonly countryList: CountryList
  private readonly favorites: Favorites
  private readonly appLoader: AppLoader
  @observable
  private loaded: boolean

  constructor (props: AppProps) {
    super(props)
    this.tequilAPIDriver = props.tequilAPIDriver
    this.connectionStore = props.connectionStore
    this.errorDisplayDelegate = props.errorDisplayDelegate
    this.vpnAppState = props.vpnAppState
    this.countryList = props.countryList
    this.favorites = props.favorites
    this.appLoader = props.appLoader
    this.loaded = false
  }

  public render (): ReactNode {
    if (!this.loaded) {
      return <Loading/>
    }

    return (
      <Home
        tequilAPIDriver={this.tequilAPIDriver}
        connectionStore={this.connectionStore}
        vpnAppState={this.vpnAppState}
        errorDisplayDelegate={this.errorDisplayDelegate}
        countryList={this.countryList}
        favorites={this.favorites}
      />
    )
  }

  public async componentDidMount () {
    try {
      await this.appLoader.load()
      this.loaded = true
    } catch (err) {
      console.log('App loading failed', err)
    }
  }
}
