/*
 * Copyright (C) 2018 The 'MysteriumNetwork/mysterion' Authors.
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

type AppStyles = {
  container: ViewStyle,
  controls: ViewStyle,
  footer: ViewStyle,
  imageBackground: ViewStyle,
  textIp: ViewStyle,
  countryPicker: ViewStyle
}

export default StyleSheet.create({
  container: {
    alignItems: 'center',
    backgroundColor: '#fff',
    flex: 1
  },
  controls: {
    width: '100%',
    top: 270,
    alignItems: 'center'
  },
  footer: {
    position: 'absolute',
    bottom: 0
  },
  imageBackground: {
    top: 10,
    position: 'absolute',
    width: '100%'
  },
  textIp: {
    marginTop: STYLES.MARGIN,
    fontSize: STYLES.FONT_NORMAL,
    color: STYLES.COLOR_SECONDARY
  },
  countryPicker: {
    width: '90%',
    marginTop: 10,
    marginBottom: 10,
    height: 50
  }
}) as AppStyles
