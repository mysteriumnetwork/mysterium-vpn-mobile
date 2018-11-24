import { TequilapiClient } from 'mysterium-tequilapi'

const TequilapiClientMock = jest.fn<TequilapiClient>(() => ({
  connectionStatus: jest
    .fn()
    .mockReturnValue(new Promise((resolve) => resolve({
      status: 'NotConnected',
      sessionId: 'MOCKED_SESSION_ID'
    }))),
  connectionIP: jest
    .fn()
    .mockReturnValue(new Promise((resolve) => resolve({
      ip: '123.123.123.123'
    }))),
  connectionStatistics: jest
    .fn()
    .mockReturnValue(new Promise((resolve) => resolve({
      duration: 60,
      bytesReceived: 1024,
      bytesSent: 512
    })))
}))

export { TequilapiClientMock }
