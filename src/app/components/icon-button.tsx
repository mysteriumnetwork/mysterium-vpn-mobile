import { Icon } from 'native-base'
import React, { Component } from 'react'
import { StyleSheet, TouchableOpacity } from 'react-native'
import colors from '../styles/colors'

type ButtonProps = {
  onClick: () => void,
  icon: string
}

export default class IconButton extends Component<ButtonProps> {
  public render () {
    return (
      <TouchableOpacity
        style={[styles.button]}
        onPress={() => this.props.onClick()}
      >
        <Icon style={styles.icon} name={this.props.icon}/>
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
