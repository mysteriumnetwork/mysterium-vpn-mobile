
const setupErrorHandlers = () => {
  const defaultHandler = ErrorUtils.getGlobalHandler()
  const wrapGlobalHandler = async (error, isFatal) => {
    // custom error handling could be added here.
    defaultHandler(error, isFatal)
  }
  ErrorUtils.setGlobalHandler(wrapGlobalHandler)
}

module.exports.setupErrorHandlers = setupErrorHandlers
