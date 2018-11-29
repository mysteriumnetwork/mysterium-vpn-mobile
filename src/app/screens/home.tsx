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

import React from 'react'
import { Text, View } from 'react-native'
import { CONFIG } from '../../config'
import TequilApiDriver from '../../libraries/tequil-api/tequil-api-driver'
import styles from '../app-styles'
import ButtonConnect from '../components/button-connect'
import ConnectionStatus from '../components/connection-status'
import { ICountry } from '../components/country-picker/country'
import CountryPicker from '../components/country-picker/country-picker'
import ErrorDropdown from '../components/error-dropdown'
import LogoBackground from '../components/logo-background'
import Stats from '../components/stats'
import CountryList from '../countries/country-list'
import Favorites from '../countries/favorites'
import ErrorDisplayDelegate from '../errors/error-display-delegate'
import ConnectionStore from '../stores/connection-store'
import translations from '../translations'
import VpnAppState from '../vpn-app-state'

type HomeProps = {
  tequilAPIDriver: TequilApiDriver,
  connectionStore: ConnectionStore,
  vpnAppState: VpnAppState,
  errorDisplayDelegate: ErrorDisplayDelegate,
  countryList: CountryList,
  favorites: Favorites
}

class Home extends React.Component<HomeProps> {
  private readonly tequilAPIDriver: TequilApiDriver
  private readonly connectionStore: ConnectionStore
  private readonly errorDisplayDelegate: ErrorDisplayDelegate
  private readonly vpnAppState: VpnAppState
  private readonly countryList: CountryList
  private readonly favorites: Favorites

  constructor (props: HomeProps) {
    super(props)
    this.tequilAPIDriver = props.tequilAPIDriver
    this.connectionStore = props.connectionStore
    this.errorDisplayDelegate = props.errorDisplayDelegate
    this.vpnAppState = props.vpnAppState
    this.countryList = props.countryList
    this.favorites = props.favorites
  }

  public render (): React.ReactNode {
    const connectionData = this.connectionStore.data

    return (
      <View style={styles.container}>
        <LogoBackground/>

        <ConnectionStatus status={connectionData.status}/>

        <Text style={styles.textIp}>IP: {connectionData.IP || CONFIG.TEXTS.IP_UPDATING}</Text>

        <View style={styles.controls}>
          <View style={styles.countryPicker}>
            <CountryPicker
              placeholder={translations.COUNTRY_PICKER_LABEL}
              countries={this.countryList.countries}
              onSelect={(country: ICountry) => this.vpnAppState.selectedProviderId = country.providerID}
              onFavoriteToggle={() => this.favorites.toggle(this.vpnAppState.selectedProviderId)}
              isFavoriteSelected={this.favorites.isFavored(this.vpnAppState.selectedProviderId)}
            />
          </View>

          <ButtonConnect
            connectionStatus={connectionData.status}
            connect={this.tequilAPIDriver.connect.bind(this.tequilAPIDriver, this.vpnAppState.selectedProviderId)}
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

        <ErrorDropdown ref={(ref: ErrorDropdown) => this.errorDisplayDelegate.errorDisplay = ref}/>
      </View>
    )
  }
}

export default Home
