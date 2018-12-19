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

import { StyleSheet } from 'react-native'
import { STYLES } from '../styles'

export default StyleSheet.create({
  app: {
    backgroundColor: '#fff',
    flex: 1
  },
  screen: {
    alignItems: 'center',
    flex: 1
  },
  feedback: {
    position: 'absolute',
    top: 10,
    left: 10
  },
  controls: {
    width: '100%',
    alignItems: 'center',
    position: 'absolute',
    bottom: 30
  },
  imageBackground: {
    width: '100%',
    flex: 1
  },
  textIp: {
    marginTop: STYLES.MARGIN,
    fontSize: STYLES.FONT_NORMAL,
    color: STYLES.COLOR_SECONDARY
  },
  proposalPicker: {
    width: '90%',
    marginTop: 10,
    marginBottom: 10
  },
  controlsWithLogoContainer: {
    flex: 1,
    width: '100%'
  }
})
