module.exports = {
  preset: 'react-native',
  "transformIgnorePatterns": [
    "node_modules/(?!react-native|native-base|native-base-shoutem-theme|react-navigation)"
  ],
  setupFiles: [
    "<rootDir>/jest.setup.js"
  ],
  globals: {
    __DEV__: true
  },
  moduleFileExtensions: [
    "ts",
    "tsx",
    "js"
  ],
  transform: {
    "^.+\\.(js)$": "<rootDir>/node_modules/babel-jest",
    "\\.(ts|tsx)$": "<rootDir>/node_modules/ts-jest/preprocessor.js"
  },
  testRegex: "(/*\\.(test|spec))\\.(ts|tsx|js)$",
  testPathIgnorePatterns: [
    "\\.snap$",
    "<rootDir>/node_modules/",
    "<rootDir>/lib/",
    "<rootDir>/tests/e2e/"
  ],
  cacheDirectory: ".jest/cache"
}
