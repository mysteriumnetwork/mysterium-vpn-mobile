import {
  Body,
  Button,
  Container,
  Content,
  Header,
  Icon,
  Input,
  Item,
  Left,
  List,
  ListItem,
  Right,
  Text
} from 'native-base'
import React, { ReactNode } from 'react'
import { Platform, StyleSheet } from 'react-native'
import colors from '../../../app/styles/colors'
import ProposalFilter from '../../proposals/proposal-filter'
import translations from '../../translations'
import CountryFlag from './country-flag'
import { IProposal } from './proposal'

type ListProps = {
  proposals: IProposal[],
  selectedProposal: IProposal | null,
  onClose: () => void,
  onSelect: (proposal: IProposal) => void
}

type ListState = {
  filteredProposals: IProposal[]
}

class ProposalList extends React.Component<ListProps, ListState> {
  private proposalFilter: ProposalFilter

  constructor (props: ListProps) {
    super(props)

    this.state = { filteredProposals: this.props.proposals }
    this.proposalFilter = new ProposalFilter(this.props.proposals)
  }

  public render (): ReactNode {
    return (
      <Container>
        <Header>
          <Item style={styles.headerItem}>
            <Icon name="ios-search" style={styles.searchIcon}/>
            <Input
              placeholderTextColor={platformStyles.search.inputColor}
              placeholder={translations.COUNTRY_SEARCH}
              onChange={(event) => this.onSearchValueChange(event.nativeEvent.text)}
              style={styles.searchInput}
            />
          </Item>
          <Right>
            <Button transparent={true} onPress={() => this.props.onClose()}>
              <Text>Close</Text>
            </Button>
          </Right>
        </Header>
        <Content>
          <List>
            {this.state.filteredProposals.map((proposal: IProposal) => this.renderProposal(proposal))}
          </List>
        </Content>
      </Container>
    )
  }

  private renderProposal (proposal: IProposal): ReactNode {
    return (
      <ListItem
        style={this.listItemStyle(proposal)}
        icon={true}
        key={proposal.providerID}
        onPress={() => this.props.onSelect(proposal)}
      >
        <Left style={styles.flagImage}>
          <CountryFlag countryCode={proposal.countryCode}/>
        </Left>
        <Body>
        <Text style={this.listItemTextStyle(proposal)}>{proposal.countryName}</Text>
        <Text style={this.providerIdStyle(proposal)}>
          {proposal.providerID.substring(0, 25) + '...'}
        </Text>
        </Body>
        <Right>
          <Text>{proposal.quality ? Math.round(proposal.quality * 100) + '%' : '?'}</Text>
          <Icon
            name={proposal.isFavorite ? 'md-star' : 'md-star-outline'}
          />
        </Right>
      </ListItem>
    )
  }

  private listItemStyle (proposal: IProposal) {
    const style = [styles.listItem]

    if (this.isProposalSelected(proposal)) {
      style.push(styles.selectedListItem)
    }

    return style
  }

  private listItemTextStyle (proposal: IProposal) {
    return this.isProposalSelected(proposal) ? [styles.selectedListItemText] : []
  }

  private providerIdStyle (proposal: IProposal) {
    return this.isProposalSelected(proposal)
      ? [styles.providerIdText, styles.selectedListItemText]
      : [styles.providerIdText]
  }

  private isProposalSelected (proposal: IProposal) {
    const selected = this.props.selectedProposal

    return selected && selected.providerID === proposal.providerID
  }

  private onSearchValueChange (text: string) {
    const filteredProposals = this.proposalFilter.filterByText(text)

    this.setState({ filteredProposals })
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
  searchIcon: {
    color: platformStyles.search.iconColor
  },
  searchInput: {
    color: platformStyles.search.inputColor
  }
})

export default ProposalList
