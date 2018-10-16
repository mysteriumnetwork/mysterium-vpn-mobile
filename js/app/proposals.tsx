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

import {action, computed} from 'mobx'
import { observer } from 'mobx-react/native'
import React, {ReactNode} from 'react'
import {Button, Picker, Text, View} from 'react-native'
import { ProposalsFetcher } from '../fetchers/proposals-fetcher'
import { Proposal } from '../libraries/favorite-proposal'
import styles from './proposals-styles'

type ProposalsProps = {
  proposalsFetcher: ProposalsFetcher,
  proposalsStore: {
    SelectedProviderId: string | null,
    Proposals: Proposal[] | null,
  },
}

@observer
export default class Proposals extends React.Component<ProposalsProps> {
  private static renderProposal(p: Proposal) {
    const label = (p.isFavorite ? '* ' : '') + p.name
    return <Picker.Item key={p.id} label={label} value={p.id} />
  }

  public render(): ReactNode {
    const proposals = this.props.proposalsStore.Proposals
    const selectedProviderId = this.props.proposalsStore.SelectedProviderId
    if (!proposals) {
      return <Text>Loading proposals...</Text>
    }
    return (
      <View style={{ flexDirection: 'row' }}>
        <Picker
          style={styles.picker}
          selectedValue={selectedProviderId}
          onValueChange={(providerId: string) => this.onProposalSelected(providerId)}
        >
          {proposals.map((p: Proposal) => Proposals.renderProposal(p))}
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

  @computed
  private get loadedProposals(): Proposal[] {
    return this.props.proposalsStore.Proposals || []
  }

  private async onFavoritePress(selectedProviderId: string): Promise<void> {
    const proposal = this.loadedProposals.find(
      (p: Proposal) => p.id === selectedProviderId,
    )

    if (proposal) {
      await proposal.toggleFavorite()

      // TODO: don't need to fetch proposals here, remove later
      await this.props.proposalsFetcher.refresh()
    }
  }

  @action
  private onProposalSelected(providerId: string) {
    this.props.proposalsStore.SelectedProviderId = providerId
  }
}
