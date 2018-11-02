describe('HomeScreen', () => {
  beforeEach(async () => {
    await device.reloadReactNative()
  })

  it('should show IP updating status', () => {
    expect(element(by.text('IP: updating...'))).toExist()
  })
})
