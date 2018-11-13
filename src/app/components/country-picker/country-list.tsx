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
import * as React from 'react'

import { Platform, StyleSheet } from 'react-native'
import colors from '../../styles/colors'
import { Country } from './country'
import CountryFlag from './country-flag'
import translations from '../../translations'

type ListProps = {
  items: Country[],
  onClose: () => void,
  onSelect: (country: Country) => void
}

class CountryList extends React.Component<ListProps> {
  private items: Country[]

  constructor (props: ListProps) {
    super(props)

    this.items = this.props.items
  }

  public render () {
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
            {this.items.map((country: Country) => this.renderListItem(country))}
          </List>
        </Content>
      </Container>
    )
  }

  private renderListItem (country: Country) {
    return (
      <ListItem icon={true} key={country.id} onPress={() => this.onItemSelect(country)}>
        <Left style={styles.flagImage}>
          <CountryFlag countryCode={country.countryCode}/>
        </Left>
        <Body>
        <Text>{country.name}</Text>
        </Body>
      </ListItem>
    )
  }

  private onSearchValueChange (text: string) {
    this.items = this.props.items

    if (!text.trim().length) {
      return
    }

    this.items = this.items.filter((country: Country) => {
      return country.name.toLowerCase().includes(text.toLowerCase())
    })
  }

  private onItemSelect (country: Country) {
    this.props.onSelect(country)
  }
}

const platformStyles = {
  search: {
    iconColor: Platform.OS === 'ios' ? '#222' : '#fff',
    inputColor: Platform.OS === 'ios' ? '#222' : '#fff'
  }
}

const styles: any = StyleSheet.create({
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

export default CountryList
