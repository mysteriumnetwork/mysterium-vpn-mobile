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
import { FavoritesStorage } from '../../../libraries/favorites-storage'
import { mysteriumClient } from '../../../libraries/mysterium-client'
import TequilApiDriver from '../../../libraries/tequil-api/tequil-api-driver'
import AppState from '../../app-state'
import ConnectButton from '../../components/connect-button'
import ConnectionStatus from '../../components/connection-status'
import CountryPicker  from '../../components/country-picker/country-picker'
import { proposalsToCountries } from '../../components/country-picker/country'
import ErrorDropdown from '../../components/error-dropdown'
import Stats from '../../components/stats'
import ErrorDisplayDelegate from '../../errors/error-display-delegate'
import translations from './../../translations'
import styles from './styles'
import IPAddress from '../../components/ip-address'
import BackgroundImage from '../../components/background-image'
import { Country } from '../../components/country-picker/country'

type AppProps = {
  tequilAPIDriver: TequilApiDriver,
  appState: AppState,
  errorDisplayDelegate: ErrorDisplayDelegate,
  favoritesStore: FavoritesStorage
}

@observer
export default class HomeScreen extends React.Component<AppProps> {
  private readonly tequilAPIDriver: TequilApiDriver
  private readonly appState: AppState
  private readonly errorDisplayDelegate: ErrorDisplayDelegate

  constructor (props: AppProps) {
    super(props)

    this.tequilAPIDriver = props.tequilAPIDriver
    this.appState = props.appState
    this.errorDisplayDelegate = props.errorDisplayDelegate
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

  private get ipAddress () {
    return this.appState.IP || translations.IP_UPDATING
  }

  private get sessionDuration () {
    if (!this.appState.Statistics) {
      return 0
    }

    return this.appState.Statistics.duration
  }

  private get sessionBytesSent () {
    if (!this.appState.Statistics) {
      return 0
    }

    return this.appState.Statistics.bytesSent
  }

  private get sessionBytesReceived () {
    if (!this.appState.Statistics) {
      return 0
    }

    return this.appState.Statistics.bytesReceived
  }

  private get countries () {
    return proposalsToCountries(this.appState.Proposals)
  }

  private onCountrySelect (country: Country) {
    this.props.appState.SelectedProviderId = country.id
  }

  private get buttonIsReady (): boolean {
    return this.appState.isReady
  }

  private get buttonIsActive (): boolean {
    return this.appState.isConnected
  }

  private get connectionStatusText () {
    return this.appState.ConnectionStatus
      ? this.appState.ConnectionStatus.status
      : undefined
  }

  private onConnectButtonClick () {
    if (!this.appState.isConnected && !this.appState.SelectedProviderId) {
      Toast.show({
        text: translations.UNSELECTED_COUNTRY,
        textStyle: {
          textAlign: 'center'
        }
      })
      return
    }

    this.connectOrDisconnect()
  }

  /**
   * Connects or disconnects to VPN server, depends on current connection state.
   * Is connection state is unknown - does nothing
   */
  private async connectOrDisconnect () {
    if (!this.appState.isReady) {
      return
    }

    if (this.appState.isConnected) {
      await this.tequilAPIDriver.disconnect()
    } else {
      await this.tequilAPIDriver.connect()
    }
  }
}
