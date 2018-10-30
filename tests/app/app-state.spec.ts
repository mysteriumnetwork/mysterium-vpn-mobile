import AppState from '../../src/app/app-state'

describe('AppState', () => {
  let appState: AppState

  beforeEach(() => {
    appState = new AppState()
  })

  describe('.Error', () => {
    it('returns undefined initially', () => {
      expect(appState.Error).toBeUndefined()
    })
  })

  describe('.showError', () => {
    it('changes Error', () => {
      appState.showError('New error')
      expect(appState.Error).toEqual('New error')
    })
  })
})
