/*
 * Copyright (C) 2018 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
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

import { Container } from 'native-base'
import React from 'react'
import { ScrollView, StyleSheet } from 'react-native'
import HTMLView from 'react-native-htmlview'
import TextButton from '../components/text-button'
import Terms from '../domain/terms'
import { TERMS_TEXT } from './terms-text'

type TermsScreenProps = {
  terms: Terms,
  close: () => void
}

class TermsScreen extends React.Component<TermsScreenProps> {
  public componentDidMount (): void {
    this.closeIfAccepted().catch(error => {
      console.error('Terms check failed', error)
    })
  }

  public render () {
    return (
      <Container style={styles.root}>
        <ScrollView style={styles.text}>
          <HTMLView stylesheet={htmlStyles} value={TERMS_TEXT} />
        </ScrollView>
        <TextButton onPress={() => this.acceptTerms()}>
          Accept
        </TextButton>
      </Container>
    )
  }

  private async closeIfAccepted () {
    const accepted = await this.props.terms.areAccepted()
    if (accepted) {
      this.props.close()
    }
  }

  private async acceptTerms () {
    console.log('Accepting terms')
    await this.props.terms.accept()
    this.props.close()
  }
}

const styles = StyleSheet.create({
  root: {
    alignItems: 'center',
    width: '100%',
    padding: 20
  },
  text: {
    marginBottom: 20
  }
})

const htmlStyles = StyleSheet.create({
  h3: {
    textAlign: 'center',
    fontWeight: '500',
    fontSize: 18
  }
})

export default TermsScreen
