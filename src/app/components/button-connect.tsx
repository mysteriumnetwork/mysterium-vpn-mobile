import { ConnectionStatus } from 'mysterium-tequilapi'
import React, { Component } from 'react'
import { StyleProp, StyleSheet, Text, TouchableOpacity, ViewStyle } from 'react-native'
import { CONFIG } from '../../config'
import { ConnectionStatusEnum } from '../../libraries/tequil-api/enums'
import { STYLES } from '../../styles'

type ButtonConnectProps = {
  connectionStatus: ConnectionStatus
  connect: () => void,
  disconnect: () => void
}

export default class ButtonConnect extends Component<ButtonConnectProps> {
  public render () {
    let buttonStylesDisabled: StyleProp<ViewStyle>
    let textStylesDisabled: StyleProp<ViewStyle>

    if (!this.isButtonEnabled) {
      buttonStylesDisabled = styles.disabledRoot
      textStylesDisabled = styles.disabledButtonContent
    }

    return (
      <TouchableOpacity
        activeOpacity={0.6}
        style={[styles.root, buttonStylesDisabled]}
        onPress={this.connectOrDisconnectOrCancel}
      >
        <Text style={[styles.buttonContent, textStylesDisabled]}>
          {this.buttonText}
        </Text>
      </TouchableOpacity>
    )
  }

  private get isButtonEnabled (): boolean {
    if (!this.props.connectionStatus) return false
    const connectionStatus = this.props.connectionStatus
    return (connectionStatus === ConnectionStatusEnum.NOT_CONNECTED
      || connectionStatus === ConnectionStatusEnum.CONNECTED
      || connectionStatus === ConnectionStatusEnum.CONNECTING
    )
  }

  /***
   * Connects or disconnects to VPN server, depends on current connection state.
   * Is connection state is unknown - does nothing
   */
  private connectOrDisconnectOrCancel = async () => {
    if (!this.props.connectionStatus) return
    const status = this.props.connectionStatus

    if (status === ConnectionStatusEnum.CONNECTING
      || status === ConnectionStatusEnum.CONNECTED) {
      this.props.disconnect()
    }

    if (status === ConnectionStatusEnum.NOT_CONNECTED) {
      this.props.connect()
    }
  }

  private get buttonText (): string {
    if (!this.props.connectionStatus) return CONFIG.TEXTS.UNKNOWN

    const connectionStatus = this.props.connectionStatus
    switch (connectionStatus) {
      case ConnectionStatusEnum.NOT_CONNECTED:
        return CONFIG.TEXTS.CONNECT_BUTTON.CONNECT
      case ConnectionStatusEnum.CONNECTED:
        return CONFIG.TEXTS.CONNECT_BUTTON.CONNECTED
      case ConnectionStatusEnum.CONNECTING:
        return CONFIG.TEXTS.CONNECT_BUTTON.CONNECTING
      case ConnectionStatusEnum.DISCONNECTING:
        return CONFIG.TEXTS.CONNECT_BUTTON.DISCONNECTING
      default:
        return CONFIG.TEXTS.UNKNOWN
    }
  }
}

const styles = StyleSheet.create({
  root: {
    width: 220,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    padding: STYLES.PADDING,
    borderRadius: 10,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 1
    },
    shadowOpacity: 0.35,
    shadowRadius: 20,
    borderColor: STYLES.COLOR_MAIN,
    borderWidth: 1,
    backgroundColor: STYLES.COLOR_BACKGROUND
  },
  disabledRoot: {
    borderColor: STYLES.COLOR_DISABLED
  },
  buttonContent: {
    fontSize: STYLES.FONT_NORMAL,
    color: STYLES.COLOR_MAIN
  },
  disabledButtonContent: {
    color: STYLES.COLOR_DISABLED
  }
})
