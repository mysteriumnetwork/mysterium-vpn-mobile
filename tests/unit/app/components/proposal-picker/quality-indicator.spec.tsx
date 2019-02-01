/*
 * Copyright (C) 2019 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
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
import { QualityIndicator } from '../../../../../src/app/components/proposal-picker/quality-indicator'

describe('QualityIndicator', () => {
  it('renders unknown icon for empty quality', () => {
    const wrapper = shallow(<QualityIndicator quality={null}/>)
    expect(wrapper.prop('source').testUri).toContain('unknown.png')
  })

  it('renders low icon for low quality', () => {
    const wrapper = shallow(<QualityIndicator quality={0.1}/>)
    expect(wrapper.prop('source').testUri).toContain('low.png')
  })

  it('renders medium icon for medium quality', () => {
    const wrapper = shallow(<QualityIndicator quality={0.3}/>)
    expect(wrapper.prop('source').testUri).toContain('medium.png')
  })

  it('renders high icon for high quality', () => {
    const wrapper = shallow(<QualityIndicator quality={0.6}/>)
    expect(wrapper.prop('source').testUri).toContain('high.png')
  })
})
