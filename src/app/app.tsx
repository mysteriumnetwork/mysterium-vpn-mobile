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
import TequilAPIDriver from '../libraries/tequilapi/tequilAPI-driver'
import AppState from './app-state'
import styles from './app-styles'
import ButtonConnect from './components/button-connect'
import ConnectionStatus from './components/connection-status'
import ErrorDropdown from './components/error-dropdown'
import ProposalsDropdown, { ProposalsState } from './components/proposals-dropdown'
import Stats from './components/stats'
import ErrorDisplayDelegate from './errors/error-display-delegate'

type AppProps = {
  tequilAPIDriver: TequilAPIDriver,
  appState: AppState,
  errorDisplayDelegate: ErrorDisplayDelegate,
  favoritesStore: FavoritesStorage
}

@observer
export default class App extends React.Component<AppProps> {
  private readonly tequilAPIDriver: TequilAPIDriver
  private readonly appState: AppState
  private readonly errorDisplayDelegate: ErrorDisplayDelegate

  constructor (props: AppProps) {
    super(props)
    this.tequilAPIDriver = props.tequilAPIDriver
    this.appState = props.appState
    this.errorDisplayDelegate = props.errorDisplayDelegate
  }

  public render (): ReactNode {
    return (
      <View style={styles.container}>
        <Image
          style={styles.imageBackground}
          source={require('../assets/background-logo.png')}
          resizeMode="contain"
        />

        <ConnectionStatus status={this.appState.ConnectionStatus ? this.appState.ConnectionStatus.status : undefined}/>
        <Text style={styles.textIp}>IP: {this.appState.IP || CONFIG.TEXTS.IP_UPDATING}</Text>

        <View style={styles.controls}>
          <ProposalsDropdown
            favoritesStore={this.props.favoritesStore}
            proposalsFetcher={this.tequilAPIDriver.proposalFetcher}
            proposalsState={this.appState as ProposalsState}
          />
          <ButtonConnect
            title={this.buttonText}
            disabled={!this.buttonEnabled}
            onPress={() => this.connectOrDisconnect()}
          />
        </View>
        {this.appState.Statistics ? <Stats style={styles.footer} {...this.appState.Statistics} /> : null}
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

    // TODO: remove it later, serviceStatus is used only for native call test
    const serviceStatus = await mysteriumClient.startService(4050)
    console.log('serviceStatus', serviceStatus)
  }

  private get buttonEnabled (): boolean {
    return this.appState.isReady
  }

  private get buttonText (): string {
    const isReady = this.appState.isReady
    const isConnected = this.appState.isConnected
    return isReady
      ? isConnected
        ? 'Disconnect'
        : 'Connect'
      : CONFIG.TEXTS.CONNECTION_STATUS.UNKNOWN
  }

  /***
   * Connects or disconnects to VPN server, depends on current connection state.
   * Is connection state is unknown - does nothing
   */
  private async connectOrDisconnect () {
    if (!this.appState.isReady) {
      return
    }

    if (this.appState.isConnected) {
      await this.tequilAPIDriver.disconnect()
    } else {
      await this.tequilAPIDriver.connect()
    }
  }
}
