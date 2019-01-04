import Proposal from '../../../../src/app/models/proposal'

const proposals: Proposal[] = [
  {
    providerID: '0x1',
    countryCode: 'lt',
    countryName: 'Lithuania',
    metrics: {
      connectCount: {
        success: 0,
        fail: 0,
        timeout: 0
      }
    }
  },
  {
    providerID: '0x2',
    countryCode: 'us',
    countryName: 'United States',
    metrics: {
      connectCount: {
        success: 2,
        fail: 6,
        timeout: 0
      }
    }
  },
  {
    providerID: '0x3',
    countryCode: 'us',
    countryName: 'United States',
    metrics: {
      connectCount: {
        success: 0,
        fail: 0,
        timeout: 0
      }
    }
  },
  {
    providerID: '0x4',
    countryCode: 'gb',
    countryName: 'United Kingdom',
    metrics: {
      connectCount: {
        success: 0,
        fail: 0,
        timeout: 0
      }
    }
  },
  {
    providerID: '0x5',
    countryCode: 'it',
    countryName: 'Italy',
    metrics: {
      connectCount: {
        success: 0,
        fail: 0,
        timeout: 0
      }
    }
  },
  {
    providerID: '0x6',
    countryCode: 'it',
    countryName: 'Italy',
    metrics: {
      connectCount: {
        success: 5,
        fail: 3,
        timeout: 2
      }
    }
  },
  {
    providerID: '0x7',
    countryCode: 'it',
    countryName: 'Italy',
    metrics: {
      connectCount: {
        success: 0,
        fail: 0,
        timeout: 0
      }
    }
  },
  {
    providerID: '0x8',
    countryCode: 'al',
    countryName: 'Albania',
    metrics: {
      connectCount: {
        success: 0,
        fail: 0,
        timeout: 0
      }
    }
  }
]

export default proposals
