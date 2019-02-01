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

import React, { Component } from 'react'
import { StyleSheet, Text } from 'react-native'
import { CONFIG } from '../../config'
import { ConnectionStatusEnum } from '../../libraries/tequil-api/enums'
import { STYLES } from '../../styles'

type ConnectionStatusProps = {
  status?: string
}

export default class ConnectionStatus extends Component<ConnectionStatusProps> {
  private readonly connectionStatusTexts: { [key: string]: string | undefined } = {
    [ConnectionStatusEnum.NOT_CONNECTED]: this.statusTexts.NOT_CONNECTED,
    [ConnectionStatusEnum.CONNECTING]: this.statusTexts.CONNECTING,
    [ConnectionStatusEnum.CONNECTED]: this.statusTexts.CONNECTED,
    [ConnectionStatusEnum.DISCONNECTING]: this.statusTexts.DISCONNECTING
  }

  public render () {
    return (
      <Text style={style.root}>{this.connectionStatus}</Text>
    )
  }

  private get connectionStatus (): string {
    const status = this.props.status

    if (status === undefined) {
      return this.statusTexts.UNKNOWN
    }

    const text = this.connectionStatusTexts[status]
    if (text === undefined) {
      throw new Error(`Unknown connection status: ${status}`)
    }

    return text
  }

  private get statusTexts () {
    return CONFIG.TEXTS.CONNECTION_STATUS
  }
}

const style = StyleSheet.create({
  root: {
    marginTop: 45,
    fontSize: STYLES.FONT_LARGE,
    color: STYLES.COLOR_MAIN
  }
})
