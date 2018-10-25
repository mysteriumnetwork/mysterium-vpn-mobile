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
import TequilaRider from '../libraries/tequila/rider'
import TequilaState from '../libraries/tequila/state'
import styles from './app-styles'
import Proposals from './proposals'
import Stats from './stats'

type TequilapiProp = {
  tequilaRider: TequilaRider,
  tequilaState: TequilaState
}

@observer
export default class App extends React.Component<TequilapiProp> {
  private readonly tequilaRider: TequilaRider
  private readonly tequilaState: TequilaState

  constructor (props: TequilapiProp) {
    super(props)
    this.tequilaRider = props.tequilaRider
    this.tequilaState = props.tequilaState
  }

  public render (): ReactNode {
    return (
      // @ts-ignore TODO remove ignore or transform
      <View style={styles.container} transform={[{ scaleX: 2 }, { scaleY: 2 }]}>
        <Text>
          {this.tequilaState.ConnectionStatus
            ? this.tequilaState.ConnectionStatus.status
            : CONFIG.TEXTS.UNKNOWN_STATUS}
        </Text>
        <Text>IP: {this.tequilaState.IP || CONFIG.TEXTS.IP_UPDATING}</Text>
        <Proposals
          proposalsFetcher={this.tequilaRider.proposalFetcher}
          proposalsStore={this.tequilaState}
        />
        <Button
          title={this.buttonText}
          disabled={!this.buttonEnabled}
          onPress={() => this.connectOrDisconnect()}
        />
        {this.tequilaState.Statistics ? <Stats {...this.tequilaState.Statistics} /> : null}
      </View>
    )
  }

  /***
   * Refreshes connection state, ip and unlocks identity.
   * Starts periodic state refreshing
   * Called once after first rendering.
   */
  public async componentDidMount () {
    await this.tequilaRider.unlock()

    // TODO: remove it later, serviceStatus is used only for native call test
    const serviceStatus = await mysteriumClient.startService(4050)
    console.log('serviceStatus', serviceStatus)
  }

  private get buttonEnabled (): boolean {
    return this.tequilaState.isReady
  }

  private get buttonText (): string {
    const isReady = this.tequilaState.isReady
    const isConnected = this.tequilaState.isConnected
    return isReady
      ? isConnected
        ? 'disconnect'
        : 'connect'
      : CONFIG.TEXTS.UNKNOWN_STATUS
  }

  /***
   * Connects or disconnects to VPN server, depends on current connection state.
   * Is connection state is unknown - does nothing
   */
  private async connectOrDisconnect () {
    if (!this.tequilaState.isReady) {
      return
    }

    if (this.tequilaState.isConnected) {
      await this.tequilaRider.disconnect()
    } else {
      await this.tequilaRider.connect()
    }
  }
}
