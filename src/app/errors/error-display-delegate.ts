import IErrorDisplay from './error-display'

/**
 * Error display delegating to another implementation.
 */
class ErrorDisplayDelegate implements IErrorDisplay {
  private _errorDisplay?: IErrorDisplay

  public set errorDisplay (value: IErrorDisplay) {
    this._errorDisplay = value
  }

  public showError (error: string) {
    this.getDelegateOrFail().showError(error)
  }

  private getDelegateOrFail (): IErrorDisplay {
    if (this._errorDisplay === undefined) {
      throw new Error('ErrorDisplayDelegate failed - errorDisplay not set')
    }
    return this._errorDisplay
  }
}

export default ErrorDisplayDelegate
