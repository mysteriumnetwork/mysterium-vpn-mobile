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

import React, { Component } from 'react'
import { Platform, StyleSheet, Text } from 'react-native'
import translations from '../../app/translations'
import { ConnectionStatusEnum } from '../../libraries/tequil-api/enums'
import colors from '../styles/colors'
import fonts from '../styles/fonts'

type ConnectionStatusProps = {
  status?: string
}

class ConnectionStatus extends Component<ConnectionStatusProps> {
  private readonly connectionStatusTexts: { [key: string]: string | undefined } = {
    [ConnectionStatusEnum.NOT_CONNECTED]: translations.CONNECTION_STATUS.NOT_CONNECTED,
    [ConnectionStatusEnum.CONNECTING]: translations.CONNECTION_STATUS.CONNECTING,
    [ConnectionStatusEnum.CONNECTED]: translations.CONNECTION_STATUS.CONNECTED,
    [ConnectionStatusEnum.DISCONNECTING]: translations.CONNECTION_STATUS.DISCONNECTING
  }

  public render () {
    return (
      <Text style={style.root}>{this.connectionStatus}</Text>
    )
  }

  private get connectionStatus (): string {
    const status = this.props.status

    if (status === undefined) {
      return translations.CONNECTION_STATUS.UNKNOWN
    }

    const text = this.connectionStatusTexts[status]
    if (text === undefined) {
      throw new Error(`Unknown connection status: ${status}`)
    }

    return text
  }
}

const style = StyleSheet.create({
  root: {
    marginTop: Platform.OS === 'ios' ? 35 : 20,
    fontSize: fonts.size.large,
    color: colors.primary
  }
})

export default ConnectionStatus
