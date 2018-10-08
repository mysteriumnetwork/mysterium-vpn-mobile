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

import React from 'react'
import { Text, View, Button } from 'react-native'
import styles from './app-styles'
import { CONFIG } from '../config'
import Stats from './stats'
import AppTequilapi from './app-tequilapi'
import Proposals from './proposals'
import MysteriumClient from '../libraries/mysterium-client'
import { store } from '../store/tequilapi-store'
import { observer } from 'mobx-react/native'

@observer
export default class App extends AppTequilapi {
  constructor(props: any) {
    super(props)

    // Bind local functions
    this.connectDisconnect = this.connectDisconnect.bind(this)
  }

  public render() {
    const isReady = store.isReady
    const isConnected = store.isConnected
    const connectText = isReady
      ? isConnected
        ? 'disconnect'
        : 'connect'
      : CONFIG.TEXTS.UNKNOWN_STATUS
    return (
      // @ts-ignore
      <View style={styles.container} transform={[{ scaleX: 2 }, { scaleY: 2 }]}>
        <Text>
          {store.ConnectionStatus
            ? store.ConnectionStatus.status
            : CONFIG.TEXTS.UNKNOWN}
        </Text>
        <Text>IP: {store.IP}</Text>
        <Proposals
          proposalsFetcher={this.proposalFetcher}
          proposalsStore={store}
        />
        <Button
          title={connectText}
          onPress={this.connectDisconnect}
          disabled={!isReady}
        />
        {store.Statistics ? <Stats {...store.Statistics} /> : null}
      </View>
    )
  }

  /***
   * Refreshes connection state, ip and unlocks identity.
   * Starts periodic state refreshing
   * Called once after first rendering.
   */
  public async componentDidMount() {
    await this.unlock()

    // TODO: remove it later, serviceStatus is used only for native call test
    const mysteriumClient = new MysteriumClient()
    const serviceStatus = await mysteriumClient.startService(4050)
    console.log('serviceStatus', serviceStatus)
  }

  /***
   * Connects or disconnects to VPN server, depends on current connection state.
   * Is connection state is unknown - does nothing
   * @returns {Promise<void>}
   */
  private async connectDisconnect() {
    if (!store.isReady) {
      return
    }

    if (store.isConnected) {
      await this.disconnect()
    } else {
      await this.connect()
    }
  }
}
