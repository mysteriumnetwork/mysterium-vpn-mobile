import React, { SFC } from 'react'
import { StyleSheet, Text, TouchableOpacity } from 'react-native'
import { STYLES } from '../../styles'

type ButtonFavoriteProps = {
  isFavorite: boolean,
  onPress: () => void
}

export const ButtonFavorite: SFC<ButtonFavoriteProps> = ({
  isFavorite, onPress
}) => (
  <TouchableOpacity
    style={styles.root}
    onPress={onPress}
  >
    <Text style={styles.buttonContent}>
      {isFavorite ? '★' : '☆'}
    </Text>
  </TouchableOpacity>
)

const styles = StyleSheet.create({
  root: {
    alignItems: 'center',
    justifyContent: 'center',
    width: 50,
    height: 40
  },
  buttonContent: {
    fontSize: 45,
    color: STYLES.COLOR_MAIN
  }
})

export default ButtonFavorite
