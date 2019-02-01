/*
 * Copyright (C) 2019 The "mysteriumnetwork/mysterium-vpn-mobile" Authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import MessageDisplay from '../../../src/app/messages/message-display'
import MessageDisplayDelegate from '../../../src/app/messages/message-display-delegate'

class MessageDisplayKeeper implements MessageDisplay {
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
