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

import { Col, Grid, Icon, Text } from 'native-base'
import React, { ReactNode } from 'react'
import { StyleSheet, TouchableOpacity, View } from 'react-native'
import colors from '../../../app/styles/colors'
import CountryFlag from './country-flag'
import ProposalList from './proposal-list'
import { ProposalListItem } from './proposal-list-item'
import ProposalModal from './proposal-modal'
import { ServiceIndicator } from './service-indicator'

type PickerProps = {
  proposals: ProposalListItem[]
  onSelect: (proposal: ProposalListItem) => void
  onFavoriteToggle: () => void
  isFavoriteSelected: boolean
  placeholder: string,
  selectedProposal: ProposalListItem | null,
  disabled: boolean,
  serviceFilterOptions: string[]
}

type PickerState = {
  modalIsOpen: boolean
  selectedProposal: ProposalListItem | null
}

class ProposalPicker extends React.Component<PickerProps, PickerState> {
  constructor (props: PickerProps) {
    super(props)

    this.state = {
      modalIsOpen: false,
      selectedProposal: this.props.selectedProposal
    }
  }

  public render (): ReactNode {
    const pickerButtonStyles = [styles.pickerButton]
    if (this.props.disabled) {
      pickerButtonStyles.push(styles.pickerButtonDisabled)
    }

    return (
      <View style={styles.container}>
        <ProposalModal
          isOpen={this.state.modalIsOpen}
          onClose={() => this.closeProposalModal()}
        >
          <ProposalList
            proposals={this.props.proposals}
            selectedProposal={this.state.selectedProposal}
            onClose={() => this.closeProposalModal()}
            onSelect={(proposal: ProposalListItem) => this.onProposalSelect(proposal)}
            serviceFilterOptions={this.props.serviceFilterOptions}
          />
        </ProposalModal>

        <View style={styles.proposalPicker}>
          <Grid>
            <Col size={85}>
              <TouchableOpacity
                style={pickerButtonStyles}
                onPress={() => this.openProposalModal()}
                disabled={this.props.disabled}
              >
                <Grid>
                  <Col size={15} style={styles.countryFlagBox}>
                    <CountryFlag countryCode={this.countryCode || ''} showPlaceholder={true}/>
                  </Col>

                  <Col size={15} style={styles.serviceIndicatorBox}>
                    {this.renderServiceIndicator()}
                  </Col>

                  <Col size={90} style={styles.countryNameBox}>
                    {this.renderProposalLabel()}
                  </Col>

                  <Col size={10} style={styles.arrowBox}>
                    <Icon style={styles.arrowIcon} name="arrow-down"/>
                  </Col>
                </Grid>
              </TouchableOpacity>
            </Col>
            <Col size={15} style={styles.favoritesBox}>
              <TouchableOpacity onPress={() => this.props.onFavoriteToggle()}>
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
    if (!this.state.selectedProposal) {
      return null
    }

    const code = this.state.selectedProposal.countryCode
    if (code === null) {
      return null
    }
    return code.toLowerCase()
  }

  private renderProposalLabel () {
    if (!this.state.selectedProposal) {
      return (
        <Text>{this.countryName}</Text>
      )
    }

    return (
      <View>
        <Text>{this.countryName}</Text>
        <Text style={styles.providerId}>{this.providerId}</Text>
      </View>
    )
  }

  private renderServiceIndicator () {
    if (!this.props.selectedProposal) {
      return
    }
    return <ServiceIndicator serviceType={this.props.selectedProposal.serviceType}/>
  }

  private get countryName (): string {
    if (!this.state.selectedProposal) {
      return this.props.placeholder
    }

    return this.state.selectedProposal.countryName || ''
  }

  private get providerId (): string {
    if (!this.state.selectedProposal) {
      return ''
    }

    return this.state.selectedProposal.providerID.substring(0, 25) + '...'
  }

  private openProposalModal () {
    this.setState({ modalIsOpen: true })
  }

  private closeProposalModal () {
    this.setState({ modalIsOpen: false })
  }

  private onProposalSelect (proposal: ProposalListItem) {
    this.props.onSelect(proposal)

    this.setState({
      selectedProposal: proposal,
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
  proposalPicker: {
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
  pickerButtonDisabled: {
    opacity: 0.5
  },
  countryFlagBox: {
    justifyContent: 'center',
    alignItems: 'center',
    borderLeftWidth: 0.5,
    borderColor: colors.border,
    borderRightWidth: 0.5,
    height: boxHeight
  },
  serviceIndicatorBox: {
    justifyContent: 'center',
    alignItems: 'center',
    height: boxHeight
  },
  countryNameBox: {
    justifyContent: 'center',
    alignItems: 'flex-start',
    height: boxHeight
  },
  providerId: {
    color: '#666666',
    fontSize: 12,
    marginBottom: 4
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

export default ProposalPicker
