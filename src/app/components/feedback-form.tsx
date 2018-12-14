/*
 * Copyright (C) 2018 The "MysteriumNetwork/mysterium-vpn-mobile" Authors.
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

import { Button, Form, Grid, Picker, Row, Text, Textarea } from 'native-base'
import React, { ReactNode } from 'react'
import { UserFeedback } from '../../bug-reporter/feedback-reporter'
import { STYLES } from '../../styles'

type FeedbackFormProps = {
  onSubmit: (feedback: UserFeedback) => void,

  options: FeedbackTypeOption[]
}

type FeedbackTypeOption = {
  value: string,
  label: string
}

class FeedbackForm extends React.PureComponent
  <FeedbackFormProps, UserFeedback> {
  public state: UserFeedback = {
    type: 'bug',
    message: ''
  }

  public render (): ReactNode {
    return (
      <Form>
        <Grid>
          <Row>
            <Text style={{ paddingTop: 14, textAlign: 'right' }}>Feedback type:</Text>
          </Row>
          <Row>
            <Picker
              selectedValue={this.state.type}
              onValueChange={this.selectType}
            >
              {this.props.options.map((option: FeedbackTypeOption) =>
                <Picker.Item label={option.label} key={option.value} value={option.value}/>)}
            </Picker>
          </Row>
          <Row>
            <Text style={{ paddingTop: 14, textAlign: 'right' }}>Message:</Text>
          </Row>
          <Row>
          <Textarea
            style={{ width: '100%' }}
            placeholder={'You may enter your feedback here...'}
            rowSpan={5}
          />
          </Row>
        </Grid>
        <Button
          color={STYLES.COLOR_MAIN}
          onPress={() => {
            this.props.onSubmit(this.state)
          }}
        >
          <Text>Send Feedback</Text>
        </Button>
      </Form>
    )
  }

  private selectType = (type: string) => {
    this.setState({ type })
  }
}

export { FeedbackTypeOption }
export default FeedbackForm
