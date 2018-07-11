/*
 * Copyright (C) 2017 The 'MysteriumNetwork/mysterion' Authors.
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

// @flow

import React from 'react'
import { Picker } from 'react-native'
import styles from './Proposals-styles'
import CONFIG from '../config'
import Countries from '../libraries/countries'
import ProposalDTO from '../libraries/mysterium-tequilapi/dto/proposal'
import PropTypes from 'prop-types'

export default class Proposals extends React.Component {
  static renderProposal (p: ProposalDTO) {
    if (!p.serviceDefinition) {
      return null
    }
    const countryCode = p.serviceDefinition.locationOriginate.country.toLocaleLowerCase()
    const countryName = Countries[countryCode] || CONFIG.TEXTS.UNKNOWN
    const providerId = p.providerId
    return (
      <Picker.Item key={p.id} label={countryName} value={providerId} />
    )
  }

  render () {
    const { proposals, selectedProviderId, onProposalSelected } = this.props
    return (
      <Picker style={styles.picker} selectedValue={selectedProviderId} onValueChange={onProposalSelected}>
        {proposals.map(p => Proposals.renderProposal(p))}
      </Picker>
    )
  }
}

Proposals.propTypes = {
  proposals: PropTypes.arrayOf(PropTypes.instanceOf(ProposalDTO)),
  selectedProviderId: PropTypes.string,
  onProposalSelected: PropTypes.func
}
