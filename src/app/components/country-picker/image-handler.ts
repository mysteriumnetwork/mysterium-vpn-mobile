import countries from './countries.json'

const getCountryImageFile = (code: string) => {
  if (countries[code]) {
    return countries[code].image
  }

  return ''
}

export {
  getCountryImageFile
}
