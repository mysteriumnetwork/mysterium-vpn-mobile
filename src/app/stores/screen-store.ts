/*
 * Copyright (C) 2018 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
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

import { action, computed, observable } from 'mobx'

export default class ScreenStore {
  @observable
  private screen: Screen = Screen.Terms

  @computed
  public get inTermsScreen (): boolean {
    return this.screen === Screen.Terms
  }

  @computed
  public get inLoadingScreen (): boolean {
    return this.screen === Screen.Loading
  }

  @computed
  public get inVpnScreen (): boolean {
    return this.screen === Screen.Vpn
  }

  @computed
  public get inFeedbackScreen (): boolean {
    return this.screen === Screen.Feedback
  }

  @action
  public navigateToLoadingScreen () {
    console.log('Opening loading screen')
    this.screen = Screen.Loading
  }

  @action
  public navigateToVpnScreen () {
    console.log('Opening VPN screen')
    this.screen = Screen.Vpn
  }

  @action
  public navigateToFeedbackScreen () {
    console.log('Opening Feedback screen')
    this.screen = Screen.Feedback
  }
}

enum Screen {
  Terms,
  Loading,
  Vpn,
  Feedback
}
