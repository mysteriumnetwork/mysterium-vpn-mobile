import { TequilapiClient } from 'mysterium-tequilapi'
import { CONFIG } from '../../js/config'
import { StatusFetcher } from '../../js/fetchers/status-fetcher'
import { store } from '../../js/store/tequilapi-store'
import { TequilapiClientMock } from '../mocks/tequilapi-mock'

describe('StatusFetcher', () => {
  const refreshInterval = CONFIG.REFRESH_INTERVALS.CONNECTION
  let api: TequilapiClient
  let fetcher: StatusFetcher

  beforeAll(() => {
    jest.useFakeTimers()
  })

  afterAll(() => {
    jest.useRealTimers()
  })

  beforeEach(() => {
    api = new TequilapiClientMock()
    fetcher = new StatusFetcher(api)
    jest.runAllTicks()
  })

  it('.constructor',  () => {
    expect(fetcher.isStarted).toBe(true)

    expect(api.connectionStatus).toHaveBeenCalledTimes(1)
    expect(store.ConnectionStatus).toEqual({
      status: 'NotConnected',
      sessionId: 'MOCKED_SESSION_ID',
    })
    expect(fetcher.isRunning).toBe(false)

    for (let call = 2; call < 6; call++) {
      jest.advanceTimersByTime(refreshInterval)
      expect(api.connectionStatus).toHaveBeenCalledTimes(call)
      jest.runAllTicks()
      expect(fetcher.isRunning).toBe(false)
    }
  })

  it('.start', () => {
    fetcher.stop()
    jest.runAllTicks()
    expect(api.connectionStatus).toHaveBeenCalledTimes(1)
    expect(fetcher.isRunning).toBe(false)

    fetcher.start(refreshInterval)
    expect(api.connectionStatus).toHaveBeenCalledTimes(2)
  })

  it('.stop', () => {
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

  it('.refresh', async () => {
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
