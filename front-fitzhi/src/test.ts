// This file is required by karma.conf.js and loads recursively all the .spec and framework files
import 'zone.js/testing';
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
// const context = require.context('./', true,  /alternative-openid-connection-google-github\.component\.spec\.ts$/);

// const context = require.context('./', true,  /auth\.service\.spec\.ts$/);
// const context = require.context('./', true,  /register-user\.component\.spec\.ts$/);
// const context = require.context('./', true,  /callback-github-connect-1\.component\.spec\.ts$/);
// const context = require.context('./', true,  /callback-github-connect-2\.component\.spec\.ts$/);
// const context = require.context('./', true,  /register-user-single-oauth\.component\.spec\.ts$/);
// const context = require.context('./', true,  /staff-form.component\.createStaff\.spec\.ts$/);
// const context = require.context('./', true,  /alternative-openid-connection-google-github\.component\.spec\.ts$/);

// const context = require.context('./', true,  /github\.service\.spec\.ts$/);
// const context = require.context('./', true,  /github\.service\.spec\.ts$/);
// const context = require.context('./', true,  /auth\.service\.spec\.ts$/);
// const context = require.context('./', true,  /register-user-multi-oauth\.component\.spec\.ts$/)
// const context = require.context('./', true,  /app\.component\.spec\.ts$/);
// const context = require.context('./', true,  /http-refresh-token-error-interceptor\.component\.spec\.ts$/);
// const context = require.context('./', true,  /project-inactivate\.component\.spec\.ts$/);
// const context = require.context('./', true,  /connect-user.component\.spec\.ts$/);
// const context = require.context('./', true,  /http-refresh-token-error-interceptor\.spec\.ts$/);
// const context = require.context('./', true,  /project-appropriateDistribution\.service\.spec\.ts$/);
// const context = require.context('./', true,  /skill\.service\.spec\.ts$/);
// const context = require.context('./', true,  /project-calculateSonarEvaluation\.service\.spec\.ts$/);
// const context = require.context('./', true,  /summary\.component\.spec\.ts$/);
// const context = require.context('./', true,  /treemap-skills\.component\.spec\.ts$/);
// const context = require.context('./', true,  /project-sunburst-with-data\.component\.spec\.ts$/);
// const context = require.context('./', true,  /referential\.service\.spec\.ts$/);
// const context = require.context('./', true,  /project-actualizeProject\.service\.spec\.ts$/);
// const context = require.context('./', true,  /starting-setup\.component\.spec\.ts$/);
// const context = require.context('./', true,  /project-sunburst-with-data\.component\.spec\.ts$/);
// const context = require.context('./', true,  /fitzhi-dashboard-help-popper-treemap-skills\.component\.spec\.ts$/);
// const context = require.context('./', true,  /project-sunburst-reload-button\.component\.spec\.ts$/);

// const context = require.context('./', true,  /dashboard\.service\.globalScoreSkillDistribution\.spec\.ts$/);
// And load the modules.
context.keys().map(context);
