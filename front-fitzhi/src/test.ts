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
// const context = require.context('./', true,  /starfield\.service\.spec\.ts$/);
// const context = require.context('./', true,  /starfield\.component\.spec\.ts$/);
// const context = require.context('./', true,  /starfield-header\.component\.spec\.ts$/);
// const context = require.context('./', true,  /treemap-skills\.component\.spec\.ts$/);
// const context = require.context('./', true,  /project-sunburst-with-data\.component\.spec\.ts$/);
// const context = require.context('./', true,  /pie-chart-display\.component\.spec\.ts$/);
// const context = require.context('./', true,  /http-refresh-token-error-interceptor\.spec\.ts$/);
// const context = require.context('./', true,  /project-appropriateDistribution\.service\.spec\.ts$/);
// const context = require.context('./', true,  /staff-list\.reloadStaff\.service\.spec\.ts$/);
// const context = require.context('./', true,  /project-calculateSonarEvaluation\.service\.spec\.ts$/);
// const context = require.context('./', true,  /treemap-skills\.component\.spec\.ts$/);
// const context = require.context('./', true,  /treemap-skills\.component\.spec\.ts$/);
// const context = require.context('./', true,  /project-sunburst-with-data\.component\.spec\.ts$/);
// const context = require.context('./', true,  /sonar-initSonarServer\$\.service\.spec\.ts$/);
// const context = require.context('./', true,  /project-hasBeenEvaluated\.service\.spec\.ts$/);
// const context = require.context('./', true,  /treemap-projects-chart\.component\.spec\.ts$/);
// const context = require.context('./', true,  /table-projects-filter-1\.component\.spec\.ts$/);
// const context = require.context('./', true,  /table-projects-filter-2\.component\.spec\.ts$/);
// const context = require.context('./', true,  /treemap-projects\.service\.spec\.ts$/);
// const context = require.context('./', true,  /dashboard\.service\.processSkillDistributionFilesSize\.spec\.ts$/);
// And load the modules.
context.keys().map(context);
