module.exports = {
  preset: 'react-native',
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
