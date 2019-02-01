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
import ConnectionStatus from '../../../../src/app/components/connection-status'
import { ConnectionStatusEnum } from '../../../../src/libraries/tequil-api/enums'

describe('ConnectionStatus', () => {
  it('renders loading text with no status', () => {
    const wrapper = shallow(
      <ConnectionStatus status={undefined}/>
    )
    expect(wrapper.childAt(0).text()).toEqual('Loading...')
  })

  it('renders custom text with known status', () => {
    const wrapper = shallow(
      <ConnectionStatus status={ConnectionStatusEnum.NOT_CONNECTED}/>
    )
    expect(wrapper.childAt(0).text()).toEqual('Disconnected')
  })

  it('throws error with unknown status', () => {
    const render = () => shallow(
      <ConnectionStatus status={'something'}/>
    )
    expect(render).toThrowError('Unknown connection status: something')
  })
})
