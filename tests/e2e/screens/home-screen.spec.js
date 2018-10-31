describe('HomeScreen', () => {
  beforeEach(async () => {
    await device.reloadReactNative()
  })

  it('Should show IP updating status', async () => {
    await expect(await element(by.text('IP: updating...'))).toExist()
  })
})
