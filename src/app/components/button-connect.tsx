import React, { Component } from 'react'
import { StyleProp, StyleSheet, Text, TouchableOpacity, ViewStyle } from 'react-native'
import { CONFIG } from '../../config'

type ButtonConnectProps = {
  title: string,
  disabled: boolean,
  onPress: () => void
}

export default class ButtonConnect extends Component<ButtonConnectProps> {
  public render () {
    let buttonStylesDisabled: StyleProp<ViewStyle>
    let textStylesDisabled: StyleProp<ViewStyle>

    if (this.props.disabled) {
      buttonStylesDisabled = styles.disabledRoot
      textStylesDisabled = styles.disabledButtonContent
    }

    return (
      <TouchableOpacity
        style={[styles.root, buttonStylesDisabled]}
        onPress={this.props.onPress}
      >
        <Text style={[styles.buttonContent, textStylesDisabled]}>
          {this.props.title}
        </Text>
      </TouchableOpacity>
    )
  }
}

const styles = StyleSheet.create({
  root: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'center',
    padding: CONFIG.STYLES.PADDING,
    borderRadius: 10,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 1
    },
    shadowOpacity: 0.35,
    shadowRadius: 20,
    borderColor: CONFIG.STYLES.COLOR_MAIN,
    borderWidth: 1,
    backgroundColor: CONFIG.STYLES.COLOR_BACKGROUND
  },
  disabledRoot: {
    borderColor: CONFIG.STYLES.COLOR_DISABLED
  },
  buttonContent: {
    fontSize: CONFIG.STYLES.FONT_NORMAL,
    color: CONFIG.STYLES.COLOR_MAIN
  },
  disabledButtonContent: {
    color: CONFIG.STYLES.COLOR_DISABLED
  }
})
