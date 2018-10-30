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

import React, { ReactNode } from 'react'
import { StyleProp, StyleSheet, Text, View, ViewStyle } from 'react-native'
import { bytesDisplay, timeDisplay } from '../libraries/unitConverter'
import { STYLES } from '../styles'

type StatsProps = {
  style?: StyleProp<ViewStyle>
  duration: number,
  bytesReceived: number,
  bytesSent: number
}

const Stats: React.SFC<StatsProps> = ({
  style,
  duration,
  bytesReceived,
  bytesSent
}) => {
  return (
    <View style={[styles.container, style]}>
      {createStatsBlock('Duration', timeDisplay(duration), 'H:M:S')}
      {createStatsBlock('Received', bytesDisplay(bytesReceived))}
      {createStatsBlock('Sent', bytesDisplay(bytesSent))}
    </View>
  )
}

function createStatsBlock (name: string, value: string, units?: string): ReactNode {
  const textName = name.toUpperCase()
  const parts = value.split(' ')
  const textAmount = parts[0]
  const textUnits = parts[1] || units || ''
  return <StatsBlock textName={textName} textAmount={textAmount} textUnits={textUnits}/>
}

type StatsBlockProps = {
  textName: string,
  textAmount: string,
  textUnits: string
}

const StatsBlock: React.SFC<StatsBlockProps> = ({
  textName, textAmount, textUnits
}) => {
  return (
    <View style={styles.textBlock}>
      <Text style={styles.textName}>
        {textName}
      </Text>
      <Text style={styles.textAmount}>
        {textAmount}
      </Text>
      <Text style={styles.textUnits}>
        {textUnits}
      </Text>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: 'row',
    padding: STYLES.PADDING
  },
  textBlock: {
    width: '33.3%',
    alignItems: 'center'
  },
  textName: {
    fontSize: STYLES.FONT_NORMAL,
    color: STYLES.COLOR_SECONDARY
  },
  textAmount: {
    fontSize: 22,
    color: STYLES.COLOR_MAIN
  },
  textUnits: {
    fontSize: STYLES.FONT_NORMAL
  }
})

export default Stats
