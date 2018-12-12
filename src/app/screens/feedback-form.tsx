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

import { Button, Container, Content, Form, Picker, Text, Textarea } from 'native-base'
import React, { ReactNode } from 'react'
import { FeedbackType, IFeedbackReporter } from '../../bug-reporter/feedback-reporter'
import { STYLES } from '../../styles'

type FeedbackReporterProp = { feedbackReporter: IFeedbackReporter }
type Option = {
  value: FeedbackType,
  label: string
}
type PickerOptionsProp = {
  options: Option[]
}

type FeedbackFormState = {
  feedbackType: FeedbackType,
  feedbackMessage: string
}

export default class FeedbackForm extends React.PureComponent
  <FeedbackReporterProp & PickerOptionsProp, FeedbackFormState> {
  public state: FeedbackFormState = {
    feedbackType: 'bug',
    feedbackMessage: ''
  }

  public render (): ReactNode {
    return (
      <Container>
        <Content padder={true}>
          <Form>
            <Text style={{ paddingLeft: 10 }}>Please select your feedback type</Text>
            <Picker
              inlineLabel={true}
              placeholder="Select your SIM"
              selectedValue={this.state.feedbackType}
              onValueChange={this.selectType}
            >
              {this.props.options.map((opt: Option) =>
                <Picker.Item label={opt.label} key={opt.value} value={opt.value}/>)}
            </Picker>
            <Textarea
              placeholder={'You may enter your feedback here...'}
              rowSpan={5}
            />
            <Button
              color={STYLES.COLOR_MAIN}
              onPress={() => this.props.feedbackReporter.sendFeedback({
                type: this.state.feedbackType,
                message: this.state.feedbackMessage
              })}
            >
              <Text>Send Feedback</Text>
            </Button>
          </Form>
        </Content>
      </Container>
    )
  }

  private selectType = (type: FeedbackType) => {
    this.setState({ ...this.state, feedbackType: type })
  }
}
