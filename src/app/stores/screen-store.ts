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

import { action, computed, observable } from 'mobx'

const LOADING_SCREEN = 0
const VPN_SCREEN = 1
const FEEDBACK_SCREEN = 2

export default class ScreenStore {
  @observable
  private screen: number = LOADING_SCREEN

  @computed
  public get inLoadingScreen (): boolean {
    return this.screen === LOADING_SCREEN
  }

  @computed
  public get inVpnScreen (): boolean {
    return this.screen === VPN_SCREEN
  }

  @computed
  public get inFeedbackScreen (): boolean {
    return this.screen === FEEDBACK_SCREEN
  }

  @action
  public navigateToVpnScreen () {
    console.log('Opening VPN screen')
    this.screen = VPN_SCREEN
  }

  @action
  public navigateToFeedbackScreen () {
    console.log('Opening Feedback screen')
    this.screen = FEEDBACK_SCREEN
  }
}
