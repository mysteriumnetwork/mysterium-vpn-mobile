import IMessageDisplay from '../../../src/app/messages/message-display'
import MessageDisplayDelegate from '../../../src/app/messages/message-display-delegate'

class MessageDisplayKeeper implements IMessageDisplay {
  public error?: string
  public info?: string

  public showError (message: string) {
    this.error = message
  }

  public showInfo (message: string) {
    this.info = message
  }
}

describe('MessageDisplayDelegate', () => {
  let delegate: MessageDisplayDelegate

  beforeEach(() => {
    delegate = new MessageDisplayDelegate()
  })

  describe('.showError', () => {
    it('delegates call when messageDisplay is set', () => {
      const messageDisplay = new MessageDisplayKeeper()
      delegate.messageDisplay = messageDisplay
      delegate.showError('Error!')
      expect(messageDisplay.error).toEqual('Error!')
    })

    it('throws error if messageDisplay is not set', () => {
      expect(() => delegate.showError('Error!'))
        .toThrow('MessageDisplayDelegate failed - messageDisplay not set')
    })
  })

  describe('.showInfo', () => {
    it('delegates call when messageDisplay is set', () => {
      const messageDisplay = new MessageDisplayKeeper()
      delegate.messageDisplay = messageDisplay
      delegate.showInfo('Info')
      expect(messageDisplay.info).toEqual('Info')
    })

    it('throws error if messageDisplay is not set', () => {
      expect(() => delegate.showInfo('Info'))
        .toThrow('MessageDisplayDelegate failed - messageDisplay not set')
    })
  })
})
