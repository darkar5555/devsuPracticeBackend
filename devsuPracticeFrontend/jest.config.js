const { createCjsPreset } = require('jest-preset-angular/presets');

module.exports = {
  ...createCjsPreset(),
  testEnvironment: 'jest-preset-angular/environments/jest-jsdom-env',
  setupFilesAfterEnv: ['<rootDir>/setup-jest.ts'],
};
