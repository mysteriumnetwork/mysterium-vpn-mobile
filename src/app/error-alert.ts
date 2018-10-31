import ErrorDropdown from './components/error-dropdown'

/**
 * Allows showing errors.
 * Requires ErrorDropdown to be set.
 */
class ErrorAlert {
  private _errorDropdown?: ErrorDropdown

  public set errorDropdown (value: ErrorDropdown) {
    this._errorDropdown = value
  }

  public showError (error: string) {
    if (this._errorDropdown === undefined) {
      throw Error('Cannot show error, because ErrorDropdown is not initialized yet')
    }
    this._errorDropdown.showError(error)
  }
}

export default ErrorAlert
