describe('HomeScreen', () => {
  beforeEach(async () => {
    await device.reloadReactNative()
  })

  it('Should show IP updating status', () => {
    expect(element(by.text('IP: updating...'))).toExist()
  })
})
