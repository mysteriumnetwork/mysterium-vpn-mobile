import { Dimensions, StyleSheet } from 'react-native'
import colors from '../../styles/colors'
import fonts from '../../styles/fonts'

const height = Dimensions.get('window').height

const styles = StyleSheet.create({
  backgroundImage: {
    width: '100%',
    height: '100%'
  },
  textCentered: {
    justifyContent: 'center'
  },
  ipText: {
    marginTop: 15,
    fontSize: fonts.size.small,
    color: colors.secondary
  },
  connectButton: {
    marginTop: 20
  },
  statsContainer: {
    marginTop: 30,
    padding: 15,
    borderTopWidth: 0.5,
    borderColor: colors.border
  },
  connectionContainer: {
    marginTop: height - 380
  },
  countryPicker: {
    paddingLeft: 10,
    paddingRight: 10
  }
})

export default styles
