import { Crashlytics } from 'react-native-fabric'

const setupErrorHandlers = () => {
  const defaultHandler = ErrorUtils.getGlobalHandler()
  const wrapGlobalHandler = async (error, isFatal) => {
    Crashlytics.setBool('dev', true)
    // following Android only
    Crashlytics.logException(error.message);
    // ios only
    Crashlytics.recordError(error)

    defaultHandler(error, isFatal)
  }
  ErrorUtils.setGlobalHandler(wrapGlobalHandler)
}

module.exports.setupErrorHandlers = setupErrorHandlers
