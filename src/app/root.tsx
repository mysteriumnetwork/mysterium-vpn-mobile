import * as React from 'react'
import TequilaRider from '../libraries/tequila/tequila-rider'
import App from './app'
import AppState from './app-state'
import Logger from './logger'

const Root: React.SFC = () => {
  const tequilaState = new AppState()
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
