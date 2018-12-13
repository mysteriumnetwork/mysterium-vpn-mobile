import { Icon } from 'native-base'
import React, { Component } from 'react'
import { StyleSheet, TouchableOpacity } from 'react-native'
import colors from '../styles/colors'

type ButtonProps = {
  onClick: () => void
}

export default class FeedbackButton extends Component<ButtonProps> {
  public render () {
    return (
      <TouchableOpacity
        style={[styles.button]}
        onPress={() => this.props.onClick()}
      >
        <Icon style={styles.icon} name="ios-help-circle-outline"/>
      </TouchableOpacity>
    )
  }
}

const styles = StyleSheet.create({
  button: {
    width: 40,
    height: 40
  },
  icon: {
    color: colors.primary,
    fontSize: 40
  }
})
