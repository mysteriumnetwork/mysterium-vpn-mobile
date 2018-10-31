import * as React from 'react'
import TequilAPIDriver from '../libraries/tequilAPI/tequilAPI-driver'
import App from './app'
import AppState from './app-state'
import ErrorAlert from './error-alert'
import Logger from './logger'

const Root: React.SFC = () => {
  const appState = new AppState()
  const errorAlert = new ErrorAlert()
  const tequilAPIDriver = new TequilAPIDriver(appState, errorAlert)

  const logger = new Logger(appState)
  logger.logObservableChanges()

  return (
    <App
      tequilAPIDriver={tequilAPIDriver}
      appState={appState}
      errorAlert={errorAlert}
    />
  )
}
export default Root
