import React from 'react'
import { FeedbackType } from 'src/bug-reporter/feedback-reporter'
import FeedbackForm from 'src/app/components/feedback-form'

type FeedbackScreenProps = {
  value: FeedbackType,
  label: string
}

class FeedbackScreen extends React.Component<FeedbackScreenProps> {
  render () {
    return (<FeedbackForm onSubmit={} options={}/>)
  }
}

export default FeedbackScreen
