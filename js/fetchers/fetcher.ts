/*
 * Copyright (C) 2018 The 'MysteriumNetwork/mysterion' Authors.
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

import {observable, reaction} from "mobx";
import Timer = NodeJS.Timer;
import {CONFIG} from "../config";

export interface Fetcher {
  refresh (): void
}

export abstract class FetcherBase<T> implements Fetcher {
  @observable isRunning: boolean = false

  _interval: Timer | null = null
  _name: string
  _prevData: T | null = null

  constructor (name: string) {
    this._name = name
  }

  start (interval: number) {
    this.run()
    this._interval = setInterval(() => this.run(), interval * CONFIG.REFRESH_INTERVALS.INTERVAL_MS)
  }

  stop () {
    if (this._interval) {
      clearInterval(this._interval)
    }
    this._interval = null
  }

  refresh (): Promise<void> {
    if (!this.isRunning) {
      return this.run()
    }
    return new Promise(resolve => {
      // run as soon as possible
      reaction(
        () => this.isRunning,
        async (isRunning, runReaction) => {
          if (!isRunning) {
            runReaction.dispose()
            await this.run()
            resolve()
          }
        })
    })
  }

  protected async run () {
    if (this.isRunning || !this.canAction) {
      return
    }
    this.isRunning = true

    try {
      const data = await this.fetch()
      if (JSON.stringify(data) !== JSON.stringify(this._prevData)) {
        this._prevData = data
        console.info(`Fetcher '${this._name}' returns`, data)
        this.update(data)
      }
    }
    catch (e) {
      console.warn(`'${this._name}' fetching error`, e)
    }
    finally {
      this.isRunning = false
    }
  }

  protected get canAction (): boolean {
    return true
  }

  protected abstract async fetch (): Promise<T>

  protected abstract update (data: T): void
}
