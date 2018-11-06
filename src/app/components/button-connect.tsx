import { ConnectionStatusDTO } from 'mysterium-tequilapi'
import React, { Component } from 'react'
import { StyleProp, StyleSheet, Text, TouchableOpacity, ViewStyle } from 'react-native'
import { CONFIG } from '../../config'
import { ConnectionStatusEnum } from '../../libraries/tequilAPI/enums'
import { STYLES } from '../../styles'

type ButtonConnectProps = {
  connectionStatus?: ConnectionStatusDTO
  onPress: () => void
}

export default class ButtonConnect extends Component<ButtonConnectProps> {
  public render () {
    let buttonStylesDisabled: StyleProp<ViewStyle>
    let textStylesDisabled: StyleProp<ViewStyle>

    if (!this.buttonEnabled) {
      buttonStylesDisabled = styles.disabledRoot
      textStylesDisabled = styles.disabledButtonContent
    }

    return (
      <TouchableOpacity
        activeOpacity={0.6}
        style={[styles.root, buttonStylesDisabled]}
        onPress={this.props.onPress}
      >
        <Text style={[styles.buttonContent, textStylesDisabled]}>
          {this.buttonText}
        </Text>
      </TouchableOpacity>
    )
  }
  private get buttonEnabled (): boolean {
    if (!this.props.connectionStatus) return false
    const connectionStatus = this.props.connectionStatus.status
    return (connectionStatus === ConnectionStatusEnum.NOT_CONNECTED
      || connectionStatus === ConnectionStatusEnum.CONNECTED
      || connectionStatus === ConnectionStatusEnum.CONNECTING
    )
  }

  private get buttonText (): string {
    let text: string = CONFIG.TEXTS.UNKNOWN
    if (!this.props.connectionStatus) return text

    const connectionStatus = this.props.connectionStatus.status
    switch (connectionStatus) {
      case ConnectionStatusEnum.NOT_CONNECTED:
        text = 'Connect'
        break
      case ConnectionStatusEnum.CONNECTED:
        text = 'Disconnect'
        break
      case ConnectionStatusEnum.CONNECTING:
        text = 'Cancel'
        break
      case ConnectionStatusEnum.DISCONNECTING:
        text = 'Disconnecting'
        break
    }
    return text
  }
}

const styles = StyleSheet.create({
  root: {
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
