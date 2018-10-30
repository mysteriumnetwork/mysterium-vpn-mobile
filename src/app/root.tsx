import * as React from 'react'
import TequilAPIDriver from '../libraries/tequilAPI/tequilAPI-driver'
import App from './app'
import AppState from './app-state'
import Logger from './logger'

const Root: React.SFC = () => {
  const appState = new AppState()
  const tequilAPIDriver = new TequilAPIDriver(appState)

  const logger = new Logger(appState)
  logger.logObservableChanges()

  return (
    <App
      tequilAPIDriver={tequilAPIDriver}
      appState={appState}
    />
  )
}
export default Root
