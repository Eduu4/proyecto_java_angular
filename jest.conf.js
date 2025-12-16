const { pathsToModuleNameMapper } = require('ts-jest');

const {
  compilerOptions: { paths = {}, baseUrl = './' },
} = require('./tsconfig.json');

module.exports = {
  preset: 'jest-preset-angular',
  setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
  transform: {
    // Use babel-jest to transform .mjs ESM files from node_modules (Angular uses .mjs)
    '^.+\\.mjs$': 'babel-jest',
    // Note: HTML templates are inlined via the ts-jest astTransformers config
  },
  // allow transforming specific node_modules packages (Angular, rxjs, dayjs)
  transformIgnorePatterns: ['node_modules/(?!(?:@angular|rxjs|dayjs)/)'],
  testEnvironment: 'jest-environment-jsdom',
  // '.mjs' files are treated as ESM by default; no need to add extensionsToTreatAsEsm
  resolver: 'jest-preset-angular/build/resolvers/ng-jest-resolver.js',
  globals: {
    __VERSION__: 'test',
  },
  roots: ['<rootDir>', `<rootDir>/${baseUrl}`],
  modulePaths: [`<rootDir>/${baseUrl}`],
  setupFiles: ['jest-date-mock'],
  cacheDirectory: '<rootDir>/target/jest-cache',
  coverageDirectory: '<rootDir>/target/test-results/',
  moduleNameMapper: Object.assign(
    {
      '^app/(.*)$': `<rootDir>/${baseUrl}app/$1`,
    },
    pathsToModuleNameMapper(paths, { prefix: `<rootDir>/${baseUrl}/` }),
  ),
  reporters: [
    'default',
    ['jest-junit', { outputDirectory: '<rootDir>/target/test-results/', outputName: 'TESTS-results-jest.xml' }],
    ['jest-sonar', { outputDirectory: './target/test-results/jest', outputName: 'TESTS-results-sonar.xml' }],
  ],
  testMatch: ['<rootDir>/src/main/webapp/app/**/@(*.)@(spec.ts)'],
  testEnvironmentOptions: {
    url: 'https://jhipster.tech',
  },
};
