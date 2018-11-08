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

import { Col, Grid } from 'native-base'
import React, { ReactNode } from 'react'
import { StyleSheet, Text, View } from 'react-native'
import { bytesDisplay, timeDisplay } from '../../libraries/unitConverter'
import colors from '../styles/colors'
import fonts from '../styles/fonts'

type StatsProps = {
  duration: number,
  bytesReceived: number,
  bytesSent: number
}

const Stats: React.SFC<StatsProps> = ({ duration, bytesReceived, bytesSent }) => {
  return (
    <Grid>
      <Col>
        {createStatsBlock('Duration', timeDisplay(duration || 0), 'H:M:S')}
      </Col>
      <Col>
        {createStatsBlock('Received', bytesDisplay(bytesReceived || 0))}
      </Col>
      <Col>
        {createStatsBlock('Sent', bytesDisplay(bytesSent || 0))}
      </Col>
    </Grid>
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

const StatsBlock: React.SFC<StatsBlockProps> = ({ textName, textAmount, textUnits }) => {
  return (
    <View>
      <Text style={[styles.textName, styles.centerText]}>
        {textName}
      </Text>
      <Text style={[styles.textAmount, styles.centerText]}>
        {textAmount}
      </Text>
      <Text style={[styles.textUnits, styles.centerText]}>
        {textUnits}
      </Text>
    </View>
  )
}

const styles = StyleSheet.create({
  centerText: {
    justifyContent: 'center',
    textAlign: 'center'
  },
  textName: {
    fontSize: fonts.size.small,
    color: colors.secondary
  },
  textAmount: {
    fontSize: fonts.size.medium,
    color: colors.primary
  },
  textUnits: {
    fontSize: fonts.size.small
  }
})

export default Stats
