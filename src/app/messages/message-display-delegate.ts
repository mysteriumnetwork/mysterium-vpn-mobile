import IMessageDisplay from './message-display'

/**
 * Message display delegating to another implementation.
 */
class MessageDisplayDelegate implements IMessageDisplay {
  private _messageDisplay?: IMessageDisplay

  public set messageDisplay (value: IMessageDisplay) {
    this._messageDisplay = value
  }

  public showError (message: string) {
    this.getMessageDisplayOrFail().showError(message)
  }

  public showInfo (message: string) {
    this.getMessageDisplayOrFail().showInfo(message)
  }

  private getMessageDisplayOrFail (): IMessageDisplay {
    if (this._messageDisplay === undefined) {
      throw new Error('MessageDisplayDelegate failed - messageDisplay not set')
    }
    return this._messageDisplay
  }
}

export default MessageDisplayDelegate
