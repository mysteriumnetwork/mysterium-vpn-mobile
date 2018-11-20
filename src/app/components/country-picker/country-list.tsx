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
import { ICountry } from './country'
import CountryFlag from './country-flag'

type ListProps = {
  countries: ICountry[],
  onClose: () => void,
  onSelect: (country: ICountry) => void
}

type ListState = {
  countries: ICountry[]
}

class CountryList extends React.Component<ListProps, ListState> {
  constructor (props: ListProps) {
    super(props)

    this.state = { countries: this.props.countries }
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
            {this.state.countries.map((country: ICountry) => this.renderListItem(country))}
          </List>
        </Content>
      </Container>
    )
  }

  private renderListItem (country: ICountry): ReactNode {
    return (
      <ListItem
        style={styles.listItem}
        icon={true}
        key={country.providerID}
        onPress={() => this.props.onSelect(country)}
      >
        <Left style={styles.flagImage}>
          <CountryFlag countryCode={country.countryCode}/>
        </Left>
        <Body>
        <Text>{country.name}</Text>
        </Body>
        <Right>
          <Icon
            name={country.isFavorite ? 'md-star' : 'md-star-outline'}
          />
        </Right>
      </ListItem>
    )
  }

  private onSearchValueChange (text: string) {
    let countries = this.props.countries

    if (!text.trim().length) {
      this.setState({ countries })

      return
    }

    countries = this.props.countries.filter((country: ICountry) => {
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

let listItemStyles = {}
if (Platform.OS !== 'ios') {
  listItemStyles = {
    paddingLeft: 15,
    marginLeft: 0
  }
}

const styles: any = StyleSheet.create({
  listItem: listItemStyles,
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
