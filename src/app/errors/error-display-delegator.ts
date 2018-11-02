import IErrorDisplay from './error-display'

/**
 * Error display delegating to another implementation.
 */
class ErrorDisplayDelegator implements IErrorDisplay {
  private _delegate?: IErrorDisplay

  public set delegate (value: IErrorDisplay) {
    this._delegate = value
  }

  public showError (error: string) {
    this.getDelegateOrFail().showError(error)
  }

  private getDelegateOrFail (): IErrorDisplay {
    if (this._delegate === undefined) {
      throw new Error('ErrorDisplayDelegator failed - delegate not set')
    }
    return this._delegate
  }
}

export default ErrorDisplayDelegator
