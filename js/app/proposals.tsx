/*
 * Copyright (C) 2018 The 'MysteriumNetwork/mysterion' Authors.
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

import { action } from 'mobx'
import { observer } from 'mobx-react/native'
import React from 'react'
import { Button, Picker, View } from 'react-native'
import { ProposalsFetcher } from '../fetchers/proposals-fetcher'
import { FavoriteProposalDTO } from '../libraries/favorite-proposal'
import { IProposalsStore } from '../store/tequilapi-store'
import styles from './proposals-styles'

interface IProposalsProps {
  proposalsFetcher: ProposalsFetcher
  proposalsStore: IProposalsStore
}

@observer
export default class Proposals extends React.Component<IProposalsProps> {
  private static renderProposal(p: FavoriteProposalDTO) {
    const label = (p.isFavorite ? '* ' : '') + p.name
    return <Picker.Item key={p.id} label={label} value={p.id} />
  }

  public render() {
    const favoriteProposals = this.props.proposalsStore.FavoriteProposals
    const selectedProviderId = this.props.proposalsStore.SelectedProviderId
    if (!favoriteProposals) {
      return null
    }
    return (
      <View style={{ flexDirection: 'row' }}>
        <Picker
          style={styles.picker}
          selectedValue={selectedProviderId}
          onValueChange={(providerId: string) => this.onProposalSelected(providerId)}
        >
          {favoriteProposals.map((p: FavoriteProposalDTO) => Proposals.renderProposal(p))}
        </Picker>
        {selectedProviderId ? (
          <Button
            title={'*'}
            onPress={() => this.onFavoritePress(selectedProviderId)}
          />
        ) : null}
      </View>
    )
  }

  private async onFavoritePress(selectedProviderId: string): Promise<void> {
    const favoriteProposals = this.props.proposalsStore.FavoriteProposals
    if (!favoriteProposals) {
      return
    }

    const favoriteProposal = favoriteProposals.filter(
      (p: FavoriteProposalDTO) => p.id === selectedProviderId,
    )[0]

    if (favoriteProposal) {
      await favoriteProposal.toggleFavorite()
      await this.props.proposalsFetcher.refresh()
    }
  }

  @action
  private onProposalSelected(providerId: string) {
    console.log('selected', providerId)
    this.props.proposalsStore.SelectedProviderId = providerId
  }
}
