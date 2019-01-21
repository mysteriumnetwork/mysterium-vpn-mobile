/*
 * Copyright (C) 2019 The 'mysteriumnetwork/mysterium-vpn-mobile' Authors.
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
import { Image, ImageRequireSource, StyleSheet, ViewStyle } from 'react-native'
import { ServiceType } from '../../models/service-type'

type ServiceIndicatorProps = {
  serviceType: ServiceType,
  style?: ViewStyle
}

const ServiceIndicator: React.SFC<ServiceIndicatorProps> = ({ serviceType, style }) => {
  const icon = getIconImage(serviceType)
  return (
    <Image style={[styles.image, style]} source={icon} resizeMode="contain" />
  )
}

function getIconImage (serviceType: ServiceType): ImageRequireSource {
  if (serviceType === ServiceType.Openvpn) {
    return require('../../../assets/services/openvpn.png')
  }
  if (serviceType === ServiceType.Wireguard) {
    return require('../../../assets/services/wireguard.png')
  }
  throw new Error('Unknown service type')
}

const ICON_SIZE = 20

const styles = StyleSheet.create({
  image: {
    width: ICON_SIZE,
    height: ICON_SIZE
  }
})

export { ServiceIndicator }
