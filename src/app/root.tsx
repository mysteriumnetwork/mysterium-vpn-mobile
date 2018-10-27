import * as React from 'react'
import TequilaRider from '../libraries/tequila/tequila-rider'
import TequilaState from '../libraries/tequila/tequila-state'
import App from './app'
import Logger from './logger'

const Root: React.SFC = () => {
  const tequilaState = new TequilaState()
  const tequilaRider = new TequilaRider(tequilaState)

  const logger = new Logger(tequilaState)
  logger.logObservableChanges()

  return (
    <App
      tequilaRider={tequilaRider}
      tequilaState={tequilaState}
    />
  )
}
export default Root
