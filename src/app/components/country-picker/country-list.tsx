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

import { Image, Platform, StyleSheet } from 'react-native'
import colors from '../../styles/colors'
import { CountryListItem } from './country-picker'
import { getCountryImageFile } from './image-handler'

export interface IProps {
  items: CountryListItem[]
  onClose: () => void,
  onSelect: (country: CountryListItem) => void
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
    padding: 0,
    borderRadius: 13,
    borderWidth: 0.5,
    borderColor: colors.border
  },
  searchIcon: {
    color: platformStyles.search.iconColor
  },
  searchInput: {
    color: platformStyles.search.inputColor
  }
})

class CountryList extends React.Component<IProps> {
  private items: CountryListItem[]

  constructor (props: IProps) {
    super(props)

    this.items = this.props.items
  }

  public renderListItem (country: CountryListItem) {
    return (
      <ListItem icon={true} key={country.id} onPress={() => this.onItemSelect(country)}>
        <Left>
          <Image
            style={styles.flagImage}
            source={{ uri: getCountryImageFile(country.countryCode) }}
          />
        </Left>
        <Body>
        <Text>{country.name}</Text>
        </Body>
      </ListItem>
    )
  }

  public render () {
    return (
      <Container>
        <Header>
          <Item style={styles.headerItem}>
            <Icon name="ios-search" style={styles.searchIcon}/>
            <Input
              placeholderTextColor={platformStyles.search.inputColor}
              placeholder="Search countries"
              onChange={(event) => this.onSearchValueChange(event.nativeEvent.text)}
              style={styles.searchInput}
            />
          </Item>
          <Right>
            <Button transparent={true} onPress={this.props.onClose}>
              <Text>Close</Text>
            </Button>
          </Right>
        </Header>
        <Content>
          <List>
            {this.items.map((country: CountryListItem) => this.renderListItem(country))}
          </List>
        </Content>
      </Container>
    )
  }

  private onSearchValueChange (text: string) {
    this.items = this.props.items

    if (!text.trim().length) {
      return
    }

    this.items = this.items.filter((country: CountryListItem) => {
      return country.name.toLowerCase().includes(text.toLowerCase())
    })
  }

  private onItemSelect (country: CountryListItem) {
    this.props.onSelect(country)
    this.props.onClose()
  }
}

export default CountryList
