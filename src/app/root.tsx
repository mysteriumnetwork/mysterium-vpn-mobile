import * as React from 'react'
import TequilAPIDriver from '../libraries/tequilAPI/tequilAPI-driver'
import App from './app'
import AppState from './app-state'
import ErrorDisplayDelegate from './errors/error-display-delegate'
import Logger from './logger'

const Root: React.SFC = () => {
  const appState = new AppState()
  const errorDisplayDelegate = new ErrorDisplayDelegate()
  const tequilAPIDriver = new TequilAPIDriver(appState, errorDisplayDelegate)

  const logger = new Logger(appState)
  logger.logObservableChanges()

  return (
    <App
      tequilAPIDriver={tequilAPIDriver}
      appState={appState}
      errorDisplayDelegate={errorDisplayDelegate}
    />
  )
}
export default Root
