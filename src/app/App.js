import React from 'react';
import { StyleSheet, Text, View, Button, Picker } from 'react-native';
import HttpTequilapiClient from '../libraries/mysterium-tequilapi/client'
import Http from '../libraries/http'
import Countries from '../libraries/countries'
import ConnectionRequestDTO from '../libraries/mysterium-tequilapi/dto/connection-request'
import ConnectionStatusEnum from '../libraries/mysterium-tequilapi/dto/connection-status-enum'

const IP_UPDATING = 'updating...'
const passphrase = ''
const http = new Http("http://localhost:4050/")
const api = new HttpTequilapiClient(http)

export default class App extends React.Component {
  constructor(props) {
    super(props)
    this.state = {
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
    setInterval(this.refresh.bind(this), 1000)
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
    const identities = await api.identitiesList()
    let identityId: string = null
    if (identities.length) {
      identityId = identities[0].id
    } else {
      const newIdentity = api.identityCreate(passphrase)
      identityId = newIdentity.id
    }
    await api.identityUnlock(identityId, passphrase)
    this.setState({ identityId })
  }

  async refreshConnection() {
    const connection = await api.connectionStatus()
    console.log("connection", connection)
    this.setState({ connection })
  }

  async refreshIP() {
    const ipDto = await api.connectionIP()
    console.log("ip", ipDto)
    if (this.isReady()) {
      this.setState({ip: ipDto.ip})
    }
  }

  async refreshProposals() {
    const proposals = await api.findProposals()
    console.log("proposals", proposals)
    if (proposals.length) {
      this.setState({ proposals, selectedProviderId: proposals[0].providerId })
    } else {
      this.setState({ proposals })
    }
  }

  async refreshStatistics() {
    const stats = await api.connectionStatistics()
    console.log("stats", stats)
    this.setState({ stats })
  }

  refresh() {
    this.refreshConnection()
    this.refreshProposals()
    if (this.isConnected()) {
      this.refreshStatistics()
    } else {
      this.refreshIP()
    }
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

    const formatTime = sec => {
      if (sec < 60) {
        return sec + ' seconds'
      } else if (sec < 60*60) {
        return Math.round(sec / 60) + ' minutes'
      } else {
        return Math.round(sec / 3600) + ' hours'
      }
    }

    const formatBytes = bytes => {
      if (bytes < 1024) {
        return bytes + ' B'
      } else if (bytes < 1024*1024) {
        return Math.round(bytes / 1024) + ' kB'
      } else {
        return Math.round(bytes / (1024*1024)) + ' mB'
      }
    }

    return (
      <View>
        <Text>Duration: {formatTime(stats.duration)}</Text>
        <Text>Received: {formatBytes(stats.bytesReceived)}</Text>
        <Text>Sent: {formatBytes(stats.bytesSent)}</Text>
      </View>
    )
  }

  render() {
    const s = this.state
    const isReady = this.isReady()
    const isConnected = this.isConnected()
    const connectText = isReady
      ? (isConnected ? 'disconnect' : 'connect')
      : 'Loading...'
    return (
      <View style={styles.container}>
        {s.connection ? <Text>{s.connection.status}</Text> : null }
        <Text>IP: {s.ip}</Text>
        <Picker style={styles.picker} selectedValue={s.selectedProviderId} onValueChange={this.onProposalSelected}>
          {s.proposals.map(p => App.renderProposal(p))}
        </Picker>
        <Button title={connectText} onPress={this.connectDisconnect} disabled={!isReady}/>
        {s.stats && isConnected ? App.renderStats(s.stats) : null}
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
  picker: {
    width: 150,
    height: 20,
    margin: 20,
  }
});
