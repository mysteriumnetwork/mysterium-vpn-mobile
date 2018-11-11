import { StyleSheet, Text } from 'react-native'
import fonts from '../styles/fonts'
import colors from '../styles/colors'
import React from 'react'

type IPProps = {
  ipAddress?: string
}

const IPAddress: React.SFC<IPProps> = ({ ipAddress }) => {
  return (
    <Text style={styles.ipText}>
      IP: {ipAddress}
    </Text>
  )
}

const styles = StyleSheet.create({
  ipText: {
    marginTop: 15,
    fontSize: fonts.size.small,
    color: colors.secondary
  }
})

export default IPAddress
