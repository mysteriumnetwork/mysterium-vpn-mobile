import * as React from 'react'
import App from './app'
import AppStateStore from '../store/app-state-store'
import AppTequilapi from './app-tequilapi'
import {SFC} from 'react'
import Logger from './logger'

const store = new AppStateStore()
const tequilapi = new AppTequilapi(store)
const logger = new Logger(store)
logger.logObservableChanges()

const Root:SFC = () => <App tequilapi={tequilapi}/>
export default Root
