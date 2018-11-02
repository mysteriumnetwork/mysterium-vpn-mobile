import IErrorDisplay from '../../../src/app/errors/error-display'
import ErrorDisplayDelegate from '../../../src/app/errors/error-display-delegate'

class ErrorDisplayKeeper implements IErrorDisplay {
  public error?: string

  public showError (error: string) {
    this.error = error
  }
}

describe('ErrorDisplayDelegate', () => {
  let delegate: ErrorDisplayDelegate

  beforeEach(() => {
    delegate = new ErrorDisplayDelegate()
  })

  describe('.showError', () => {
    it('delegates call when errorDisplay is set', () => {
      const errorDisplay = new ErrorDisplayKeeper()
      delegate.errorDisplay = errorDisplay
      delegate.showError('Error!')
      expect(errorDisplay.error).toEqual('Error!')
    })

    it('throws error if errorDisplay is not set', () => {
      expect(() => delegate.showError('Error!'))
        .toThrow('ErrorDisplayDelegate failed - errorDisplay not set')
    })
  })
})
