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
