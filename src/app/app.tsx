/*
 * Copyright (C) 2018 The 'MysteriumNetwork/mysterion' Authors.
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
import React, { ReactNode } from 'react'
import { Image, Text, View } from 'react-native'
import { CONFIG } from '../config'
import TequilApiDriver from '../libraries/tequil-api/tequil-api-driver'
import AppLoader from './app-loader'
import styles from './app-styles'
import ButtonConnect from './components/button-connect'
import ConnectionStatus from './components/connection-status'
import { ICountry } from './components/country-picker/country'
import CountryPicker from './components/country-picker/country-picker'
import ErrorDropdown from './components/error-dropdown'
import Stats from './components/stats'
import CountryList from './countries/country-list'
import Favorites from './countries/favorites'
import ErrorDisplayDelegate from './errors/error-display-delegate'
import ConnectionStore from './stores/connection-store'
import translations from './translations'
import VpnAppState from './vpn-app-state'

type AppProps = {
  tequilAPIDriver: TequilApiDriver,
  connectionStore: ConnectionStore,
  vpnAppState: VpnAppState,
  errorDisplayDelegate: ErrorDisplayDelegate,
  countryList: CountryList,
  favorites: Favorites,
  appLoader: AppLoader
}

@observer
export default class App extends React.Component<AppProps> {
  private readonly tequilAPIDriver: TequilApiDriver
  private readonly connectionStore: ConnectionStore
  private readonly errorDisplayDelegate: ErrorDisplayDelegate
  private readonly vpnAppState: VpnAppState
  private readonly countryList: CountryList
  private readonly favorites: Favorites
  private readonly appLoader: AppLoader

  constructor (props: AppProps) {
    super(props)
    this.tequilAPIDriver = props.tequilAPIDriver
    this.connectionStore = props.connectionStore
    this.errorDisplayDelegate = props.errorDisplayDelegate
    this.vpnAppState = props.vpnAppState
    this.countryList = props.countryList
    this.favorites = props.favorites
    this.appLoader = props.appLoader
  }

  public render (): ReactNode {
    return (
      <View style={styles.container}>
        <Image
          style={styles.imageBackground}
          source={require('../assets/background-logo.png')}
          resizeMode="contain"
        />

        <ConnectionStatus status={this.connectionStore.connection.connectionStatus.status}/>

        <Text style={styles.textIp}>IP: {this.connectionStore.connection.IP || CONFIG.TEXTS.IP_UPDATING}</Text>

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
            connectionStatus={this.connectionStore.connection.connectionStatus.status}
            connect={this.tequilAPIDriver.connect.bind(this.tequilAPIDriver, this.vpnAppState.selectedProviderId)}
            disconnect={this.tequilAPIDriver.disconnect.bind(this.tequilAPIDriver)}
          />
        </View>

        <View style={styles.footer}>
          <Stats
            duration={this.connectionStore.connection.connectionStatistics.duration}
            bytesReceived={this.connectionStore.connection.connectionStatistics.bytesReceived}
            bytesSent={this.connectionStore.connection.connectionStatistics.bytesSent}
          />
        </View>

        <ErrorDropdown ref={(ref: ErrorDropdown) => this.errorDisplayDelegate.errorDisplay = ref}/>
      </View>
    )
  }

  public async componentDidMount () {
    try {
      await this.appLoader.load()
    } catch (err) {
      console.log('App loading failed', err)
    }
  }
}
