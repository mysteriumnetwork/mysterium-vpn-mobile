import React from 'react';
import { Text, View, Button, Picker } from 'react-native';
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
import {bytesDisplay, timeDisplay} from '../libraries/unitConverter'
import CONFIG from '../config'

const IP_UPDATING = CONFIG.TEXTS.IP_UPDATING
const http = new Http(CONFIG.TEQUILAPI_ADDRESS)
const api = new HttpTequilapiClient(http)

export default class App extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
      refreshing: false,
      identityId: null,
      ip: IP_UPDATING,
      proposals: [],
      connection: null,
      selectedProviderId: null,
      stats: null,
    }

    // Bind local functions
    this.connectDisconnect = this.connectDisconnect.bind(this)
    this.isReady = this.isReady.bind(this)
    this.onProposalSelected = this.onProposalSelected.bind(this)
  }

  componentDidMount() {
    this.unlock()
    this.refresh()
    setInterval(this.refresh.bind(this), 5000)
  }

  isReady() {
    const s = this.state
    return s.identityId && s.connection
      && ((s.connection.status == ConnectionStatusEnum.NOT_CONNECTED && s.selectedProviderId)
        || s.connection.status == ConnectionStatusEnum.CONNECTED)
  }

  isConnected() {
    const c = this.state.connection
    return c && c.status == ConnectionStatusEnum.CONNECTED
  }

  async unlock() {
    const identities: Array<IdentityDTO> = await api.identitiesList()
    let identityId: string = null
    if (identities.length) {
      identityId = identities[0].id
    } else {
      const newIdentity = api.identityCreate(CONFIG.PASSPHRASE)
      identityId = newIdentity.id
    }
    await api.identityUnlock(identityId, CONFIG.PASSPHRASE)
    this.setState({ identityId })
  }

  async refreshConnection() {
    const connection: ConnectionStatusDTO = await api.connectionStatus()
    console.log("connection", connection)
    this.setState({ connection })
  }

  async refreshIP() {
    const ipDto: ConnectionIPDTO = await api.connectionIP()
    console.log("ip", ipDto)
    if (this.isReady()) {
      this.setState({ip: ipDto.ip})
    }
  }

  async refreshProposals() {
    const proposals: Array<ProposalDTO> = await api.findProposals()
    console.log("proposals", proposals)
    if (proposals.length) {
      this.setState({ proposals, selectedProviderId: proposals[0].providerId })
    } else {
      this.setState({ proposals })
    }
  }

  async refreshStatistics() {
    const stats: ConnectionStatisticsDTO = await api.connectionStatistics()
    console.log("stats", stats)
    this.setState({ stats })
  }

  refresh() {
    this.setState({ refreshing: true })
    const promises = []
    promises.push(this.refreshConnection())
    promises.push(this.refreshProposals())
    if (this.isConnected()) {
      promises.push(this.refreshStatistics())
    } else {
      promises.push(this.refreshIP())
    }
    Promise.all(promises).then(() => this.setState({ refreshing: false }))
  }

  async connectDisconnect() {
    if (!this.isReady()) {
      return
    }

    if (this.isConnected()) {
      await this.disconnect()
    } else {
      await this.connect()
    }
  }

  async connect() {
    const s = this.state
    this.setState({ ip: IP_UPDATING, connection: { status: ConnectionStatusEnum.CONNECTING }})
    const request = new ConnectionRequestDTO(s.identityId, s.selectedProviderId)
    const connection = await api.connectionCreate(request)
    console.log("connect", connection)
    this.refresh()
  }

  async disconnect() {
    const s = this.state
    this.setState({ ip: IP_UPDATING, connection: { status: ConnectionStatusEnum.DISCONNECTING }})
    await api.connectionCancel()
    console.log("disconnect")
    this.refresh()
  }

  onProposalSelected(value, index) {
    console.log("selected", value, index)
    this.setState({ selectedProviderId: value })
  }

  static renderProposal(p) {
    const countryCode = p.serviceDefinition.locationOriginate.country.toLocaleLowerCase()
    const countryName = Countries[countryCode] || 'unknown'
    const providerId = p.providerId
    return (
      <Picker.Item key={p.id} label={countryName} value={providerId} />
    )
  }

  static renderStats(stats) {
    if (!stats) {
      return null
    }
    return (
      <View>
        <Text>Duration: {timeDisplay(stats.duration)}</Text>
        <Text>Received: {bytesDisplay(stats.bytesReceived)}</Text>
        <Text>Sent: {bytesDisplay(stats.bytesSent)}</Text>
      </View>
    )
  }

  render() {
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
        { s.stats && isConnected ? App.renderStats(s.stats) : null }
      </View>
    );
  }
}
