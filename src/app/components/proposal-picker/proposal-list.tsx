/*
 * Copyright (C) 2019 The 'mysteriumnetwork/mysterium-vpn-mobile' Authors.
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

import { observer } from 'mobx-react/native'
import { Body, Button as NativeButton, Container, Header, Icon, Input, NativeBase, Right, Text } from 'native-base'
import React, { ReactElement, ReactNode } from 'react'
import { Button, FlatList, Picker, Platform, StyleSheet, TouchableOpacity, View } from 'react-native'
import colors from '../../../app/styles/colors'
import { ProposalItem } from '../../models/proposal-item'
import { ServiceType } from '../../models/service-type'
import { ProposalsListStore } from '../../stores/proposals-list-store'
import translations from '../../translations'
import CountryFlag from './country-flag'
import { QualityIndicator } from './quality-indicator'
import { ServiceIndicator } from './service-indicator'

type ListProps = {
  proposals: ProposalItem[],
  selectedProposal: ProposalItem | null,
  onClose: () => void,
  onSelect: (proposal: ProposalItem) => void
}

@observer
class ProposalList extends React.Component<ListProps> {
  private store: ProposalsListStore = new ProposalsListStore(this.props.proposals)

  private readonly SERVICE_TYPE_ALL_LABEL = 'all'
  private readonly SORTING_VALUE = 'country'
  private readonly SORTING_LABEL = 'Country'

  constructor (props: ListProps) {
    super(props)
  }

  public render (): ReactNode {
    const filteredProposals = this.store.filteredProposals

    return (
      <Container>
        <Header hasSegment={true}>
          <Body style={styles.headerItem}>
            <View style={styles.searchBar}>
              <Icon name="ios-search" style={styles.searchIcon}/>
              <Input
                placeholderTextColor={platformStyles.search.inputColor}
                placeholder={translations.PROPOSAL_SEARCH}
                onChange={(event) => this.onSearchValueChange(event.nativeEvent.text)}
                style={styles.searchInput}
              />
            </View>
          </Body>
          <Right style={styles.rightItem}>
            <NativeButton transparent={true} onPress={() => this.props.onClose()}>
              <Text>Close</Text>
            </NativeButton>
          </Right>
        </Header>
        <View style={styles.content}>
          <View style={styles.toolbar}>
            <Picker selectedValue={this.SORTING_VALUE} style={styles.sortingPicker}>
              <Picker.Item label={this.SORTING_LABEL} value={this.SORTING_VALUE}/>
            </Picker>
            {this.renderServiceFilterOptions()}
          </View>
          <FlatList
            keyExtractor={(item) => item.id}
            data={filteredProposals}
            renderItem={({ item }) => this.renderProposal(item)}
          />
        </View>
      </Container>
    )
  }

  private renderServiceFilterOptions (): ReactNode[] {
    const options = this.store.serviceFilterOptions.map((serviceType) => {
      return this.renderServiceFilterOption(serviceType)
    })
    return options
  }

  private renderServiceFilterOption (serviceType: ServiceType | null): ReactNode {
    const count = this.store.proposalsCountByServiceType(serviceType)
    const label = (serviceType || this.SERVICE_TYPE_ALL_LABEL) + ` (${count})`
    const active = serviceType === this.store.serviceTypeFilter

    return (
      <Button
        key={label}
        color={active ? colors.primary : colors.secondary}
        onPress={() => this.onFilterOptionPressed(serviceType)}
        title={label}
      />
    )
  }

  private renderProposal (proposal: ProposalItem): ReactElement<NativeBase.ListItem> {
    return (
      <TouchableOpacity
        style={this.listItemStyle(proposal)}
        onPress={() => this.props.onSelect(proposal)}
      >
          <CountryFlag
            countryCode={proposal.countryCode}
            showPlaceholder={true}
            style={styles.proposalItemElement}
          />
          <View style={[styles.proposalItemElement, styles.proposalLabelContainer]}>
              <Text style={this.listItemTextStyle(proposal)}>{proposal.countryName}</Text>
              <Text style={this.providerIdStyle(proposal)}>
                {proposal.providerID.substring(0, 25) + '...'}
              </Text>
          </View>
          <ServiceIndicator
              serviceType={proposal.serviceType}
              style={styles.proposalItemElement}
          />
          <QualityIndicator
            quality={proposal.quality}
            style={styles.proposalItemElement}
          />
          <Icon
              name={proposal.isFavorite ? 'md-star' : 'md-star-outline'}
              style={[styles.proposalItemElement, styles.favoritesIcon]}
          />
      </TouchableOpacity>
    )
  }

  private listItemStyle (proposal: ProposalItem) {
    const style = [styles.listItem]

    if (this.isProposalSelected(proposal)) {
      style.push(styles.selectedListItem)
    }

    return style
  }

  private listItemTextStyle (proposal: ProposalItem) {
    return this.isProposalSelected(proposal) ? [styles.selectedListItemText] : []
  }

  private providerIdStyle (proposal: ProposalItem) {
    return this.isProposalSelected(proposal)
      ? [styles.providerIdText, styles.selectedListItemText]
      : [styles.providerIdText]
  }

  private isProposalSelected (proposal: ProposalItem) {
    const selected = this.props.selectedProposal
    if (selected === null) {
      return false
    }

    return selected.id === proposal.id
  }

  private onFilterOptionPressed (serviceType: ServiceType | null) {
    this.store.filterByServiceType(serviceType)
  }

  private onSearchValueChange (text: string) {
    this.store.filterByText(text)
  }
}

const iosSearchColor = '#222'
const androidSearchColor = '#fff'

const platformStyles = {
  search: {
    iconColor: Platform.OS === 'ios' ? iosSearchColor : androidSearchColor,
    inputColor: Platform.OS === 'ios' ? iosSearchColor : androidSearchColor
  }
}

const styles: any = StyleSheet.create({
  listItem: {
    flexDirection: 'row',
    alignItems: 'center',
    borderBottomWidth: 1,
    borderBottomColor: colors.border
  },
  selectedListItem: {
    backgroundColor: colors.primary
  },
  content: {
    flex: 1
  },
  sortingPicker: {
    flex: 1
  },
  toolbar: {
    flexDirection: 'row',
    alignItems: 'center',
    flexWrap: 'wrap'
  },
  serviceFilterButtonActive: {
    color: 'red'
  },
  providerIdText: {
    fontSize: 12,
    color: '#666666',
    marginBottom: 4
  },
  selectedListItemText: {
    color: '#fff'
  },
  headerItem: {
    borderWidth: 0,
    width: '100%',
    borderColor: 'transparent'
  },
  rightItem: {
    flex: 0,
    borderColor: 'transparent'
  },
  searchBar: {
    flexDirection: 'row',
    alignItems: 'center'
  },
  searchIcon: {
    color: platformStyles.search.iconColor
  },
  searchInput: {
    color: platformStyles.search.inputColor
  },
  proposalItemElement: {
    marginHorizontal: 5
  },
  proposalLabelContainer: {
    flex: 1
  },
  favoritesIcon: {
    color: colors.secondary,
    fontSize: 26
  }
})

export default ProposalList
