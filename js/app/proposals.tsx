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

import React from 'react'
import { View, Picker, Button } from 'react-native'
import styles from './proposals-styles'
import { FavoriteProposalDTO, sortFavorites } from '../libraries/favoriteStorage'
import {ProposalDTO} from "mysterium-tequilapi";

interface ProposalsProps {
  proposals: ProposalDTO[],
  selectedProviderId: string | null,
  onProposalSelected (providerId: string): void
}

interface ProposalsState {
  proposals: ProposalDTO[] | null,
  favoriteProposals: FavoriteProposalDTO[] | null
}

export default class Proposals extends React.Component<ProposalsProps, ProposalsState> {
  constructor (props: ProposalsProps) {
    super(props)
    this.state = {
      proposals: null,
      favoriteProposals: null
    }
  }

  shouldComponentUpdate (nextProps: ProposalsProps, nextState: ProposalsState): boolean {
    if (JSON.stringify(nextProps.proposals) === JSON.stringify(nextState.proposals)) {
      return false
    }
    sortFavorites(nextProps.proposals).then(fp => {
      this.setState({ favoriteProposals: fp })
    })
    return true
  }

  async onFavoritePress (selectedProviderId: string): Promise<void> {
    let { favoriteProposals } = this.state
    if (!favoriteProposals) {
      return
    }

    const favoriteProposal: FavoriteProposalDTO = favoriteProposals
      .filter(p => p.id === selectedProviderId)[0]

    if (favoriteProposal) {
      await favoriteProposal.toggleFavorite()
      favoriteProposals = await sortFavorites(this.props.proposals)
      this.setState({favoriteProposals})
    }
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
