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

import { Dimensions, StyleSheet } from 'react-native'
import colors from '../../styles/colors'
import fonts from '../../styles/fonts'

const height = Dimensions.get('window').height

const styles = StyleSheet.create({
  backgroundImage: {
    width: '100%',
    height: '100%'
  },
  textCentered: {
    justifyContent: 'center'
  },
  ipText: {
    marginTop: 15,
    fontSize: fonts.size.small,
    color: colors.secondary
  },
  connectButton: {
    marginTop: 20
  },
  statsContainer: {
    marginTop: 30,
    padding: 15,
    borderTopWidth: 0.5,
    borderColor: colors.border
  },
  connectionContainer: {
    marginTop: height - 380
  },
  countryPicker: {
    paddingLeft: 10,
    paddingRight: 10
  }
})

export default styles
