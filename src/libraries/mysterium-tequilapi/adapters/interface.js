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

type HttpQueryParams = {
  [string]: mixed
}

interface HttpInterface {
  get (path: string, query: ?HttpQueryParams, timeout: ?number): Promise<?any>;
  post (path: string, data: mixed, timeout: ?number): Promise<?any>;
  delete (path: string, timeout: ?number): Promise<?any>;
  put (path: string, data: mixed, timeout: ?number): Promise<?any>;
}

export type {HttpInterface, HttpQueryParams}
