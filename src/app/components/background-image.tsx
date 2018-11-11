import { ImageBackground, StyleSheet } from 'react-native'
import React from 'react'

class BackgroundImage extends React.Component {
  public render () {
    return (
      <ImageBackground
        style={styles.backgroundImage}
        source={require('../../assets/background-logo.png')}
        resizeMode="contain"
      >
        {this.props.children}
      </ImageBackground>
    )
  }
}

const styles = StyleSheet.create({
  backgroundImage: {
    width: '100%',
    height: '100%'
  }
})

export default BackgroundImage
