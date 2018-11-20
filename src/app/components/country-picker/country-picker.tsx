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
import React, { ReactNode } from 'react'
import { StyleSheet, TouchableOpacity, View } from 'react-native'
import colors from '../../../app/styles/colors'
import { ICountry } from './country'
import CountryFlag from './country-flag'
import CountryList from './country-list'
import CountryModal from './country-modal'

type PickerProps = {
  countries: ICountry[]
  onSelect: (country: ICountry) => void
  onFavoriteSelect: () => void
  isFavoriteSelected: boolean
  placeholder: string
}

type PickerState = {
  modalIsOpen: boolean
  selectedCountry: ICountry | null
}

class CountryPicker extends React.Component<PickerProps, PickerState> {
  constructor (props: PickerProps) {
    super(props)

    this.state = {
      modalIsOpen: false,
      selectedCountry: null
    }
  }

  public render (): ReactNode {
    return (
      <View style={styles.container}>
        <CountryModal
          isOpen={this.state.modalIsOpen}
          onClose={() => this.closeCountryModal()}
        >
          <CountryList
            countries={this.props.countries}
            onClose={() => this.closeCountryModal()}
            onSelect={(country: ICountry) => this.onCountrySelect(country)}
          />
        </CountryModal>

        <View style={styles.countryPicker}>
          <Grid>
            <Col size={85}>
              <TouchableOpacity style={styles.pickerButton} onPress={() => this.openCountryModal()}>
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
              <TouchableOpacity onPress={() => this.props.onFavoriteSelect()}>
                <Icon
                  style={styles.favoritesIcon}
                  name={this.props.isFavoriteSelected ? 'md-star' : 'md-star-outline'}
                />
              </TouchableOpacity>
            </Col>
          </Grid>
        </View>
      </View>
    )
  }

  private get countryCode (): string | null {
    if (!this.state.selectedCountry) {
      return null
    }

    return this.state.selectedCountry.countryCode.toLowerCase()
  }

  private get countryName (): string {
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

  private onCountrySelect (country: ICountry) {
    this.props.onSelect(country)

    this.setState({
      selectedCountry: country,
      modalIsOpen: false
    })
  }
}

const boxHeight = 40
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
    borderColor: colors.border,
    height: boxHeight
  },
  pickerButton: {
    height: boxHeight
  },
  countryFlagBox: {
    justifyContent: 'center',
    alignItems: 'center',
    borderLeftWidth: 0.5,
    borderColor: colors.border,
    borderRightWidth: 0.5,
    height: boxHeight
  },
  countryNameBox: {
    justifyContent: 'center',
    alignItems: 'center',
    height: boxHeight
  },
  arrowBox: {
    justifyContent: 'center',
    alignItems: 'center',
    marginRight: 5,
    width: '100%',
    height: boxHeight
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
