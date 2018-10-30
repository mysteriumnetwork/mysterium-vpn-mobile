import { shallow } from 'enzyme'
import React from 'react'
import Error from '../../src/app/components/error'

describe('Error', () => {
  it('renders correctly', () => {
    const wrapper = shallow(
      <Error message={'Evil is happening!'}/>
    )
    expect(wrapper).toMatchSnapshot()
  })
})
