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
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import { observer } from 'mobx-react/native'
import { Container, Content, Grid, Row, Toast } from 'native-base'
import React, { ReactNode } from 'react'
import VpnAppState from '../../../app/vpn-app-state'
import { FavoritesStorage } from '../../../libraries/favorites-storage'
import { mysteriumClient } from '../../../libraries/mysterium-client'
import TequilApiDriver from '../../../libraries/tequil-api/tequil-api-driver'
import TequilApiState from '../../../libraries/tequil-api/tequil-api-state'
import BackgroundImage from '../../components/background-image'
import ConnectButton from '../../components/connect-button'
import ConnectionStatus from '../../components/connection-status'
import { Country, proposalsToCountries } from '../../components/country-picker/country'
import CountryPicker from '../../components/country-picker/country-picker'
import ErrorDropdown from '../../components/error-dropdown'
import IPAddress from '../../components/ip-address'
import Stats from '../../components/stats'
import ErrorDisplayDelegate from '../../errors/error-display-delegate'
import translations from './../../translations'
import styles from './styles'

type AppProps = {
  tequilAPIDriver: TequilApiDriver,
  tequilApiState: TequilApiState,
  vpnAppState: VpnAppState,
  errorDisplayDelegate: ErrorDisplayDelegate,
  favoritesStore: FavoritesStorage
}

@observer
export default class HomeScreen extends React.Component<AppProps> {
  private readonly tequilAPIDriver: TequilApiDriver
  private readonly tequilApiState: TequilApiState
  private readonly errorDisplayDelegate: ErrorDisplayDelegate
  private readonly vpnAppState: VpnAppState

  constructor (props: AppProps) {
    super(props)
    this.tequilAPIDriver = props.tequilAPIDriver
    this.tequilApiState = props.tequilApiState
    this.errorDisplayDelegate = props.errorDisplayDelegate
    this.vpnAppState = props.vpnAppState
  }

  /***
   * Refreshes connection state, ip and unlocks identity.
   * Starts periodic state refreshing
   * Called once after first rendering.
   */
  public async componentDidMount () {
    await this.tequilAPIDriver.unlock()

    // TODO: remove it later, serviceStatus is used only for native call test
    const serviceStatus = await mysteriumClient.startService(4050)
    console.log('serviceStatus', serviceStatus)
  }

  public render (): ReactNode {
    return (
      <Container>
        <Content>
          <BackgroundImage>
            <Grid>
              <Row>
                <ErrorDropdown ref={(ref: ErrorDropdown) => this.errorDisplayDelegate.errorDisplay = ref}/>
              </Row>

              <Row style={styles.textCentered}>
                <ConnectionStatus status={this.connectionStatusText}/>
              </Row>

              <Row style={styles.textCentered}>
                <IPAddress ipAddress={this.ipAddress}/>
              </Row>

              <Row style={styles.connectionContainer}>
                <Grid>
                  <Row style={styles.countryPicker}>
                    <CountryPicker
                      placeholder={translations.COUNTRY_PICKER_LABEL}
                      items={this.countries}
                      onSelect={(country: Country) => this.onCountrySelect(country)}
                      onFavoriteSelect={() => this.onFavoriteSelect()}
                      isFavoriteSelected={this.selectedProviderIsFavored}
                    />
                  </Row>

                  <Row style={[styles.textCentered, styles.connectButton]}>
                    <ConnectButton
                      onClick={() => this.onConnectButtonClick()}
                      loading={!this.buttonIsReady}
                      active={this.buttonIsActive}
                    />
                  </Row>

                  <Row style={styles.statsContainer}>
                    <Stats
                      duration={this.sessionDuration}
                      bytesReceived={this.sessionBytesReceived}
                      bytesSent={this.sessionBytesSent}
                    />
                  </Row>
                </Grid>
              </Row>
            </Grid>
          </BackgroundImage>
        </Content>
      </Container>
    )
  }

  private get ipAddress (): string {
    return this.tequilApiState.IP || translations.IP_UPDATING
  }

  private get sessionDuration (): number {
    if (!this.tequilApiState.connectionStatistics) {
      return 0
    }

    return this.tequilApiState.connectionStatistics.duration
  }

  private get sessionBytesSent (): number {
    if (!this.tequilApiState.connectionStatistics) {
      return 0
    }

    return this.tequilApiState.connectionStatistics.bytesSent
  }

  private get sessionBytesReceived (): number {
    if (!this.tequilApiState.connectionStatistics) {
      return 0
    }

    return this.tequilApiState.connectionStatistics.bytesReceived
  }

  private get countries (): Country[] {
    return proposalsToCountries(this.tequilApiState.proposals)
  }

  private get buttonIsReady (): boolean {
    return this.tequilApiState.isReady
  }

  private get buttonIsActive (): boolean {
    return this.tequilApiState.isConnected
  }

  private get connectionStatusText (): string {
    return this.tequilApiState.connectionStatus.status
  }

  private get selectedProviderIsFavored (): boolean {
    if (!this.vpnAppState.selectedProviderId) {
      return false
    }

    return this.props.favoritesStore.has(this.vpnAppState.selectedProviderId)
  }

  private onCountrySelect (country: Country) {
    this.vpnAppState.selectedProviderId = country.id
  }

  private async onConnectButtonClick (): Promise<void> {
    if (!this.tequilApiState.isConnected && !this.vpnAppState.selectedProviderId) {
      this.showMissingCountryError()
      return
    }

    await this.connectOrDisconnect()
  }

  private async connectOrDisconnect (): Promise<void> {
    if (!this.tequilApiState.isReady) {
      return
    }

    if (this.tequilApiState.isConnected) {
      await this.tequilAPIDriver.disconnect()
    } else {
      await this.tequilAPIDriver.connect(this.vpnAppState.selectedProviderId)
    }
  }

  private async onFavoriteSelect (): Promise<void> {
    const store = this.props.favoritesStore
    const selectedProviderId = this.vpnAppState.selectedProviderId

    if (!selectedProviderId) {
      this.showMissingCountryError()
      return
    }

    if (!store.has(selectedProviderId)) {
      await store.add(selectedProviderId)
    } else {
      await store.remove(selectedProviderId)
    }
  }

  private showMissingCountryError () {
    Toast.show({
      text: translations.UNSELECTED_COUNTRY,
      textStyle: {
        textAlign: 'center'
      }
    })
  }
}
