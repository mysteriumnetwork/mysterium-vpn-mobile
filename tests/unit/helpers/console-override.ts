// workaround: https://github.com/airbnb/enzyme/issues/831
const consoleOverride = () => {
  const origConsole = console.error
  beforeEach(() => {
    console.error = () => {
      //
    }
  })
  afterEach(() => {
    console.error = origConsole
  })
}

export default consoleOverride
