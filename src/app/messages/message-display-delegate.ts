import MessageDisplay from './message-display'

/**
 * Message display delegating to another implementation.
 */
class MessageDisplayDelegate implements MessageDisplay {
  private _messageDisplay?: MessageDisplay

  public set messageDisplay (value: MessageDisplay) {
    this._messageDisplay = value
  }

  public showError (message: string) {
    this.getMessageDisplayOrFail().showError(message)
  }

  public showInfo (message: string) {
    this.getMessageDisplayOrFail().showInfo(message)
  }

  private getMessageDisplayOrFail (): MessageDisplay {
    if (this._messageDisplay === undefined) {
      throw new Error('MessageDisplayDelegate failed - messageDisplay not set')
    }
    return this._messageDisplay
  }
}

export default MessageDisplayDelegate
