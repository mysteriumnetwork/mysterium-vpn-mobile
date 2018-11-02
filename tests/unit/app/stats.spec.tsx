import { shallow } from 'enzyme'
import React from 'react'
import Stats from '../../../src/app/components/stats'

describe('Stats', () => {
  it('renders correctly', () => {
    const wrapper = shallow(
      <Stats duration={999999} bytesReceived={9999999} bytesSent={9999}/>
    )
    expect(wrapper).toMatchSnapshot()
  })
})
