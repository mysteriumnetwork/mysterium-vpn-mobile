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

import { shallow } from 'enzyme'
import React from 'react'
import countries from '../../../../../src/app/components/proposal-picker/countries'
import CountryFlag from '../../../../../src/app/components/proposal-picker/country-flag'

describe('QualityIndicator', () => {
  describe('when showing placeholder', () => {
    it('renders country flag with known country code', () => {
      const wrapper = shallow(<CountryFlag countryCode={'lt'} showPlaceholder={true}/>)

      expect(wrapper.prop('source').uri).toEqual(countries.lt.image)
    })

    it('renders default flag with unknown country code', () => {
      const wrapper = shallow(<CountryFlag countryCode={'unknown'} showPlaceholder={true}/>)

      expect(wrapper.prop('source')).toBeUndefined()
      expect(wrapper.prop('name')).toEqual('ios-globe')
    })

    it('renders default flag with empty country code', () => {
      const wrapper = shallow(<CountryFlag countryCode={null} showPlaceholder={true}/>)

      expect(wrapper.prop('source')).toBeUndefined()
      expect(wrapper.prop('name')).toEqual('ios-globe')
    })
  })

  describe('when not showing placeholder', () => {
    it('renders nothing with unknown country code', () => {
      const wrapper = shallow(<CountryFlag countryCode={'unknown'} showPlaceholder={false}/>)
      expect(wrapper.get(0)).toBeNull()
    })

    it('renders nothing with empty country code', () => {
      const wrapper = shallow(<CountryFlag countryCode={null} showPlaceholder={false}/>)
      expect(wrapper.get(0)).toBeNull()
    })
  })
})
