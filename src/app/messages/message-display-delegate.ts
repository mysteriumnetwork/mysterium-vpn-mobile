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
