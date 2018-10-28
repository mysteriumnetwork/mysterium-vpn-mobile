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
import { CONFIG } from '../config'

type AppStyles = {
  container: ViewStyle,
  footer: ViewStyle,
  imageLoader: ViewStyle,
  textStatus: ViewStyle,
  textIp: ViewStyle
}

export default StyleSheet.create({
  container: {
    alignItems: 'center',
    backgroundColor: '#fff',
    flex: 1
  },
  footer: {
    top: 270,
    alignItems: 'center'
  },
  imageLoader: {
    top: -25,
    position: 'absolute',
    height: 720,
    left: 0,
    width: '100%'
  },
  textStatus: {
    marginTop: 45,
    fontSize: CONFIG.STYLES.FONT_LARGE,
    color: CONFIG.STYLES.COLOR_MAIN
  },
  textIp: {
    marginTop: CONFIG.STYLES.MARGIN,
    fontSize: CONFIG.STYLES.FONT_NORMAL,
    color: CONFIG.STYLES.COLOR_SECONDARY
  }
}) as AppStyles
