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
// const context = require.context('./', true,  /list-10-contributors\.component\.spec\.ts$/);
// const context = require.context('./', true,  /list-filenames\.component\.spec\.ts$/);
// const context = require.context('./', true,  /node-detail\.component\.spec\.ts$/);
// const context = require.context('./', true,  /list-projects\.service\.spec\.ts$/);
// And load the modules.
context.keys().map(context);