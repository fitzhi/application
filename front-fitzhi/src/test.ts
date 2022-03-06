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

// const context = require.context('./', true,  /git\.service\.spec\.ts$/);
// const context = require.context('./', true,  /http-refresh-token-error-interceptor\.spec\.ts$/);
// const context = require.context('./', true,  /starfield-header\.component\.spec\.ts$/);
// const context = require.context('./', true,  /dashboard\.service\.calculateGeneralAverage\.spec\.ts$/);
// const context = require.context('./', true,  /turnover\.service\.spec\.ts$/);
// const context = require.context('./', true,  /starfield-broadcastConstellations\.service\.spec\.ts$/);
// const context = require.context('./', true,  /backend-setup\.component\.spec\.ts$/);
// const context = require.context('./', true,  /app\.component\.spec\.ts$/);
// const context = require.context('./', true,  /treemap-skills-chart\.component\.spec\.ts$/);
// const context = require.context('./', true,  /project-inactivate\.component\.spec\.ts$/);
// const context = require.context('./', true,  /project-staff\.component\.spec\.ts$/);
// const context = require.context('./', true,  /http-refresh-token-error-interceptor\.spec\.ts$/);
// const context = require.context('./', true,  /project-appropriateDistribution\.service\.spec\.ts$/);
// const context = require.context('./', true,  /skill\.service\.spec\.ts$/);
// const context = require.context('./', true,  /project-calculateSonarEvaluation\.service\.spec\.ts$/);
// const context = require.context('./', true,  /summary\.component\.spec\.ts$/);
// const context = require.context('./', true,  /treemap-skills\.component\.spec\.ts$/);
// const context = require.context('./', true,  /project-sunburst-with-data\.component\.spec\.ts$/);
// const context = require.context('./', true,  /sonar-initSonarServer\$\.service\.spec\.ts$/);
// const context = require.context('./', true,  /project-actualizeProject\.service\.spec\.ts$/);
// const context = require.context('./', true,  /treemap-projects-chart-2\.component\.spec\.ts$/);
// const context = require.context('./', true,  /project-sunburst-with-data\.component\.spec\.ts$/);
// const context = require.context('./', true,  /fitzhi-dashboard-help-popper-treemap-skills\.component\.spec\.ts$/);
// const context = require.context('./', true,  /dashboardService-testingComponentFor-colorTile\.component\.spec\.ts$/);
// const context = require.context('./', true,  /dashboard\.service\.globalScoreSkillDistribution\.spec\.ts$/);
// And load the modules.
context.keys().map(context);
