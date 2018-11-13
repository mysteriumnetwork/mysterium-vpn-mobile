module.exports = {
  preset: 'react-native',
  transformIgnorePatterns: [
    'node_modules/(?!native-base|react-native|native-base-shoutem-theme|react-navigation)'
  ],
  setupFiles: [
    '<rootDir>/jest.setup.js'
  ],
  globals: {
    __DEV__: true
  },
  moduleFileExtensions: [
    'ts',
    'tsx',
    'js'
  ],
  transform: {
    '^.+\\.(js)$': '<rootDir>/node_modules/react-native/jest/preprocessor.js',
    '\\.(ts|tsx)$': 'ts-jest'
  },
  testRegex: '(/*\\.(test|spec))\\.(ts|tsx|js)$',
  testPathIgnorePatterns: [
    '\\.snap$',
    '<rootDir>/node_modules/',
    '<rootDir>/lib/',
    '<rootDir>/tests/e2e/'
  ],
  cacheDirectory: '.jest/cache'
}
