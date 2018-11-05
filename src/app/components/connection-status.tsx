import { computed } from 'mobx'
import React, { Component } from 'react'
import { StyleSheet, Text } from 'react-native'
import { CONFIG } from '../../config'
import { ConnectionStatusEnum } from '../../libraries/tequilAPI/enums'
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

  @computed
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
