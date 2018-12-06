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

import { observer } from 'mobx-react/native'
import React, { ReactNode } from 'react'
import { View } from 'react-native'
import TequilApiDriver from '../libraries/tequil-api/tequil-api-driver'
import AppLoader from './app-loader'
import styles from './app-styles'
import ErrorDropdown from './components/error-dropdown'
import ErrorDisplayDelegate from './errors/error-display-delegate'
import Favorites from './proposals/favorites'
import ProposalList from './proposals/proposal-list'
import LoadingScreen from './screens/loading-screen'
import VpnScreen from './screens/vpn-screen'
import ConnectionStore from './stores/connection-store'
import VpnAppState from './vpn-app-state'

type AppProps = {
  tequilAPIDriver: TequilApiDriver,
  connectionStore: ConnectionStore,
  vpnAppState: VpnAppState,
  errorDisplayDelegate: ErrorDisplayDelegate,
  proposalList: ProposalList,
  favorites: Favorites,
  appLoader: AppLoader
}

@observer
export default class App extends React.Component<AppProps> {
  private readonly tequilAPIDriver: TequilApiDriver
  private readonly connectionStore: ConnectionStore
  private readonly errorDisplayDelegate: ErrorDisplayDelegate
  private readonly vpnAppState: VpnAppState
  private readonly proposalList: ProposalList
  private readonly favorites: Favorites
  private readonly appLoader: AppLoader

  constructor (props: AppProps) {
    super(props)
    this.tequilAPIDriver = props.tequilAPIDriver
    this.connectionStore = props.connectionStore
    this.errorDisplayDelegate = props.errorDisplayDelegate
    this.vpnAppState = props.vpnAppState
    this.proposalList = props.proposalList
    this.favorites = props.favorites
    this.appLoader = props.appLoader
  }

  public render (): ReactNode {
    return (
      <View style={styles.app}>
        {this.renderCurrentScreen()}
        <ErrorDropdown ref={(ref: ErrorDropdown) => this.errorDisplayDelegate.errorDisplay = ref}/>
      </View>
    )
  }

  public async componentDidMount () {
    try {
      await this.appLoader.load()
      this.vpnAppState.markAppAsLoaded()
    } catch (err) {
      console.log('App loading failed', err)
    }
  }

  private renderCurrentScreen (): ReactNode {
    if (!this.vpnAppState.isAppLoaded) {
      return <LoadingScreen/>
    }
    return (
      <VpnScreen
        tequilAPIDriver={this.tequilAPIDriver}
        connectionStore={this.connectionStore}
        vpnAppState={this.vpnAppState}
        proposalList={this.proposalList}
        favorites={this.favorites}
      />
    )
  }

}
