interface StatisticsEventManagerAdapter {
  startConnectionTracking (
    connectionDetails: ConnectionDetails,
    countryDetails: CountryDetails
  ): ConnectionEventSenderAdapter
}

interface ConnectionEventSenderAdapter {
  sendSuccessfulConnectionEvent (): void
  sendFailedConnectionEvent (error: string): void
  sendCanceledConnectionEvent (): void
}

type ConnectionDetails = {
  consumerId: string,
  providerId: string
}

type CountryDetails = {
  originalCountry: string,
  providerCountry: string | null
}

export { StatisticsEventManagerAdapter, ConnectionEventSenderAdapter }
