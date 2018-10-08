import { TequilapiClient } from 'mysterium-tequilapi'

const TequilapiClientMock = jest.fn<TequilapiClient>(() => ({
  connectionStatus: jest
    .fn()
    .mockReturnValue({
      status: 'NotConnected',
      sessionId: 'MOCKED_SESSION_ID'
    })
}))

export { TequilapiClientMock }
