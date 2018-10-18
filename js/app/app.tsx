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
import { Button, Text, View } from 'react-native'
import { CONFIG } from '../config'
import { mysteriumClient } from '../libraries/mysterium-client'
import { store } from '../store/app-store'
import styles from './app-styles'
import AppTequilapi from './app-tequilapi'
import Proposals from './proposals'
import Stats from './stats'

@observer
export default class App extends AppTequilapi {
  public render(): ReactNode {
    return (
      // @ts-ignore TODO remove ignore or transform
      <View style={styles.container} transform={[{ scaleX: 2 }, { scaleY: 2 }]}>
        <Text>
          {store.ConnectionStatus
            ? store.ConnectionStatus.status
            : CONFIG.TEXTS.UNKNOWN_STATUS}
        </Text>
        <Text>IP: {store.IP || CONFIG.TEXTS.IP_UPDATING}</Text>
        <Proposals
          proposalsFetcher={this.proposalFetcher}
          proposalsStore={store}
        />
        <Button
          title={this.buttonText}
          disabled={!this.buttonEnabled}
          onPress={() => this.connectDisconnect()}
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
    const serviceStatus = await mysteriumClient.startService(4050)
    console.log('serviceStatus', serviceStatus)
  }

  private get buttonEnabled(): boolean {
    return store.isReady
  }

  private get buttonText(): string {
    const isReady = store.isReady
    const isConnected = store.isConnected
    return isReady
      ? isConnected
        ? 'disconnect'
        : 'connect'
      : CONFIG.TEXTS.UNKNOWN_STATUS
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
