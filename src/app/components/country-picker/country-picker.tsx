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
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import {
  Col,
  Grid,
  Icon,
  Text
} from 'native-base'
import * as React from 'react'

import { StyleSheet, TouchableOpacity, View } from 'react-native'
import colors from '../../../app/styles/colors'
import { Country } from './country'
import CountryFlag from './country-flag'
import CountryList from './country-list'
import CountryModal from './country-modal'

type PickerProps = {
  items: Country[],
  onSelect: (country: Country) => void,
  placeholder: string
}

type PickerState = {
  modalIsOpen: boolean,
  selectedCountry?: Country
}

class CountryPicker extends React.Component<PickerProps, PickerState> {
  constructor (props: PickerProps) {
    super(props)

    this.state = { modalIsOpen: false }
  }

  public render () {
    return (
      <View style={styles.container}>
        <CountryModal
          isOpen={this.state.modalIsOpen}
          onClose={() => this.closeCountryModal()}
        >
          <CountryList
            items={this.props.items}
            onClose={() => this.closeCountryModal()}
            onSelect={(country: Country) => this.onCountrySelect(country)}
          />
        </CountryModal>

        <View style={styles.countryPicker}>
          <Grid>
            <Col size={85}>
              <TouchableOpacity onPress={() => this.openCountryModal()}>
                <Grid>
                  <Col size={15} style={styles.countryFlagBox}>
                    <CountryFlag countryCode={this.countryCode}/>
                  </Col>

                  <Col size={90} style={styles.countryNameBox}>
                    <Text>{this.countryName}</Text>
                  </Col>

                  <Col size={10} style={styles.arrowBox}>
                    <Icon style={styles.arrowIcon} name="arrow-down"/>
                  </Col>
                </Grid>
              </TouchableOpacity>
            </Col>
            <Col size={15} style={styles.favoritesBox}>
              <Icon style={styles.favoritesIcon} name="md-star-outline"/>
            </Col>
          </Grid>
        </View>
      </View>
    )
  }

  private get countryCode () {
    if (!this.state.selectedCountry) {
      return
    }

    return this.state.selectedCountry.countryCode.toLowerCase()
  }

  private get countryName () {
    if (!this.state.selectedCountry) {
      return this.props.placeholder
    }

    return this.state.selectedCountry.name
  }

  private openCountryModal () {
    this.setState({ modalIsOpen: true })
  }

  private closeCountryModal () {
    this.setState({ modalIsOpen: false })
  }

  private onCountrySelect (country: Country) {
    this.props.onSelect(country)

    this.setState({
      selectedCountry: country,
      modalIsOpen: false
    })
  }
}

const styles = StyleSheet.create({
  container: {
    width: '100%',
    backgroundColor: '#fff'
  },
  countryPicker: {
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 4,
    borderWidth: 0.5,
    borderColor: colors.border
  },
  countryFlagBox: {
    justifyContent: 'center',
    alignItems: 'center',
    borderLeftWidth: 0.5,
    borderColor: colors.border,
    borderRightWidth: 0.5
  },
  countryNameBox: {
    justifyContent: 'center',
    alignItems: 'center',
    height: 40
  },
  arrowBox: {
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 5,
    width: '100%'
  },
  arrowIcon: {
    fontSize: 20,
    marginTop: 4,
    color: colors.primary
  },
  favoritesBox: {
    justifyContent: 'center',
    alignItems: 'center',
    borderLeftWidth: 0.5,
    borderColor: colors.border
  },
  favoritesIcon: {
    color: colors.primary
  }
})

export default CountryPicker
