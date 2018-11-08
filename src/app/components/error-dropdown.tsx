/*
 * Copyright (C) 2018 The 'MysteriumNetwork/mysterium-vpn-mobile' Authors.
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

import React, { ReactNode } from 'react'
import DropdownAlert from 'react-native-dropdownalert'
import IErrorDisplay from '../errors/error-display'

/**
 * Allowing show error messages as a dropdowns.
 */
class ErrorDropdown extends React.Component implements IErrorDisplay {
  private dropdown: DropdownAlert

  public render (): ReactNode {
    return <DropdownAlert ref={(ref: DropdownAlert) => this.dropdown = ref}/>
  }

  public showError (error: string) {
    if (this.dropdown === undefined) {
      throw new Error('DropdownAlert not set yet')
    }
    this.dropdown.alertWithType('error', 'Error', error)
  }
}

export default ErrorDropdown
