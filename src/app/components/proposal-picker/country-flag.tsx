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

import { Icon } from 'native-base'
import * as React from 'react'
import { Image, ImageStyle, StyleProp, StyleSheet } from 'react-native'
import colors from '../../styles/colors'
import countries from './countries'

type FlagProps = {
  countryCode: string | null,
  showPlaceholder: boolean,
  style?: StyleProp<ImageStyle>
}

const CountryFlag: React.SFC<FlagProps> = ({ countryCode, showPlaceholder, style }) => {
  const uri = getCountryImageUri(countryCode)
  if (!uri) {
    if (showPlaceholder) {
      return (<Icon style={[styles.globeIcon, style]} name={'ios-globe'}/>)
    }

    return (null)
  }

  return (
    <Image
      source={{ uri }}
      style={[styles.countryFlagImage, style]}
    />
  )
}

const getCountryImageUri = (code: string | null): string | null => {
  if (code && countries[code]) {
    return countries[code].image
  }

  return null
}

const styles = StyleSheet.create({
  countryFlagImage: {
    width: 26,
    height: 26,
    borderRadius: 13,
    borderWidth: 0.5,
    borderColor: colors.border
  },
  globeIcon: {
    color: colors.primary
  }
})

export default CountryFlag
