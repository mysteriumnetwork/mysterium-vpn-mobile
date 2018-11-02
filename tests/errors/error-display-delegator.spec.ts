import IErrorDisplay from '../../src/app/errors/error-display'
import ErrorDisplayDelegator from '../../src/app/errors/error-display-delegator'

class ErrorDisplayKeeper implements IErrorDisplay {
  public error?: string

  public showError (error: string) {
    this.error = error
  }
}

describe('ErrorDisplayDelegator', () => {
  describe('.showError', () => {
    it('delegates call when delegate is set', () => {
      const delegator = new ErrorDisplayDelegator()
      const delegate = new ErrorDisplayKeeper()
      delegator.delegate = delegate
      delegator.showError('Error!')
      expect(delegate.error).toEqual('Error!')
    })

    it('throws error if delegate is not set', () => {
      const delegator = new ErrorDisplayDelegator()
      expect(() => delegator.showError('Error!'))
        .toThrow('ErrorDisplayDelegator failed - delegate not set')
    })
  })
})
