import { Crashlytics } from 'react-native-fabric'

const setupErrorHandlers = () => {
  const defaultHandler = ErrorUtils.getGlobalHandler()
  const wrapGlobalHandler = async (error, isFatal) => {
    // following Android only
    Crashlytics.logException(error.message);

    defaultHandler(error, isFatal)
  }
  ErrorUtils.setGlobalHandler(wrapGlobalHandler)
}

export { setupErrorHandlers }
