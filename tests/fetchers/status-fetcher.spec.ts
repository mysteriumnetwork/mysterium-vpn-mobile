import { StatusFetcher } from '../../js/fetchers/status-fetcher'
import { TequilapiClientMock } from '../mocks/tequilapi-mock'
import { TequilapiClient } from 'mysterium-tequilapi'
import { store } from '../../js/store/tequilapi-store'

describe('StatusFetcher', () => {
  let api: TequilapiClient
  let fetcher: StatusFetcher

  beforeEach(() => {
    api = new TequilapiClientMock()
    fetcher = new StatusFetcher(api)
  })

  test('.constructor',  () => {
    expect(api.connectionStatus).toHaveBeenCalled()
  })

  test('.refresh', async () => {
    await fetcher.refresh()
    expect(store.ConnectionStatus).toEqual({
      status: 'NotConnected',
      sessionId: 'MOCKED_SESSION_ID'
    })
  })
})
