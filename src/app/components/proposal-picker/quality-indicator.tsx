/*
 * Copyright (C) 2019 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
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
import { Image, ImageRequireSource, ImageStyle, StyleProp, StyleSheet } from 'react-native'
import { QualityLevel } from '../../../libraries/mysterium-vpn-js/models/quality-level'

type QualityIndicatorProps = {
  level: QualityLevel,
  style?: StyleProp<ImageStyle>
}

const QualityIndicator: React.SFC<QualityIndicatorProps> = ({ level, style }) => {
  const icon = getIconImage(level)
  return (
    <Image style={[styles.image, style]} source={icon} resizeMode="contain" />
  )
}

function getIconImage (level: number | null): ImageRequireSource {
  if (level === QualityLevel.HIGH) {
    return require('../../../assets/quality/high.png')
  }
  if (level === QualityLevel.MEDIUM) {
    return require('../../../assets/quality/medium.png')
  }
  if (level === QualityLevel.LOW) {
    return require('../../../assets/quality/low.png')
  }
  if (level !== QualityLevel.UNKNOWN) {
    throw Error(`Unknown quality level ${level}`)
  }
  return require('../../../assets/quality/unknown.png')
}

const ICON_SIZE = 30
const styles = StyleSheet.create({
  image: {
    width: ICON_SIZE,
    height: ICON_SIZE
  }
})

export { QualityIndicator }
