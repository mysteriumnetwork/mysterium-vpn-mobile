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
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

import React, { ReactNode } from 'react'
import { View } from 'react-native'
import styles from '../app-styles'
import LogoBackground from '../components/logo-background'

type LoadingScreenProps = {
  load: () => Promise<void>
}

class LoadingScreen extends React.Component<LoadingScreenProps> {
  public async componentDidMount () {
    try {
      await this.props.load()
    } catch (err) {
      console.log('App loading failed', err)
    }
  }

  public render (): ReactNode {
    return (
      <View style={styles.screen}>
        <LogoBackground/>
      </View>
    )
  }
}

export default LoadingScreen
