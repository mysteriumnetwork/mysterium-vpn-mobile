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
import { View, Picker, Button } from 'react-native'
import styles from './Proposals-styles'
import PropTypes from 'prop-types'
import { FavoriteProposalDTO } from '../libraries/favoriteStorage'

export default class Proposals extends React.Component {
  static renderProposal (p: FavoriteProposalDTO) {
    const label = (p.isFavorite ? '* ' : '') + p.name
    return (
      <Picker.Item key={p.id} label={label} value={p} />
    )
  }

  constructor (props) {
    super(props)
    this.state = {
      selectedProposal: null
    }
  }

  onFavoritePress () {
    this.selectedProposal.isFavorite = !this.selectedProposal.isFavorite
  }

  onProposalChanged (value, index) {
    // this.setState({ selectedProposal: value })
    this.props.onProposalSelected(value.id, index)
  }

  render () {
    const { proposals, selectedProviderId } = this.props
    return (
      <View>
        <Picker style={styles.picker} selectedValue={selectedProviderId} onValueChange={this.onProposalChanged.bind(this)}>
          {proposals.map(p => Proposals.renderProposal(p))}
        </Picker>
        {this.state.selectedProposal ? <Button title={'*'} onKeyPress={() => this.onFavoritePress()} /> : null}
      </View>
    )
  }
}

Proposals.propTypes = {
  proposals: PropTypes.arrayOf(PropTypes.instanceOf(FavoriteProposalDTO)),
  selectedProviderId: PropTypes.string,
  onProposalSelected: PropTypes.func
}
