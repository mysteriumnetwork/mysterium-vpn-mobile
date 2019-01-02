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
import translations from '../../translations'
import CountryFlag from './country-flag'
import { IProposal } from './proposal'
import colors from '../../../app/styles/colors'

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
  constructor (props: ListProps) {
    super(props)

    this.state = { filteredProposals: this.props.proposals }
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
        </Body>
        <Right>
          <Icon
            name={proposal.isFavorite ? 'md-star' : 'md-star-outline'}
          />
        </Right>
      </ListItem>
    )
  }

  private listItemStyle (current: IProposal) {
    const style = [styles.listItem]

    if (this.isProposalSelected(current)) {
      style.push(styles.selectedListItem)
    }

    return style
  }

  private listItemTextStyle (current: IProposal) {
    return this.isProposalSelected(current) ? [styles.selectedListItemText] : []
  }

  private isProposalSelected (proposal: IProposal) {
    const selected = this.props.selectedProposal

    return selected && selected.providerID === proposal.providerID
  }

  private onSearchValueChange (text: string) {
    const filteredProposals = this.filteredProposals(text)

    this.setState({ filteredProposals })
  }

  private filteredProposals (text: string): IProposal[] {
    let filteredProposals = this.props.proposals

    if (!text.trim().length) {
      return filteredProposals
    }

    filteredProposals = filteredProposals.filter((proposal: IProposal) => {
      const name = proposal.countryName || ''
      return name.toLowerCase().includes(text.toLowerCase())
    })

    return filteredProposals
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
