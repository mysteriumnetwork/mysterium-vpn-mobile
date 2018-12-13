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

import { Button, Col, Container, Content, Form, Grid, Picker, Text, Textarea } from 'native-base'
import React, { ReactNode } from 'react'
import { FeedbackType, UserFeedback } from '../../bug-reporter/feedback-reporter'
import { STYLES } from '../../styles'

type FeedbackFormProps = {
  onSubmit: (feedback: UserFeedback) => void,

  options: FeedbackTypeOption[]
}

type FeedbackTypeOption = {
  value: FeedbackType,
  label: string
}

export default class FeedbackForm extends React.PureComponent
  <FeedbackFormProps, UserFeedback> {
  public state: UserFeedback = {
    type: 'bug',
    message: ''
  }

  public render (): ReactNode {
    return (
      <Container>
        <Content padder={true}>
          <Form>
            <Grid>
              <Col>
                <Text style={{ paddingTop: 14, textAlign: 'right' }}>Feedback type:</Text>
              </Col>
              <Col size={2}>
                <Picker
                  selectedValue={this.state.type}
                  onValueChange={this.selectType}
                >
                  {this.props.options.map((option: FeedbackTypeOption) =>
                    <Picker.Item label={option.label} key={option.value} value={option.value}/>)}
                </Picker>
              </Col>
            </Grid>
            <Textarea
              placeholder={'You may enter your feedback here...'}
              rowSpan={5}
            />
            <Button
              color={STYLES.COLOR_MAIN}
              onPress={() => {
                if (this.state.type) {
                  this.props.onSubmit(this.state)
                }
              }}
            >
              <Text>Send Feedback</Text>
            </Button>
          </Form>
        </Content>
      </Container>
    )
  }

  private selectType = (type: FeedbackType) => {
    this.setState({ ...this.state, type })
  }
}
