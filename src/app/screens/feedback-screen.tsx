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

import { Container, Content, Toast } from 'native-base'
import React from 'react'
import { StyleSheet, Text, View } from 'react-native'
import FeedbackReporter, { UserFeedback } from '../../bug-reporter/feedback-reporter'
import FeedbackForm, { FeedbackTypeOption } from '../components/feedback-form'
import IconButton from '../components/icon-button'
import colors from '../styles/colors'
import translations from '../translations'

type FeedbackScreenProps = {
  feedbackReporter: FeedbackReporter,
  navigateBack: () => void
}

class FeedbackScreen extends React.Component<FeedbackScreenProps> {
  public render () {
    return (
      <Container>
        <View style={styles.backButton}>
          <IconButton
            icon="ios-arrow-dropleft"
            onClick={() => this.props.navigateBack()}
          />
        </View>
        <Text style={styles.heading}>Feedback</Text>
        <Content style={styles.container} padder={true}>
          <FeedbackForm
            onSubmit={(feedback: UserFeedback) => this.submitFeedback(feedback)}
            options={this.feedbackTypeOptions}
          />
        </Content>
      </Container>
    )
  }

  private get feedbackTypeOptions (): FeedbackTypeOption[] {
    return [
      {
        label: 'Bug',
        value: 'bug'
      },
      {
        label: 'Connectivity issues',
        value: 'connectivity'
      },
      {
        label: 'Positive feedback',
        value: 'positive'
      }
    ]
  }

  private submitFeedback (feedback: UserFeedback) {
    this.props.feedbackReporter.sendFeedback(feedback)
    this.props.navigateBack()
    this.showSubmitMessage()
  }

  private showSubmitMessage () {
    Toast.show({
      duration: 5000,
      text: translations.FEEDBACK_SUBMIT,
      textStyle: {
        textAlign: 'center'
      }
    })
  }
}

const styles = StyleSheet.create({
  backButton: {
    position: 'absolute',
    top: 10,
    left: 10,
    zIndex: 2
  },
  heading: {
    textAlign: 'center',
    fontSize: 24,
    color: colors.primary,
    marginTop: 15
  },
  container: {
    marginTop: 50
  }
})

export default FeedbackScreen
