import { shallow } from 'enzyme'
import React from 'react'
import ConnectionStatus from '../../../../src/app/components/connection-status'
import { ConnectionStatusEnum } from '../../../../src/libraries/tequilapi/enums'

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
