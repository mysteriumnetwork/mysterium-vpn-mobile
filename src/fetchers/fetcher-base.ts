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

import { observable, reaction } from 'mobx'
import Timer = NodeJS.Timer
import { IFetcher } from './fetcher'

export abstract class FetcherBase<T> implements IFetcher {
  @observable
  public isRunning: boolean = false

  private interval?: Timer

  protected constructor (protected name: string, private update: (data: T) => void) {
  }

  public get isStarted (): boolean {
    return this.interval !== undefined
  }

  public start (interval: number) {
    if (this.isStarted) {
      return
    }

    this.run().catch(error => {
      console.error('Fetcher run failed:', error)
    })

    this.interval = setInterval(
      () => this.run(),
      interval
    )
  }

  public stop () {
    if (this.interval) {
      clearInterval(this.interval)
    }

    this.interval = undefined
  }

  public refresh (): Promise<void> {
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
        }
      )
    })
  }

  protected get canRun (): boolean {
    return true
  }

  protected abstract async fetch (): Promise<T>

  private async run () {
    if (this.isRunning || !this.canRun) {
      return
    }

    this.isRunning = true

    try {
      const data = await this.fetch()
      this.update(data)
    } catch (e) {
      console.warn(`'${this.name}' fetching error`, e)
    } finally {
      this.isRunning = false
    }
  }
}
