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
import { View } from 'react-native'
import IFeedbackReporter from '../bug-reporter/feedback-reporter'
import TequilApiDriver from '../libraries/tequil-api/tequil-api-driver'
import AppLoader from './app-loader'
import styles from './app-styles'
import ErrorDropdown from './components/error-dropdown'
import Terms from './domain/terms'
import MessageDisplayDelegate from './messages/message-display-delegate'
import Favorites from './proposals/favorites'
import FeedbackScreen from './screens/feedback-screen'
import LoadingScreen from './screens/loading-screen'
import TermsScreen from './screens/terms-screen'
import VpnScreen from './screens/vpn-screen'
import ConnectionStore from './stores/connection-store'
import ScreenStore from './stores/screen-store'
import VpnScreenStore from './stores/vpn-screen-store'

type AppProps = {
  tequilAPIDriver: TequilApiDriver,
  connectionStore: ConnectionStore,
  vpnScreenStore: VpnScreenStore,
  screenStore: ScreenStore,
  messageDisplayDelegate: MessageDisplayDelegate,
  terms: Terms,
  favorites: Favorites,
  appLoader: AppLoader,
  feedbackReporter: IFeedbackReporter
}

@observer
export default class App extends React.Component<AppProps> {
  private readonly tequilAPIDriver: TequilApiDriver
  private readonly connectionStore: ConnectionStore
  private readonly messageDisplayDelegate: MessageDisplayDelegate
  private readonly terms: Terms
  private readonly vpnScreenStore: VpnScreenStore
  private readonly screenStore: ScreenStore
  private readonly favorites: Favorites
  private readonly appLoader: AppLoader
  private readonly feedbackReporter: IFeedbackReporter

  constructor (props: AppProps) {
    super(props)
    this.tequilAPIDriver = props.tequilAPIDriver
    this.connectionStore = props.connectionStore
    this.messageDisplayDelegate = props.messageDisplayDelegate
    this.terms = props.terms
    this.vpnScreenStore = props.vpnScreenStore
    this.screenStore = props.screenStore
    this.favorites = props.favorites
    this.appLoader = props.appLoader
    this.feedbackReporter = props.feedbackReporter
  }

  public render (): ReactNode {
    return (
      <View style={styles.app}>
        {this.renderCurrentScreen()}
        <ErrorDropdown ref={(ref: ErrorDropdown) => this.messageDisplayDelegate.messageDisplay = ref}/>
      </View>
    )
  }

  private renderCurrentScreen (): ReactNode {
    if (this.screenStore.inTermsScreen) {
      return <TermsScreen terms={this.terms} close={() => this.screenStore.navigateToLoadingScreen()}/>
    }

    if (this.screenStore.inLoadingScreen) {
      return <LoadingScreen load={() => this.load()} />
    }

    if (this.screenStore.inFeedbackScreen) {
      return (
        <FeedbackScreen
          feedbackReporter={this.feedbackReporter}
          navigateBack={() => this.screenStore.navigateToVpnScreen()}
        />
      )
    }

    return (
      <VpnScreen
        tequilAPIDriver={this.tequilAPIDriver}
        connectionStore={this.connectionStore}
        vpnScreenStore={this.vpnScreenStore}
        screenStore={this.screenStore}
        favorites={this.favorites}
        messageDisplay={this.messageDisplayDelegate}
      />
    )
  }

  private async load () {
    try {
      await this.appLoader.load()
      this.screenStore.navigateToVpnScreen()
    } catch (err) {
      console.log('App loading failed', err)
    }
  }
}
