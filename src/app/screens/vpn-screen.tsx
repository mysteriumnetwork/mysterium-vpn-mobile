/*
 * Copyright (C) 2018 The 'MysteriumNetwork/mysterium-vpn-mobile' Authors.
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
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

import { observer } from 'mobx-react'
import React from 'react'
import { Text, View } from 'react-native'
import { CONFIG } from '../../config'
import TequilApiDriver from '../../libraries/tequil-api/tequil-api-driver'
import styles from '../app-styles'
import ButtonConnect from '../components/button-connect'
import ConnectionStatus from '../components/connection-status'
import FeedbackButton from '../components/feedback-button'
import LogoBackground from '../components/logo-background'
import { IProposal } from '../components/proposal-picker/proposal'
import ProposalPicker from '../components/proposal-picker/proposal-picker'
import Stats from '../components/stats'
import IMessageDisplay from '../messages/message-display'
import messages from '../messages/messages'
import Favorites from '../proposals/favorites'
import ProposalList from '../proposals/proposal-list'
import ConnectionStore from '../stores/connection-store'
import ScreenStore from '../stores/screen-store'
import translations from '../translations'
import VpnAppState from '../vpn-app-state'

type HomeProps = {
  tequilAPIDriver: TequilApiDriver,
  connectionStore: ConnectionStore,
  vpnAppState: VpnAppState,
  screenStore: ScreenStore,
  proposalList: ProposalList,
  favorites: Favorites,
  messageDisplay: IMessageDisplay
}

@observer
class VpnScreen extends React.Component<HomeProps> {
  private readonly tequilAPIDriver: TequilApiDriver
  private readonly connectionStore: ConnectionStore
  private readonly vpnAppState: VpnAppState
  private readonly screenStore: ScreenStore
  private readonly proposalList: ProposalList
  private readonly favorites: Favorites
  private readonly messageDisplay: IMessageDisplay

  constructor (props: HomeProps) {
    super(props)
    this.tequilAPIDriver = props.tequilAPIDriver
    this.connectionStore = props.connectionStore
    this.vpnAppState = props.vpnAppState
    this.screenStore = props.screenStore
    this.proposalList = props.proposalList
    this.favorites = props.favorites
    this.messageDisplay = props.messageDisplay
  }

  public render () {
    const connectionData = this.connectionStore.data

    return (
      <View style={styles.screen}>
        <LogoBackground/>

        <View style={styles.feedback}>
          <FeedbackButton onClick={() => this.screenStore.navigateToFeedbackScreen()}/>
        </View>

        <ConnectionStatus status={connectionData.status}/>

        <Text style={styles.textIp}>IP: {connectionData.IP || CONFIG.TEXTS.IP_UPDATING}</Text>

        <View style={styles.controls}>
          <View style={styles.proposalPicker}>
            <ProposalPicker
              placeholder={translations.COUNTRY_PICKER_LABEL}
              proposals={this.proposalList.proposals}
              onSelect={(proposal: IProposal) => this.vpnAppState.selectedProviderId = proposal.providerID}
              onFavoriteToggle={() => this.favorites.toggle(this.vpnAppState.selectedProviderId)}
              isFavoriteSelected={this.favorites.isFavored(this.vpnAppState.selectedProviderId)}
            />
          </View>

          <ButtonConnect
            connectionStatus={connectionData.status}
            connect={() => this.connect()}
            disconnect={this.tequilAPIDriver.disconnect.bind(this.tequilAPIDriver)}
          />
        </View>

        <View style={styles.footer}>
          <Stats
            duration={connectionData.connectionStatistics.duration}
            bytesReceived={connectionData.connectionStatistics.bytesReceived}
            bytesSent={connectionData.connectionStatistics.bytesSent}
          />
        </View>
      </View>
    )
  }

  private async connect () {
    const providerId = this.vpnAppState.selectedProviderId
    if (providerId === null) {
      this.messageDisplay.showInfo(messages.COUNTRY_NOT_SELECTED)
      return
    }
    await this.tequilAPIDriver.connect(providerId)
  }
}

export default VpnScreen
