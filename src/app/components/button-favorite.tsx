import React, { Component } from 'react'
import { StyleSheet, Text, TouchableOpacity } from 'react-native'
import { CONFIG } from '../../config'

type ButtonFavoriteProps = {
  title: string,
  onPress: () => void
}

export default class ButtonFavorite extends Component<ButtonFavoriteProps> {
  public render () {
    return (
      <TouchableOpacity
        style={styles.root}
        onPress={this.props.onPress}
      >
        <Text style={styles.buttonContent}>
          {this.props.title}
        </Text>
      </TouchableOpacity>
    )
  }
}

const styles = StyleSheet.create({
  root: {
    alignItems: 'center',
    justifyContent: 'center',
    width: 50,
    height: 40
  },
  buttonContent: {
    fontSize: 45,
    color: CONFIG.STYLES.COLOR_MAIN
  }
})
