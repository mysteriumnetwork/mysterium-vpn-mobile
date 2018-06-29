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
import { Text, View, Button, Picker } from 'react-native'
import HttpTequilapiClient from '../libraries/mysterium-tequilapi/client'
import Http from '../libraries/mysterium-tequilapi/adapters/http'
import Countries from '../libraries/countries'
import ConnectionRequestDTO from '../libraries/mysterium-tequilapi/dto/connection-request'
import ConnectionStatusEnum from '../libraries/mysterium-tequilapi/dto/connection-status-enum'
import IdentityDTO from '../libraries/mysterium-tequilapi/dto/identity'
import ConnectionStatusDTO from '../libraries/mysterium-tequilapi/dto/connection-status'
import ConnectionIPDTO from '../libraries/mysterium-tequilapi/dto/connection-ip'
import ProposalDTO from '../libraries/mysterium-tequilapi/dto/proposal'
import ConnectionStatisticsDTO from '../libraries/mysterium-tequilapi/dto/connection-statistics'
import styles from './App-styles'
import CONFIG from '../config'
import Stats from './Stats'

const IP_UPDATING = CONFIG.TEXTS.IP_UPDATING
const http = new Http(CONFIG.TEQUILAPI_ADDRESS)
const api = new HttpTequilapiClient(http)

export default class App extends React.Component {
  interval: number = 0

  constructor (props) {
    super(props)
    this.state = {
      refreshing: false,
      identityId: null,
      ip: IP_UPDATING,
      proposals: [],
      connection: null,
      selectedProviderId: null,
      stats: null
    }

    // Bind local functions
    this.connectDisconnect = this.connectDisconnect.bind(this)
    this.isReady = this.isReady.bind(this)
    this.onProposalSelected = this.onProposalSelected.bind(this)
  }

  componentDidMount () {
    this.unlock()
    this.refresh(true)
    setInterval(this.refresh.bind(this), CONFIG.REFRESH_INTERVALS.INTERVAL_MS)
  }

  isReady () {
    const s = this.state
    return s.identityId && s.connection &&
      ((s.connection.status === ConnectionStatusEnum.NOT_CONNECTED && s.selectedProviderId) ||
        s.connection.status === ConnectionStatusEnum.CONNECTED)
  }

  isConnected () {
    const c = this.state.connection
    return c && c.status === ConnectionStatusEnum.CONNECTED
  }

  async unlock () {
    const identities: Array<IdentityDTO> = await api.identitiesList()
    let identityId: ?string = null
    if (identities.length) {
      identityId = identities[0].id
    } else {
      const newIdentity = api.identityCreate(CONFIG.PASSPHRASE)
      identityId = newIdentity.id
    }
    await api.identityUnlock(identityId, CONFIG.PASSPHRASE)
    this.setState({ identityId })
  }

  async refreshConnection () {
    const connection: ConnectionStatusDTO = await api.connectionStatus()
    console.log('connection', connection)
    this.setState({ connection })
  }

  async refreshIP () {
    const ipDto: ConnectionIPDTO = await api.connectionIP()
    console.log('ip', ipDto)
    if (this.isReady()) {
      this.setState({ip: ipDto.ip})
    }
  }

  async refreshProposals () {
    const proposals: Array<ProposalDTO> = await api.findProposals()
    console.log('proposals', proposals)
    if (proposals.length) {
      this.setState({ proposals, selectedProviderId: proposals[0].providerId })
    } else {
      this.setState({ proposals })
    }
  }

  async refreshStatistics () {
    const stats: ConnectionStatisticsDTO = await api.connectionStatistics()
    console.log('stats', stats)
    this.setState({ stats })
  }

  refresh (force: boolean = false) {
    if (this.state.refreshing) {
      return
    }

    this.interval++
    this.setState({ refreshing: true })
    const promises = []
    if (force || this.interval % CONFIG.REFRESH_INTERVALS.CONNECTION === 0) {
      promises.push(this.refreshConnection())
    }
    if (force || this.interval % CONFIG.REFRESH_INTERVALS.PROPOSALS === 0) {
      promises.push(this.refreshProposals())
    }
    if (this.isConnected()) {
      if (force || this.interval % CONFIG.REFRESH_INTERVALS.STATS === 0) {
        promises.push(this.refreshStatistics())
      }
    } else {
      if (force || this.interval % CONFIG.REFRESH_INTERVALS.IP === 0) {
        promises.push(this.refreshIP())
      }
    }
    return Promise.all(promises)
      .then(() => this.setState({ refreshing: false }))
  }

  async connectDisconnect () {
    if (!this.isReady()) {
      return
    }

    if (this.isConnected()) {
      await this.disconnect()
    } else {
      await this.connect()
    }
  }

  async connect () {
    const s = this.state
    this.setState({
      ip: IP_UPDATING,
      connection: { status: ConnectionStatusEnum.CONNECTING
      }
    })
    const request = new ConnectionRequestDTO(s.identityId, s.selectedProviderId)
    const connection = await api.connectionCreate(request)
    console.log('connect', connection)
    this.refresh(true)
  }

  async disconnect () {
    this.setState({
      ip: IP_UPDATING,
      connection: { status: ConnectionStatusEnum.DISCONNECTING
      }
    })
    await api.connectionCancel()
    console.log('disconnect')
    this.refresh(true)
  }

  onProposalSelected (value, index) {
    console.log('selected', value, index)
    this.setState({ selectedProviderId: value })
  }

  static renderProposal (p) {
    const countryCode = p.serviceDefinition.locationOriginate.country.toLocaleLowerCase()
    const countryName = Countries[countryCode] || 'unknown'
    const providerId = p.providerId
    return (
      <Picker.Item key={p.id} label={countryName} value={providerId} />
    )
  }

  render () {
    const s = this.state
    const isReady = this.isReady()
    const isConnected = this.isConnected()
    const connectText = isReady
      ? (isConnected ? 'disconnect' : 'connect')
      : CONFIG.TEXTS.UNKNOWN_STATUS
    return (
      <View style={styles.container}>
        { s.refreshing ? <Text>Refreshing...</Text> : <Text> </Text> }
        { s.connection ? <Text>Status: {s.connection.status}</Text> : <Text> </Text> }
        <Text>IP: {s.ip}</Text>
        <Picker style={styles.picker} selectedValue={s.selectedProviderId} onValueChange={this.onProposalSelected}>
          {s.proposals.map(p => App.renderProposal(p))}
        </Picker>
        <Button title={connectText} onPress={this.connectDisconnect} disabled={!isReady}/>
        { s.stats && isConnected ? <Stats {...s.stats} />: null }
      </View>
    )
  }
}
