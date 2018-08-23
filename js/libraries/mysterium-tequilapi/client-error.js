/*
 * Copyright (C) 2017 The "MysteriumNetwork/mysterion" Authors.
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

// @flow

const errorCodes = {
  CONNECTION_ABORTED_ERROR_CODE: 'ECONNABORTED'
}

const httpResponseCodes = {
  CLIENT_CLOSED_REQUEST: 499,
  SERVICE_UNAVAILABLE: 503
}

type AxiosError = {
  message: string,
  response?: { status: number },
  code?: string
}

function markErrorAsHttp (error: Error) {
  const errorObj = (error: Object)
  errorObj.isHttpError = true
}

function isHttpError (error: Error): boolean {
  const errorObj = (error: Object)
  return errorObj.isHttpError === true
}

function isNetworkError (error: Error): boolean {
  return error.message === 'Network Error'
}

function isTimeoutError (error: Error): boolean {
  const axiosError = (error: AxiosError)
  if (!axiosError.code) {
    return false
  }
  return axiosError.code === errorCodes.CONNECTION_ABORTED_ERROR_CODE
}

function isRequestClosedError (error: Error): boolean {
  return hasHttpStatus(error, httpResponseCodes.CLIENT_CLOSED_REQUEST)
}

function isServiceUnavailableError (error: Error): boolean {
  return hasHttpStatus(error, httpResponseCodes.SERVICE_UNAVAILABLE)
}

function hasHttpStatus (error: Error, expectedStatus: number): boolean {
  const axiosError = (error: AxiosError)
  if (!axiosError.response) {
    return false
  }

  return axiosError.response.status === expectedStatus
}

export { isHttpError, markErrorAsHttp, isNetworkError, isTimeoutError, isRequestClosedError, isServiceUnavailableError }
