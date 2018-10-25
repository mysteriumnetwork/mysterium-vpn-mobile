import * as React from 'react'
import TequilapiRider from '../libraries/tequilapi-rider'
import AppStateStore from '../store/app-state-store'
import App from './app'
import Logger from './logger'

const store = new AppStateStore()
const tequilapi = new TequilapiRider(store)
const logger = new Logger(store)
logger.logObservableChanges()

const Root: React.SFC = () => <App tequilapi={tequilapi}/>
export default Root
