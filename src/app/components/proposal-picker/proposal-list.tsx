/*
 * Copyright (C) 2019 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
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
import { QualityCalculator } from 'mysterium-vpn-js'
import { Body, Button as NativeButton, Container, Header, Icon, Input, NativeBase, Right, Text } from 'native-base'
import React, { ReactElement, ReactNode } from 'react'
import { FlatList, Picker, Platform, StyleSheet, TouchableOpacity, View } from 'react-native'
import colors from '../../../app/styles/colors'
import { STYLES } from '../../../styles'
import { ProposalItem } from '../../models/proposal-item'
import { ServiceType } from '../../models/service-type'
import { ProposalListStore, ProposalsSorting } from '../../stores/proposal-list-store'
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
  private store: ProposalListStore = new ProposalListStore(this.props.proposals)

  private readonly SERVICE_TYPE_ALL_LABEL = 'all'

  private readonly SORTING_OPTIONS: Array<{sorting: ProposalsSorting, label: string}> = [
    { sorting: ProposalsSorting.ByCountryName, label: 'Sort: country' },
    { sorting: ProposalsSorting.ByQuality, label: 'Sort: quality' }
  ]

  constructor (props: ListProps) {
    super(props)
  }

  public render (): ReactNode {
    const proposals = this.store.currentProposals

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
            <Picker
              mode={'dropdown'}
              selectedValue={this.store.sorting}
              style={styles.sortingPicker}
              onValueChange={value => this.store.sorting = value}
            >
              {this.SORTING_OPTIONS.map(option => {
                return (<Picker.Item value={option.sorting} label={option.label} key={option.sorting}/>)
              })}
            </Picker>
            {this.renderServiceFilterOptions()}
          </View>
          <FlatList
            keyExtractor={(item) => item.id}
            data={proposals}
            renderItem={({ item }) => this.renderProposal(item)}
            ListEmptyComponent={() => this.renderEmptyListComponent()}
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

    const buttonStyle = active ? [styles.buttonStyle, styles.buttonStyleActive] : [styles.buttonStyle]
    const buttonTextStyle = active ? [styles.buttonTextStyle, styles.buttonTextStyleActive] : [styles.buttonTextStyle]
    return (
      <TouchableOpacity
        key={label}
        onPress={() => this.onFilterOptionPressed(serviceType)}
        style={buttonStyle}
      >
        <Text style={buttonTextStyle}>{label.toUpperCase()}</Text>
      </TouchableOpacity>
    )
  }

  private renderProposal (proposal: ProposalItem): ReactElement<NativeBase.ListItem> {
    const qualityLevel = new QualityCalculator().calculateLevel(proposal.quality)
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
            level={qualityLevel}
            style={styles.proposalItemElement}
          />
          <Icon
              name={proposal.isFavorite ? 'md-star' : 'md-star-outline'}
              style={[styles.proposalItemElement, styles.favoritesIcon]}
          />
      </TouchableOpacity>
    )
  }

  private renderEmptyListComponent (): ReactElement<any> {
    return (<Text>{translations.EMPTY_PROPOSAL_LIST}</Text>)
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
    this.store.serviceTypeFilter = serviceType
  }

  private onSearchValueChange (text: string) {
    this.store.textFilter = text
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
    flex: 1,
    height: '100%'
  },
  toolbar: {
    flexDirection: 'row',
    alignItems: 'center',
    flexWrap: 'wrap',
    borderBottomColor: colors.primary,
    borderBottomWidth: 1,
    height: 35
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
  },
  buttonStyle: {
    borderColor: STYLES.COLOR_MAIN,
    padding: 3,
    borderWidth: 1,
    borderRightWidth: 0,
    borderBottomWidth: 0,
    height: '100%',
    justifyContent: 'center'
  },
  buttonStyleActive: {
    backgroundColor: STYLES.COLOR_MAIN
  },
  buttonTextStyle: {
    color: STYLES.COLOR_MAIN,
    fontSize: 12
  },
  buttonTextStyleActive: {
    color: STYLES.COLOR_BACKGROUND
  }
})

export default ProposalList
