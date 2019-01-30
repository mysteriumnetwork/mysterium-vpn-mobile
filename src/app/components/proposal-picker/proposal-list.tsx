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
import {
  Body,
  Button,
  Container,
  Content,
  Header,
  Icon,
  Input,
  Left,
  List,
  ListItem,
  Right,
  Segment,
  Text
} from 'native-base'
import React, { ReactNode } from 'react'
import { Platform, StyleSheet, View } from 'react-native'
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
          <Right>
            <Button transparent={true} onPress={() => this.props.onClose()}>
              <Text>Close</Text>
            </Button>
          </Right>
        </Header>
        {this.renderServiceFilterOptions()}
        <Content>
          <List>
            {filteredProposals.map((proposal: ProposalItem) => this.renderProposal(proposal))}
          </List>
        </Content>
      </Container>
    )
  }

  private renderServiceFilterOptions (): ReactNode {
    const options = this.store.serviceFilterOptions
    const items = options.map((serviceType, index) => {
      return this.renderServiceFilterOption(serviceType, index, options.length)
    })
    return (
      <Segment>
        {items}
      </Segment>
    )
  }

  private renderServiceFilterOption (serviceType: ServiceType | null, index: number, total: number): ReactNode {
    const count = this.store.proposalsCountByServiceType(serviceType)
    const label = (serviceType || this.SERVICE_TYPE_ALL_LABEL) + ` (${count})`
    return (
      <Button
        key={label}
        first={index === 0}
        last={index + 1 === total}
        active={serviceType === this.store.serviceTypeFilter}
        onPress={() => this.onFilterOptionPressed(serviceType)}
      >
        <Text>{label}</Text>
      </Button>
    )
  }

  private renderProposal (proposal: ProposalItem): ReactNode {
    return (
      <ListItem
        style={this.listItemStyle(proposal)}
        icon={true}
        key={proposal.id}
        onPress={() => this.props.onSelect(proposal)}
      >
        <Left style={styles.flagImage}>
          <CountryFlag countryCode={proposal.countryCode} showPlaceholder={true}/>
        </Left>
        <Body>
          <Text style={this.listItemTextStyle(proposal)}>{proposal.countryName}</Text>
          <Text style={this.providerIdStyle(proposal)}>
            {proposal.providerID.substring(0, 25) + '...'}
          </Text>
        </Body>
        <Right>
          <ServiceIndicator
            serviceType={proposal.serviceType}
            style={styles.serviceIndicator}
          />
          <QualityIndicator quality={proposal.quality}/>
          <Icon
            name={proposal.isFavorite ? 'md-star' : 'md-star-outline'}
          />
        </Right>
      </ListItem>
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

let listItemStyles = {}
if (Platform.OS !== 'ios') {
  listItemStyles = {
    paddingLeft: 15,
    marginLeft: 0
  }
}

const styles: any = StyleSheet.create({
  listItem: listItemStyles,
  selectedListItem: {
    backgroundColor: colors.primary
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
    width: '70%',
    borderColor: 'transparent'
  },
  flagImage: {
    width: 26,
    height: 26,
    margin: 0,
    padding: 0
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
  serviceIndicator: {
    marginRight: 10
  }
})

export default ProposalList
