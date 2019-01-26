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
import { StyleSheet, Text, View } from 'react-native'
import { CONFIG } from '../../config'
import TequilApiDriver from '../../libraries/tequil-api/tequil-api-driver'
import { STYLES } from '../../styles'
import appStyles from '../app-styles'
import ButtonConnect from '../components/button-connect'
import ConnectionStatus from '../components/connection-status'
import IconButton from '../components/icon-button'
import LogoBackground from '../components/logo-background'
import { ProposalListItem } from '../components/proposal-picker/proposal-list-item'
import ProposalPicker from '../components/proposal-picker/proposal-picker'
import Stats from '../components/stats'
import MessageDisplay from '../messages/message-display'
import Favorites from '../proposals/favorites'
import ConnectionStore from '../stores/connection-store'
import ScreenStore from '../stores/screen-store'
import VpnScreenStore from '../stores/vpn-screen-store'
import translations from '../translations'

type HomeProps = {
  tequilAPIDriver: TequilApiDriver,
  connectionStore: ConnectionStore,
  vpnScreenStore: VpnScreenStore,
  screenStore: ScreenStore,
  favorites: Favorites,
  messageDisplay: MessageDisplay
}

@observer
class VpnScreen extends React.Component<HomeProps> {
  private readonly tequilAPIDriver: TequilApiDriver
  private readonly connectionStore: ConnectionStore
  private readonly vpnScreenStore: VpnScreenStore
  private readonly screenStore: ScreenStore
  private readonly favorites: Favorites
  private readonly messageDisplay: MessageDisplay

  constructor (props: HomeProps) {
    super(props)
    this.tequilAPIDriver = props.tequilAPIDriver
    this.connectionStore = props.connectionStore
    this.vpnScreenStore = props.vpnScreenStore
    this.screenStore = props.screenStore
    this.favorites = props.favorites
    this.messageDisplay = props.messageDisplay
  }

  public render () {
    const connectionData = this.connectionStore.data

    return (
      <View style={appStyles.screen}>
        <View style={styles.feedback}>
          <IconButton
            icon="ios-help-circle-outline"
            onClick={() => this.screenStore.navigateToFeedbackScreen()}
          />
        </View>

        <ConnectionStatus status={connectionData.status}/>

        <Text style={styles.textIp}>IP: {connectionData.location.ip || CONFIG.TEXTS.IP_UPDATING}</Text>

        <View style={styles.controlsWithLogoContainer}>
          <LogoBackground/>

          <View style={styles.controls}>
            <View style={appStyles.proposalPicker}>
              <ProposalPicker
                placeholder={translations.PROPOSAL_PICKER_LABEL}
                proposals={this.vpnScreenStore.proposalListItems}
                selectedProposal={this.vpnScreenStore.selectedProposal}
                onSelect={(proposal: ProposalListItem) => this.vpnScreenStore.selectedProposal = proposal}
                onFavoriteToggle={() => this.toggleSelectedProposalFavorite()}
                isFavoriteSelected={this.vpnScreenStore.isFavoriteSelected}
                disabled={this.vpnScreenStore.proposalPickerDisabled}
              />
            </View>

            <ButtonConnect
              connectionStatus={connectionData.status}
              connect={() => this.connect()}
              disconnect={this.tequilAPIDriver.disconnect.bind(this.tequilAPIDriver)}
            />
          </View>
        </View>

        <Stats
          duration={connectionData.connectionStatistics.duration}
          bytesReceived={connectionData.connectionStatistics.bytesReceived}
          bytesSent={connectionData.connectionStatistics.bytesSent}
        />
      </View>
    )
  }

  private toggleSelectedProposalFavorite () {
    const proposal = this.vpnScreenStore.selectedProposal
    if (!proposal) {
      return
    }

    return this.favorites.toggle(proposal.id)
  }

  private async connect () {
    const proposal = this.vpnScreenStore.selectedProposal

    if (!proposal) {
      this.messageDisplay.showInfo(translations.UNSELECTED_PROPOSAL)
      return
    }

    await this.tequilAPIDriver.connect(
      proposal.providerID,
      proposal.serviceType,
      proposal.countryCode ? proposal.countryCode : ''
    )
  }
}

const styles = StyleSheet.create({
  feedback: {
    position: 'absolute',
    top: 10,
    left: 10
  },
  controls: {
    width: '100%',
    alignItems: 'center',
    position: 'absolute',
    bottom: 30
  },
  textIp: {
    marginTop: STYLES.MARGIN,
    fontSize: STYLES.FONT_NORMAL,
    color: STYLES.COLOR_SECONDARY
  },
  controlsWithLogoContainer: {
    flex: 1,
    width: '100%'
  }
})

export default VpnScreen
