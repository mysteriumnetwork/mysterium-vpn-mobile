import React from 'react'
import { StyleSheet, Text } from 'react-native'
import colors from '../styles/colors'
import fonts from '../styles/fonts'

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
