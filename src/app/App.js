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
import Countries from '../libraries/countries'
import ConnectionRequestDTO from '../libraries/mysterium-tequilapi/dto/connection-request'
import ConnectionStatusEnum from '../libraries/mysterium-tequilapi/dto/connection-status-enum'
import IdentityDTO from '../libraries/mysterium-tequilapi/dto/identity'
import ConnectionStatusDTO from '../libraries/mysterium-tequilapi/dto/connection-status'
import ConnectionIPDTO from '../libraries/mysterium-tequilapi/dto/connection-ip'
import ProposalDTO from '../libraries/mysterium-tequilapi/dto/proposal'
import ConnectionStatisticsDTO from '../libraries/mysterium-tequilapi/dto/connection-statistics'
import tequilapiClientFactory from '../libraries/mysterium-tequilapi/client-factory'
import type {TequilapiClient} from '../libraries/mysterium-tequilapi/client'
import styles from './App-styles'
import CONFIG from '../config'
import Stats from './Stats'

const IP_UPDATING = CONFIG.TEXTS.IP_UPDATING
const api: TequilapiClient = tequilapiClientFactory()

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
    let identities: Array<IdentityDTO>
    try {
      identities = await api.identitiesList()
    } catch (e) {
      console.warn('api.identitiesList failed', e)
      return
    }

    let identityId: ?string = null
    try {
      if (identities.length) {
        identityId = identities[0].id
      } else {
        const newIdentity: IdentityDTO = api.identityCreate(CONFIG.PASSPHRASE)
        identityId = newIdentity.id
      }
    } catch (e) {
      console.warn('api.identityCreate failed', e)
      return
    }

    try {
      await api.identityUnlock(identityId, CONFIG.PASSPHRASE)
      this.setState({identityId})
    } catch (e) {
      console.warn('api.identityUnlock failed', e)
    }
  }

  async refreshConnection () {
    try {
      const connection: ConnectionStatusDTO = await api.connectionStatus()
      console.log('connection', connection)
      this.setState({connection})
    } catch (e) {
      console.warn('api.connectionStatus failed', e)
      this.setState({connection: null})
    }
  }

  async refreshIP () {
    try {
      const ipDto: ConnectionIPDTO = await api.connectionIP()
      console.log('ip', ipDto)
      if (this.isReady()) {
        this.setState({ip: ipDto.ip})
      }
    } catch (e) {
      console.warn('api.connectionIP failed', e)
      this.setState({ip: CONFIG.TEXTS.UNKNOWN})
    }
  }

  async refreshProposals () {
    try {
      const proposals: Array<ProposalDTO> = await api.findProposals()
      console.log('proposals', proposals)
      if (proposals.length) {
        this.setState({proposals, selectedProviderId: proposals[0].providerId})
      } else {
        this.setState({proposals})
      }
    } catch (e) {
      console.warn('api.findProposals failed', e)
      this.setState({proposals: [], selectedProviderId: null})
    }
  }

  async refreshStatistics () {
    try {
      const stats: ConnectionStatisticsDTO = await api.connectionStatistics()
      console.log('stats', stats)
      this.setState({ stats })
    } catch (e) {
      console.warn('api.connectionStatistics failed', e)
    }
  }

  refresh (force: boolean = false) {
    if (this.state.refreshing) {
      return
    }

    this.interval++
    this.setState({ refreshing: true })
    const promises = []

    if (!this.state.identityId) {
      promises.push(this.unlock())
    }

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
      if (force || this.interval % CONFIG.REFRESH_INTERVALS.IP === 0 || this.state.ip === IP_UPDATING) {
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
    try {
      const request = new ConnectionRequestDTO(s.identityId, s.selectedProviderId)
      const connection = await api.connectionCreate(request)
      console.log('connect', connection)
    } catch (e) {
      console.warn('api.connectionCreate failed', e)
    }
    this.refresh(true)
  }

  async disconnect () {
    this.setState({
      ip: IP_UPDATING,
      connection: { status: ConnectionStatusEnum.DISCONNECTING
      }
    })
    try {
      await api.connectionCancel()
      console.log('disconnect')
    } catch (e) {
      console.warn('api.connectionCancel failed', e)
    }
    this.refresh(true)
  }

  onProposalSelected (value, index) {
    console.log('selected', value, index)
    this.setState({ selectedProviderId: value })
  }

  static renderProposal (p: ProposalDTO) {
    if (!p.serviceDefinition) {
      return null
    }
    const countryCode = p.serviceDefinition.locationOriginate.country.toLocaleLowerCase()
    const countryName = Countries[countryCode] || CONFIG.TEXTS.UNKNOWN
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
        <Text>Status: {s.connection ? s.connection.status : CONFIG.TEXTS.UNKNOWN}</Text>
        <Text>IP: {s.ip}</Text>
        <Picker style={styles.picker} selectedValue={s.selectedProviderId} onValueChange={this.onProposalSelected}>
          {s.proposals.map(p => App.renderProposal(p))}
        </Picker>
        <Button title={connectText} onPress={this.connectDisconnect} disabled={!isReady}/>
        { s.stats ? <Stats {...s.stats} />: null }
      </View>
    )
  }
}
