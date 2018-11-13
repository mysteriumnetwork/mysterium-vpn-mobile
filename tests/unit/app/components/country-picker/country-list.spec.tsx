import { render, shallow } from 'enzyme'
import React from 'react'
import { Country } from '../../../../../src/app/components/country-picker/country'
import CountryList from '../../../../../src/app/components/country-picker/country-list'
import consoleOverride from '../../../helpers/console-override'

describe('CountryList', () => {
  consoleOverride()

  let items: Country[] = []
  const onSelect = () => {
    //
  }
  const onClose = () => {
    //
  }

  beforeEach(() => {
    items = []
  })

  it('renders correctly', () => {
    const wrapper = shallow(
      <CountryList
        items={items}
        onSelect={onSelect}
        onClose={onClose}
      />
    )
    expect(wrapper).toMatchSnapshot()
  })

  const renderWrap = () => {
    return render(
      <CountryList
        items={items}
        onSelect={onSelect}
        onClose={onClose}
      />
    )
  }
  it('renders items', () => {
    items = [
      {
        id: '0x1',
        name: 'Lithuania',
        countryCode: 'lt'
      },
      {
        id: '0x2',
        name: 'United States',
        countryCode: 'us'
      }
    ]

    const wrapper = renderWrap()

    expect(wrapper.text()).toContain('Lithuania')
    expect(wrapper.text()).toContain('United States')
  })

  it('has close button', () => {
    const wrapper = renderWrap()

    expect(wrapper.text()).toContain('Close')
  })
})
