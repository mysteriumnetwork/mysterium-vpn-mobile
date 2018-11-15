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
import { Country } from './country'
import CountryFlag from './country-flag'

type ListProps = {
  items: Country[],
  onClose: () => void,
  onSelect: (country: Country) => void
}

type ListState = {
  countries: Country[]
}

class CountryList extends React.Component<ListProps, ListState> {
  constructor (props: ListProps) {
    super(props)

    this.state = { countries: this.props.items }
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
            {this.state.countries.map((country: Country) => this.renderListItem(country))}
          </List>
        </Content>
      </Container>
    )
  }

  private renderListItem (country: Country): ReactNode {
    return (
      <ListItem icon={true} key={country.id} onPress={() => this.props.onSelect(country)}>
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
    let countries = this.props.items

    if (!text.trim().length) {
      this.setState({ countries })

      return
    }

    countries = this.props.items.filter((country: Country) => {
      return country.name.toLowerCase().includes(text.toLowerCase())
    })

    this.setState({ countries })
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
