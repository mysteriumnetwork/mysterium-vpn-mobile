/*
 * Copyright (C) 2018 The 'MysteriumNetwork/mysterium-vpn-mobile' Authors.
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

import {
  Button,
  Icon,
  Spinner
} from 'native-base'
import * as React from 'react'

import { StyleSheet, View } from 'react-native'
import colors from '../styles/colors'

type ButtonProps = {
  onClick: () => void,
  loading: boolean,
  active: boolean
}

class ConnectButton extends React.Component<ButtonProps> {
  public render () {
    if (this.props.loading) {
      return this.spinner
    }

    return (
      <View style={styles.container}>
        <Button
          onPress={() => this.props.onClick()}
          style={this.buttonStyle}
        >
          {this.icon}
        </Button>
      </View>
    )
  }

  private get spinner () {
    return (
      <View style={styles.container}>
        <Spinner color="red" style={styles.spinner}/>
      </View>
    )
  }

  private get icon () {
    if (this.props.active) {
      return (<Icon style={[styles.icon, styles.iconActive]} name="md-checkmark"/>)
    }

    return (<Icon style={[styles.icon, styles.iconInactive]} name="ios-power"/>)
  }

  private get buttonStyle () {
    const style = [styles.button]

    if (this.props.active) {
      style.push(styles.buttonConnected)
    }

    return style
  }
}

const styles = StyleSheet.create({
  container: {
    height: 65,
    width: 65
  },
  button: {
    borderColor: colors.primary,
    width: 62,
    height: 62,
    borderRadius: 31,
    justifyContent: 'center',
    borderWidth: 1.5,
    backgroundColor: 'transparent'
  },
  buttonConnected: {
    borderColor: colors.success
  },
  icon: {
    color: colors.primary,
    fontSize: 28,
    fontWeight: '600'
  },
  iconActive: {
    color: colors.success
  },
  iconInactive: {
    color: colors.primary
  },
  spinner: {
    marginTop: -5
  }
})

export default ConnectButton
