/**
 * Allows showing errors.
 * Requires dropdown to be set.
 */
class ErrorAlert {
  private _dropdown: any

  public set dropdown (value: any) {
    this._dropdown = value
  }

  public showError (error: string) {
    if (this._dropdown === undefined) {
      throw Error('ErrorDropdown dropdown is not initialized yet')
    }
    this._dropdown.alertWithType('error', 'Error', error)
  }
}

export default ErrorAlert
