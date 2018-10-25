import * as React from 'react'
import TequilaRider from '../libraries/tequila/rider'
import TequilaState from '../libraries/tequila/state'
import App from './app'
import Logger from './logger'

const tequilaState = new TequilaState()
const tequilaRider = new TequilaRider(tequilaState)
const logger = new Logger(tequilaState)
logger.logObservableChanges()

const Root: React.SFC = () => (
  <App
    tequilaRider={tequilaRider}
    tequilaState={tequilaState}
  />
)
export default Root
