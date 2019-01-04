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
import { Image, ImageRequireSource, StyleSheet } from 'react-native'

type QualityIndicatorProps = {
  quality: number | null
}

const QualityIndicator: React.SFC<QualityIndicatorProps> = ({ quality }) => {
  const icon = getIconName(quality)
  return (
    <Image style={styles.image} source={icon} resizeMode="contain" />
  )
}

function getIconName (quality: number | null): ImageRequireSource {
  if (quality === null) {
    return require('../../../assets/quality/unknown.png')
  }
  if (quality >= HIGH_QUALITY) {
    return require('../../../assets/quality/high.png')
  }
  if (quality >= MEDIUM_QUALITY) {
    return require('../../../assets/quality/medium.png')
  }
  return require('../../../assets/quality/low.png')
}

const MEDIUM_QUALITY = 0.2
const HIGH_QUALITY = 0.5

const ICON_SIZE = 30
const styles = StyleSheet.create({
  image: {
    width: ICON_SIZE,
    height: ICON_SIZE
  }
})

export { QualityIndicator }
