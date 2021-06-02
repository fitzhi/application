// This file is required by karma.conf.js and loads recursively all the .spec and framework files

import 'zone.js/dist/zone-testing';
import { getTestBed } from '@angular/core/testing';
import {
	BrowserDynamicTestingModule,
	platformBrowserDynamicTesting
} from '@angular/platform-browser-dynamic/testing';

declare const require: any;

// First, initialize the Angular testing environment.
getTestBed().initTestEnvironment(
	BrowserDynamicTestingModule,
	platformBrowserDynamicTesting()
);
// Then we find all the tests
const context = require.context('./', true, /\.spec\.ts$/);
// const context = require.context('./', true,  /ssewatcher\.service\.spec\.ts$/);
// const context = require.context('./', true,  /http-refresh-token-error-interceptor\.spec\.ts$/);
// const context = require.context('./', true,  /project-audit\.service\.spec\.ts$/);
// const context = require.context('./', true,  /project-audit-no-project\.component\.spec\.ts$/);
// const context = require.context('./', true,  /app\.component\.search\.spec\.ts$/);
// const context = require.context('./', true,  /auth\.service\.spec\.ts$/);
// const context = require.context('./', true,  /audit-attachment\.service\.spec\.ts$/);
// And load the modules.
context.keys().map(context);