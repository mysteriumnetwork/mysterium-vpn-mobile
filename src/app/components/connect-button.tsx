import * as React from 'react'
import {
  Button,
  Icon,
  Spinner
} from 'native-base'

import { StyleSheet, View } from 'react-native'
import colors from '../styles/colors'

export interface Props {
  onClick: () => void,
  disabled: boolean,
  active: boolean,
}

const styles: any = StyleSheet.create({
  container: {
    height: 60,
    width: 60
  },
  button: {
    borderColor: colors.border,
    borderRadius: 100,
    width: 60,
    height: 60,
    justifyContent: 'center',
    borderWidth: 0
  },
  buttonDisabled: {
    borderWidth: 0
  },
  powerIcon: {
    fontSize: 30,
    fontWeight: '800'
  },
  powerIconActive: {
    color: '#266e2e'
  },
  spinner: {
    marginTop: -5
  }
})

class ConnectButton extends React.Component<Props, any> {
  public render () {
    if (this.props.disabled) {
      return (
        <View style={styles.container}>
          <Spinner color="red" style={styles.spinner}/>
        </View>
      )
    }

    return (
      <View style={styles.container}>
        <Button
          bordered={true}
          onPress={() => this.props.onClick()}
          style={[styles.button, this.props.disabled ? styles.buttonDisabled : null]}
        >
          <Icon style={[styles.powerIcon, this.props.active ? styles.powerIconActive : null]} name="ios-power"/>
        </Button>
      </View>
    )
  }
}

export default ConnectButton
