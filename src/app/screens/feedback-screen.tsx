import { Container, Content, Toast } from 'native-base'
import React from 'react'
import { StyleSheet, View } from 'react-native'
import { IFeedbackReporter, UserFeedback } from '../../bug-reporter/feedback-reporter'
import FeedbackForm, { FeedbackTypeOption } from '../components/feedback-form'
import IconButton from '../components/icon-button'
import translations from '../translations'

type FeedbackScreenProps = {
  feedbackReporter: IFeedbackReporter,
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
        label: 'Crash',
        value: 'crash'
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
    left: 10
  },
  container: {
    marginTop: 50
  }
})

export default FeedbackScreen
