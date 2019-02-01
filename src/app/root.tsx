/*
 * Copyright (C) 2019 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
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

import { Root as RootBase } from 'native-base'
import * as React from 'react'
import { onIdentityUnlockSetUserIdInBugReporter, setupGlobalErrorHandler } from '../bug-reporter/utils'
import App from './app'
import Container from './container'

class Root extends React.PureComponent {
  private readonly container = new Container()

  public async componentWillMount () {
    setupGlobalErrorHandler(this.container.bugReporter)

    onIdentityUnlockSetUserIdInBugReporter(this.container.identityManager, this.container.bugReporter)

    await this.container.favoritesStorage.fetch()
  }

  public render () {
    return (
      <RootBase>
        <App
          tequilAPIDriver={this.container.tequilAPIDriver}
          connectionStore={this.container.connectionStore}
          vpnScreenStore={this.container.vpnScreenStore}
          screenStore={this.container.screenStore}
          messageDisplayDelegate={this.container.messageDisplayDelegate}
          terms={this.container.terms}
          favorites={this.container.favorites}
          appLoader={this.container.appLoader}
          feedbackReporter={this.container.feedbackReporter}
        />
      </RootBase>
    )
  }
}

export default Root
