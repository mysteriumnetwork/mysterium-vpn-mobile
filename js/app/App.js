/*
 * Copyright (C) 2017 The 'MysteriumNetwork/mysterion' Authors.
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

// @flow

import React from 'react'
import { Text, View, Button, NativeModules } from 'react-native'
import ConnectionStatusEnum from '../libraries/mysterium-tequilapi/dto/connection-status-enum'
import styles from './App-styles'
import CONFIG from '../config'
import Stats from './Stats'
import AppApi from './App-api'
import Proposals from './Proposals'

export default class App extends AppApi {
  constructor (props) {
    super(props)

    // Bind local functions
    this.connectDisconnect = this.connectDisconnect.bind(this)
    this.isReady = this.isReady.bind(this)
    this.onProposalSelected = this.onProposalSelected.bind(this)
  }

  /***
   * Refreshes connection state, ip and unlocks identity.
   * Starts periodic state refreshing
   * Called once after first rendering.
   */
  componentDidMount () {
    this.refresh(true)
    setInterval(this.refresh.bind(this), CONFIG.REFRESH_INTERVALS.INTERVAL_MS)
  }

  /***
   * Checks ability to connect/disconnect
   * @returns {boolean} - true if where is no uncompleted operations
   */
  isReady () {
    const s = this.state
    return s.identityId && s.connection &&
      ((s.connection.status === ConnectionStatusEnum.NOT_CONNECTED && s.selectedProviderId) ||
        s.connection.status === ConnectionStatusEnum.CONNECTED)
  }

  /***
   * Checks are you already connected to VPN server
   * @returns {boolean} - true if you connected, false if not or state is unknown
   */
  isConnected () {
    const c = this.state.connection
    return c && c.status === ConnectionStatusEnum.CONNECTED
  }

  /***
   * Connects or disconnects to VPN server, depends on current connection state.
   * Is connection state is unknown - does nothing
   * @returns {Promise<void>}
   */
  async connectDisconnect () {
    if (!this.isReady()) {
      return
    }

    NativeModules.TestModule.startService("foo", (error, events) => {
      if (error) {
        console.error(error)
      } else {
        console.log(`startService returned ${events[0]}`)
      }
    })

    if (this.isConnected()) {
      await this.disconnect()
    } else {
      await this.connect()
    }
  }

  /**
   * Callback called when VPN server is selected
   * @param value - proposal/VPN providerId
   * @param index - index of proposal in dropdown list
   */
  onProposalSelected (value, index) {
    console.log('selected', value, index)
    this.setState({ selectedProviderId: value })
  }

  render () {
    const s = this.state
    const isReady = this.isReady()
    const isConnected = this.isConnected()
    const connectText = isReady
      ? (isConnected ? 'disconnect' : 'connect')
      : CONFIG.TEXTS.UNKNOWN_STATUS
    const testModule = NativeModules.TestModule

    return (
      <View style={styles.container} transform={[{ scaleX: 2 }, { scaleY: 2 }]}>
        <Text>{ `foo = ${testModule.foo}` }</Text>
        { s.refreshing ? <Text>...</Text> : <Text> </Text> }
        <Text>{s.connection ? s.connection.status : CONFIG.TEXTS.UNKNOWN}</Text>
        <Text>IP: {s.ip}</Text>
        <Proposals proposals={s.proposals} selectedProviderId={s.selectedProviderId} onProposalSelected={this.onProposalSelected}/>
        <Button title={connectText} onPress={this.connectDisconnect} disabled={!isReady}/>
        { s.stats ? <Stats {...s.stats} /> : null }
      </View>
    )
  }
}
