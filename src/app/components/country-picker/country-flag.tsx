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

import countries from './countries.json'
import { Icon } from 'native-base'
import { Image, StyleSheet } from 'react-native'
import * as React from 'react'
import colors from '../../styles/colors'

type FlagProps = {
  countryCode?: string
}

const getCountryImageUri = (code: string) => {
  if (countries[code]) {
    return countries[code].image
  }

  return ''
}

const CountryFlag: React.SFC<FlagProps> = ({ countryCode }) => {
  if (!countryCode) {
    return (
      <Icon style={styles.globeIcon} name={'ios-globe'}/>
    )
  }

  return (
    <Image
      source={{ uri: getCountryImageUri(countryCode) }}
      style={styles.countryFlagImage}
    />
  )
}

const styles = StyleSheet.create({
  countryFlagImage: {
    width: 26,
    height: 26,
    borderRadius: 13
  },
  globeIcon: {
    color: colors.primary
  }
})

export default CountryFlag
