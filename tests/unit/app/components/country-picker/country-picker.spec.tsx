import { shallow } from 'enzyme'
import React from 'react'
import { Country } from '../../../../../src/app/components/country-picker/country'
import CountryPicker from '../../../../../src/app/components/country-picker/country-picker'
import consoleOverride from '../../../helpers/console-override'

describe('CountryPicker', () => {
  consoleOverride()

  let items: Country[] = []
  const onSelect = () => {
    //
  }
  const onFavoriteSelect = () => {
    //
  }
  const isFavoriteSelected = true
  const placeholder = 'Country select'

  beforeEach(() => {
    items = []
  })

  it('renders correctly', () => {
    const wrapper = shallow(
      <CountryPicker
        items={items}
        onSelect={onSelect}
        onFavoriteSelect={onFavoriteSelect}
        isFavoriteSelected={isFavoriteSelected}
        placeholder={placeholder}
      />
    )
    expect(wrapper).toMatchSnapshot()
  })
})
