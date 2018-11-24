import { reaction } from 'mobx'
import TequilApiState from '../libraries/tequil-api/tequil-api-state'
import Connection from './core/connection'
import VpnAppState from './vpn-app-state'

export default class Logger {
  private loggingStarted: boolean = false

  constructor (
    private readonly tequilApiState: TequilApiState,
    private readonly vpnAppState: VpnAppState,
    private readonly connection: Connection) {
  }

  public logObservableChanges (): void {
    if (this.loggingStarted) {
      return
    }
    this.loggingStarted = true

    reaction(() => this.tequilApiState.identityId, () => {
      this.info('Identity unlocked', this.tequilApiState.identityId)
    })

    reaction(() => this.connection.connectionState.connectionStatus, () => {
      this.info('Connection status changed', this.connection.connectionState.connectionStatus)
    })

    reaction(() => this.connection.connectionState.IP, () => {
      this.info('IP changed', this.connection.connectionState.IP)
    })

    reaction(() => this.tequilApiState.proposals, () => {
      this.info('Proposals updated', this.tequilApiState.proposals)
    })

    reaction(() => this.vpnAppState.selectedProviderId, () => {
      this.info('Selected provider ID selected', this.vpnAppState.selectedProviderId)
    })
  }

  private info (...args: any[]) {
    console.info('[LOG]', ...args)
  }
}
