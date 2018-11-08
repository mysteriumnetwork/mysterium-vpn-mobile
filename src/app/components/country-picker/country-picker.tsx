import * as React from 'react'
import {
  Text,
  Icon,
  Grid,
  Col
} from 'native-base'

import { Image, StyleSheet, Modal, View, TouchableOpacity } from 'react-native'
import CountryList from './country-list'
import { getCountryImageFile } from './image-handler'
import colors from '../../../app/styles/colors'

const EMPTY_COUNTRY_TEXT = 'Select country'

export type CountryListItem = {
  id: string
  name: string,
  countryCode: string
}

export interface Props {
  items: CountryListItem[],
  onSelect: (country: CountryListItem) => void,
  placeholder: string
}

export interface State {
  modalIsOpen: boolean,
  selectedCountry?: CountryListItem
}

const styles: any = StyleSheet.create({
  container: {
    width: '100%',
    backgroundColor: '#fff'
  },
  logo: {
    justifyContent: 'center',
    alignItems: 'center',
    flex: 1
  },
  countryPicker: {
    justifyContent: 'center',
    alignItems: 'center',
    borderRadius: 4,
    borderWidth: 0.5,
    borderColor: colors.border
  },
  globeIcon: {
    color: colors.primary
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
  },
  countryFlagImage: {
    width: '50%',
    height: '50%'
  },
  modal: {
    width: '100%',
    height: '100%'
  }
})

class CountryPicker extends React.Component<Props, State> {
  constructor (props: Props) {
    super(props)

    this.state = { modalIsOpen: false }
  }

  public render () {
    return (
      <View style={styles.container}>
        {this.renderModalBox()}

        <View style={styles.countryPicker}>
          <Grid>
            <Col size={15} style={styles.countryFlagBox}>
              {this.renderSelectedCountryImage()}
            </Col>
            <Col size={70}>
              <Grid>
                <Col size={90} style={styles.countryNameBox}>
                  <TouchableOpacity onPress={() => this.openCountryModal()}>
                    <Text>{this.getSelectedCountryName()}</Text>
                  </TouchableOpacity>
                </Col>

                <Col size={10} style={styles.arrowBox}>
                  <Icon style={styles.arrowIcon} name="arrow-down"/>
                </Col>
              </Grid>
            </Col>
            <Col size={15} style={styles.favoritesBox}>
              <Icon style={styles.favoritesIcon} name="md-star-outline"/>
            </Col>
          </Grid>
        </View>
      </View>
    )
  }

  private renderModalBox () {
    return (
      <Modal
        animationType="slide"
        transparent={true}
        visible={this.state.modalIsOpen || false}
        onRequestClose={() => {
          this.closeCountryModal()
        }}
      >
        <View style={styles.modal}>
          <CountryList
            items={this.props.items}
            onClose={() => this.closeCountryModal()}
            onSelect={(country: CountryListItem) => this.setSelectedCountry(country)}
          />
        </View>
      </Modal>
    )
  }

  private renderSelectedCountryImage () {
    if (!this.state.selectedCountry) {
      return (
        <Icon style={styles.globeIcon} name={'ios-globe'}/>
      )
    }

    const countryCode = this.state.selectedCountry.countryCode.toLowerCase()

    return (
      <Image
        source={{ uri: getCountryImageFile(countryCode) }}
        style={styles.countryFlagImage}
      />
    )
  }

  private openCountryModal () {
    this.setState({ modalIsOpen: true })
  }

  private closeCountryModal () {
    this.setState({ modalIsOpen: false })
  }

  private setSelectedCountry (country: CountryListItem) {
    this.props.onSelect(country)

    this.setState({ selectedCountry: country })
  }

  private getSelectedCountryName () {
    if (!this.state.selectedCountry) {
      return this.props.placeholder || EMPTY_COUNTRY_TEXT
    }

    return this.state.selectedCountry.name
  }
}

export default CountryPicker
