import { TequilapiClient } from 'mysterium-tequilapi'
import {CONFIG} from '../../js/config'
import { StatusFetcher } from '../../js/fetchers/status-fetcher'
import { store } from '../../js/store/tequilapi-store'
import { TequilapiClientMock } from '../mocks/tequilapi-mock'

jest.useFakeTimers()

describe('StatusFetcher', () => {
  const refreshInterval = CONFIG.REFRESH_INTERVALS.CONNECTION
  let api: TequilapiClient
  let fetcher: StatusFetcher

  beforeEach(() => {
    api = new TequilapiClientMock()
    fetcher = new StatusFetcher(api)
  })

  test('.constructor',  () => {
    expect(fetcher.isStarted).toBe(true)
    expect(store.ConnectionStatus).toEqual({
      status: 'NotConnected',
      sessionId: 'MOCKED_SESSION_ID',
    })

    expect(api.connectionStatus).toHaveBeenCalledTimes(1)
    jest.runTimersToTime(refreshInterval)
    expect(api.connectionStatus).toHaveBeenCalledTimes(2)
  })

  test('.start', () => {
    fetcher.stop()
    jest.runTimersToTime(refreshInterval)
    expect(api.connectionStatus).toHaveBeenCalledTimes(1)

    fetcher.start(refreshInterval)
    expect(api.connectionStatus).toHaveBeenCalledTimes(2)
  })

  test('.stop', () => {
    expect(api.connectionStatus).toHaveBeenCalledTimes(1)
    jest.runTimersToTime(refreshInterval)
    expect(api.connectionStatus).toHaveBeenCalledTimes(2)

    fetcher.stop()
    expect(fetcher.isStarted).toBe(false)

    jest.runTimersToTime(refreshInterval)
    expect(api.connectionStatus).toHaveBeenCalledTimes(2)
    jest.runTimersToTime(refreshInterval)
    expect(api.connectionStatus).toHaveBeenCalledTimes(2)
  })

  test('.refresh', async () => {
    expect(fetcher.isRunning).toBe(false)
    store.ConnectionStatus = null

    const connectedStatus = {
      status: 'Connected',
      sessionId: 'MOCKED_SESSION_ID',
    }
    api.connectionStatus = jest.fn()
      .mockReturnValue(connectedStatus)

    await fetcher.refresh()
    expect(store.ConnectionStatus).toEqual(connectedStatus)
  })
})
