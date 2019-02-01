/*
 * Copyright (C) 2018 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
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

import React from 'react'
import { StyleSheet, Text, TouchableOpacity } from 'react-native'
import { STYLES } from '../../styles'

type ButtonProps = {
  onPress: () => void
}

const TextButton: React.SFC<ButtonProps> = ({ onPress, children }) => {
  return (
    <TouchableOpacity onPress={onPress} style={styles.touchable}>
      <Text style={styles.text}>{children}</Text>
    </TouchableOpacity>
  )
}

const styles = StyleSheet.create({
  touchable: {
    alignItems: 'center',
    borderRadius: 10,
    padding: STYLES.PADDING,
    borderColor: STYLES.COLOR_MAIN,
    borderWidth: 1
  },
  text: {
    color: STYLES.COLOR_MAIN
  }
})

export default TextButton
