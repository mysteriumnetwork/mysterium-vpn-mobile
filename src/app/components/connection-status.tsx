import React, { Component } from 'react'
import { StyleSheet, Text, Platform } from 'react-native'
import { ConnectionStatusEnum } from '../../libraries/tequil-api/enums'
import colors from '../styles/colors'
import fonts from '../styles/fonts'
import translations from '../../app/translations'

type ConnectionStatusProps = {
  status?: string
}

export default class ConnectionStatus extends Component<ConnectionStatusProps> {
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
