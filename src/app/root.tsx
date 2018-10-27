import * as React from 'react'
import TequilaRider from '../libraries/tequila/tequila-rider'
import App from './app'
import AppState from './app-state'
import Logger from './logger'

const Root: React.SFC = () => {
  const appState = new AppState()
  const tequilaRider = new TequilaRider(appState)

  const logger = new Logger(appState)
  logger.logObservableChanges()

  return (
    <App
      tequilaRider={tequilaRider}
      appState={appState}
    />
  )
}
export default Root
