/*
 * Copyright (C) 2017 The 'MysteriumNetwork/mysterion' Authors.
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
import {StyleSheet, Text, View} from 'react-native'
import {bytesDisplay, timeDisplay} from '../libraries/unitConverter'

interface StatsProps {
  duration: number
  bytesReceived: number
  bytesSent: number
}

const Stats: React.SFC<StatsProps> = ({ duration, bytesReceived, bytesSent }) => {
  return (
    <View style={styles.container}>
      <Text>Duration: {timeDisplay(duration)}</Text>
      <Text>Received: {bytesDisplay(bytesReceived)}</Text>
      <Text>Sent: {bytesDisplay(bytesSent)}</Text>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    padding: 20
  }
})

export default Stats
