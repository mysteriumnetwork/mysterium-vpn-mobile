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

import { StyleSheet, ViewStyle } from 'react-native'
import { STYLES } from '../styles'

type ProposalsStyles = {
  root: ViewStyle,
  picker: ViewStyle
}

export default StyleSheet.create({
  root: {
    flexDirection: 'row',
    marginBottom: STYLES.MARGIN,
    borderWidth: 1,
    borderColor: STYLES.COLOR_SECONDARY
  },
  picker: {
    width: 300,
    height: 50,
    backgroundColor: STYLES.COLOR_BACKGROUND
  }
}) as ProposalsStyles
