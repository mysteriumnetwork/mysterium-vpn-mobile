interface StatisticsAdapter {
  startConnectionTracking (
    connectionDetails: ConnectionDetails,
    countryDetails: CountryDetails
  ): ConnectionEventAdapter
}

interface ConnectionEventAdapter {
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

export { StatisticsAdapter, ConnectionEventAdapter }
