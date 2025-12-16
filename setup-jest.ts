// Use new recommended setup API from jest-preset-angular
import { setupZoneTestEnv } from 'jest-preset-angular/setup-env/zone';
setupZoneTestEnv();

// Polyfills / globals for tests can be added here if needed
// Example: define window.matchMedia in older environments
// (global as any).matchMedia = (query: string) => ({ matches: false, addListener: () => {}, removeListener: () => {} });
