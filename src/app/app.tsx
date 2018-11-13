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
import { Image, Text, View } from 'react-native'
import { CONFIG } from '../config'
import { FavoritesStorage } from '../libraries/favorites-storage'
import { mysteriumClient } from '../libraries/mysterium-client'
import TequilApiDriver from '../libraries/tequil-api/tequil-api-driver'
import TequilApiState from '../libraries/tequil-api/tequil-api-state'
import styles from './app-styles'
import ButtonConnect from './components/button-connect'
import ConnectionStatus from './components/connection-status'
import ErrorDropdown from './components/error-dropdown'
import ProposalsDropdown from './components/proposals-dropdown'
import Stats from './components/stats'
import ErrorDisplayDelegate from './errors/error-display-delegate'
import VpnAppState from './vpn-app-state'

type AppProps = {
  tequilAPIDriver: TequilApiDriver,
  tequilApiState: TequilApiState,
  vpnAppState: VpnAppState,
  errorDisplayDelegate: ErrorDisplayDelegate,
  favoritesStore: FavoritesStorage
}

@observer
export default class App extends React.Component<AppProps> {
  private readonly tequilAPIDriver: TequilApiDriver
  private readonly tequilApiState: TequilApiState
  private readonly errorDisplayDelegate: ErrorDisplayDelegate
  private readonly vpnAppState: VpnAppState

  constructor (props: AppProps) {
    super(props)
    this.tequilAPIDriver = props.tequilAPIDriver
    this.tequilApiState = props.tequilApiState
    this.errorDisplayDelegate = props.errorDisplayDelegate
    this.vpnAppState = props.vpnAppState
  }

  public render (): ReactNode {
    return (
      <View style={styles.container}>
        <Image
          style={styles.imageBackground}
          source={require('../assets/background-logo.png')}
          resizeMode="contain"
        />

        <ConnectionStatus status={this.tequilApiState.connectionStatus.status}/>

        <Text style={styles.textIp}>IP: {this.tequilApiState.IP || CONFIG.TEXTS.IP_UPDATING}</Text>

        <View style={styles.controls}>
          <ProposalsDropdown
            favoritesStore={this.props.favoritesStore}
            proposalsFetcher={this.tequilAPIDriver.proposalFetcher}
            proposals={this.tequilApiState.proposals}
            selectedProviderId={this.vpnAppState.selectedProviderId}
            setSelectedProviderId={(value) => this.vpnAppState.selectedProviderId = value}
          />
          <ButtonConnect
            connectionStatus={this.tequilApiState.connectionStatus.status}
            connect={this.tequilAPIDriver.connect.bind(this.tequilAPIDriver, this.vpnAppState.selectedProviderId)}
            disconnect={this.tequilAPIDriver.disconnect.bind(this.tequilAPIDriver)}
          />
        </View>
        {this.tequilApiState.connectionStatistics
          ? <Stats style={styles.footer} {...this.tequilApiState.connectionStatistics} />
          : null}
        <ErrorDropdown ref={(ref: ErrorDropdown) => this.errorDisplayDelegate.errorDisplay = ref}/>
      </View>
    )
  }

  /***
   * Refreshes connection state, ip and unlocks identity.
   * Starts periodic state refreshing
   * Called once after first rendering.
   */
  public async componentDidMount () {
    await this.tequilAPIDriver.unlock()
  }
}
