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

export interface IProps {
  onClick: () => void,
  disabled: boolean,
  active: boolean,
}

const styles: any = StyleSheet.create({
  container: {
    height: 60,
    width: 60
  },
  button: {
    borderColor: colors.border,
    borderRadius: 100,
    width: 60,
    height: 60,
    justifyContent: 'center',
    borderWidth: 0
  },
  buttonDisabled: {
    borderWidth: 0
  },
  powerIcon: {
    fontSize: 30,
    fontWeight: '800'
  },
  powerIconActive: {
    color: '#266e2e'
  },
  spinner: {
    marginTop: -5
  }
})

class ConnectButton extends React.Component<IProps, any> {
  public render () {
    if (this.props.disabled) {
      return (
        <View style={styles.container}>
          <Spinner color="red" style={styles.spinner}/>
        </View>
      )
    }

    return (
      <View style={styles.container}>
        <Button
          bordered={true}
          onPress={() => this.props.onClick()}
          style={[styles.button, this.props.disabled ? styles.buttonDisabled : null]}
        >
          <Icon style={[styles.powerIcon, this.props.active ? styles.powerIconActive : null]} name="ios-power"/>
        </Button>
      </View>
    )
  }
}

export default ConnectButton
