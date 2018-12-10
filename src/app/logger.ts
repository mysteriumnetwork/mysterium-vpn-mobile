import { reaction } from 'mobx'
import TequilApiState from '../libraries/tequil-api/tequil-api-state'
import Connection from './domain/connection'
import ProposalsStore from './stores/proposals-store'
import VpnAppState from './vpn-app-state'

export default class Logger {
  private loggingStarted: boolean = false

  constructor (
    private readonly tequilApiState: TequilApiState,
    private readonly vpnAppState: VpnAppState,
    private readonly connection: Connection,
    private readonly proposalsStore: ProposalsStore) {
  }

  public logObservableChanges (): void {
    if (this.loggingStarted) {
      return
    }
    this.loggingStarted = true

    reaction(() => this.tequilApiState.identityId, () => {
      this.info('Identity unlocked', this.tequilApiState.identityId)
    })

    this.connection.onStatusChange(status => {
      this.info('Connection status changed', status)
    })

    this.connection.onIpChange(ip => {
      this.info('IP changed', ip)
    })

    reaction(() => this.proposalsStore.proposals, () => {
      this.info('Proposals updated', this.proposalsStore.proposals)
    })

    reaction(() => this.vpnAppState.selectedProviderId, () => {
      this.info('Selected provider ID selected', this.vpnAppState.selectedProviderId)
    })
  }

  private info (...args: any[]) {
    console.info('[LOG]', ...args)
  }
}
