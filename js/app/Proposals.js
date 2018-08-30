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
import { FavoriteProposalDTO, sortFavorites } from '../libraries/favoriteStorage'
import ProposalDTO from '../libraries/mysterium-tequilapi/dto/proposal'

export default class Proposals extends React.Component {
  constructor (props) {
    super(props)
    this.state = {
      favoriteProposals: null
    }
  }

  // static getDerivedStateFromProps (nextProps, prevState) {
  //   const favoriteProposals = sortFavorites(nextProps.proposals)
  //   return {
  //     ...prevState,
  //     favoriteProposals
  //   }
  // }

  async componentDidUpdate (prevProps, prevState) {
    const favoriteProposals = await sortFavorites(prevProps.proposals)
    this.setState({ favoriteProposals })
  }

  async onFavoritePress (selectedProviderId) {
    let { favoriteProposals } = this.state
    const favoriteProposal: FavoriteProposalDTO = favoriteProposals
      .filter(p => p.id === selectedProviderId)[0]

    await favoriteProposal.toggleFavorite()
    favoriteProposals = await sortFavorites(this.props.proposals)
    this.setState({ favoriteProposals })
  }

  render () {
    const { selectedProviderId, onProposalSelected } = this.props
    const { favoriteProposals } = this.state
    if (!favoriteProposals) {
      return null
    }
    return (
      <View style={{ flexDirection: 'row' }}>
        <Picker style={styles.picker} selectedValue={selectedProviderId} onValueChange={onProposalSelected}>
          {favoriteProposals.map(p => Proposals.renderProposal(p))}
        </Picker>
        {selectedProviderId ? <Button title={'*'} onPress={() => this.onFavoritePress(selectedProviderId)} /> : null}
      </View>
    )
  }

  static renderProposal (p: FavoriteProposalDTO) {
    const label = (p.isFavorite ? '* ' : '') + p.name
    return (
      <Picker.Item key={p.id} label={label} value={p.id} />
    )
  }
}

Proposals.propTypes = {
  proposals: PropTypes.arrayOf(PropTypes.instanceOf(ProposalDTO)),
  selectedProviderId: PropTypes.string,
  onProposalSelected: PropTypes.func
}
