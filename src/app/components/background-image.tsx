import { ImageBackground, StyleSheet } from 'react-native'
import React from 'react'

const BackgroundImage: React.SFC = ({ children }) => {
  return (
    <ImageBackground
      style={styles.backgroundImage}
      source={require('../../assets/background-logo.png')}
      resizeMode="contain"
    >
      {children}
    </ImageBackground>
  )
}

const styles = StyleSheet.create({
  backgroundImage: {
    width: '100%',
    height: '100%'
  }
})

export default BackgroundImage
