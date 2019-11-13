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
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

import React from 'react'
import { Image } from 'react-native'
import styles from '../app-styles'

const LogoBackground: React.FunctionComponent = () => {
  return (
    <Image
      style={styles.imageBackground}
      fadeDuration={0}
      source={require('../../assets/background-logo.png')}
      resizeMode="contain"
    />
  )
}

export default LogoBackground
