import { Injectable } from '@angular/core';
import { Settings } from '../data/settings';
import { switchMap, map, catchError, take, tap, retry } from 'rxjs/operators';
import { HttpClient, HttpHeaders, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { InternalService } from '../internal-service';
import { BackendSetupService } from './backend-setup/backend-setup.service';
import { Constants } from '../constants';
import { Metric } from '../data/sonar/metric';
import { of, BehaviorSubject, Subject, Observable, EMPTY } from 'rxjs';
import { Metrics } from '../data/sonar/metrics';
import { Components } from '../data/sonar/components';
import { Component } from '../data/sonar/component';
import { ResponseComponentMeasures } from '../data/sonar/reponse-component-measures';
import { ComponentTree } from '../data/sonar/component-tree';
import { ILanguageCount } from './ILanguageCount';
import { ReferentialService } from './referential.service';
import { ProjectSonarMetric } from '../data/sonar/project-sonar-metric';
import { Project } from '../data/project';
import { ProjectService } from './project.service';
import { ɵangular_packages_platform_browser_platform_browser_j } from '@angular/platform-browser';

@Injectable({
	providedIn: 'root'
})
export class SonarService extends InternalService {

	/**
	 * Version of Sonar.
	 */
	sonarVersion: string;

	/**
	 * URL of Sonar.
	 */
	urlSonar: string;

	/**
	 * TRUE if we cannot access the SONAR;
	 */
	sonarOn = false;

	/**
	 * This observable inform the application is SONAR is accessible.
	 */
	public sonarIsAccessible$ = new BehaviorSubject<boolean>(false);

	/**
	 * This observable provide all projects declared on Sonar.
	 */
	public allSonarProjects$ = new BehaviorSubject<Component[]>([]);

	/**
	 * This observable provide all metrics declared on Sonar.
	 */
	public sonarMetrics$ = new BehaviorSubject<Metric[]>([]);

	/**
	 * List of all Sonar projects retrieved from the server.
	 */
	allSonarProjects: Component[] = [];

	public CALCULATION_RULES = {
		'bugs':
			'This is a ONE or ZERO rule :' +
			'\nEither Sonar reveals a bug and the project is getting 0% of its note (dura lex, sed lex). ' +
			'\nOr the project is simply perfect and deserves 100% of the note.',
		'code_smells':
			'The "code smell" quotation is calculated as follow :\n' +
			'Note = Max(100-ceil(code_smells/5)*10, 0).' +
			'The ceil function of x is the smallest integral value that is not less than x. ',
		'coverage':
			'Test coverage is a percentage. Our evaluation will reproduce that rate.' +
			'So we presume that it will be difficut to reach a note of 100%.',
		'duplicated_lines_density':
			'Duplication is a percentage. Our evaluation will substract this number from 100% to get ths metric evaluation.',
		'sqale_index':
			'We reproduce the Sonar range rating in a numeric way :\n' +
			'- a rating of 100%, if <=5% of the time that has already gone into the application\n' +
			'- a rating of 80%, if between 6 to 10%\n' +
			'- a rating of 60%, if between 11 to 20%\n' +
			'- a rating of 40%, if between 21 to 50%\n' +
			'- a rating of 20%, anything over 50%',
		'security_rating':
			'We reproduce the Sonar security rating in a numeric way :\n' +
			'Can the current code be exploited by hackers?\n' +
			'- a rating of 100%, if no vulnerability has been detected\n' +
			'- a rating of 80%, if at least 1 Minor Vulnerability has been detected\n' +
			'- a rating of 60%, if at least 1 Major Vulnerability has been detected\n' +
			'- a rating of 40%, if at least 1 Critical Vulnerability has been detected\n' +
			'- a rating of 20%, if at least 1 Blocker Vulnerability has been detected',
		'reliability_rating':
			'We reproduce the Sonar reliability rating in a numeric way :\n' +
			'- a rating of 100%, if no Bug has been detected\n' +
			'- a rating of 80%, if at least 1 Minor Bug has been detected\n' +
			'- a rating of 60%, if at least 1 Major Bug has been detected\n' +
			'- a rating of 40%, if at least 1 Critical Bug has been detected\n' +
			'- a rating of 20%, if at least 1 Blocker Bug has been detected',
		'sqale_rating':
			'We evaluate the technical debt as follow\n' +
			'   This evaluation is absolute and not related to the size of the project.\n' +
			'   (Use the metric \'Maintainability Rating\' for a relative evaluation)\n' +
			'- no more than 1 hour of debt : 100%\n' +
			'- from 1 hour to 1 day : 90%\n' +
			'- from 1 day to 3 days : 50%\n' +
			'- from 3 days to 1 week : 10%\n' +
			'- More than 1 WEEK : 0% (are-you kidding me?)',
		'alert_status':
			'This is a binary metric (Yes/No, One/Zero) :\n' +
			'- If the quality gate passed, you get the whole note : 100%\n' +
			'- If the quality gate failed, you get a zero',
	};

	/**
	 * Sonar metrics currently available inside our application.
	 */
	projectSonarMetrics: ProjectSonarMetric[] = [];

	constructor(
		private httpClient: HttpClient,
		private referentialService: ReferentialService,
		private projectService: ProjectService,
		private backendSetupService: BackendSetupService) {
		super();
	}

	loadSonarSupportedMetrics() {
		this.referentialService.supportedMetrics$.subscribe(
			supported => this.loadSonarMetrics(supported));
	}

	loadSonarMetrics(supported: string[]) {
		this.httpClient.get<Metrics>(this.urlSonar + '/api/metrics/search?ps=500')
			.pipe(
				tap(
					metrics => {
						if (Constants.DEBUG) {
							console.groupCollapsed(metrics.metrics.length + ' (all) metrics available on Sonar');
							metrics.metrics.forEach(metric => console.log(metric.key, metric.name));
							console.groupEnd();
						}
					}),
				take(1))
			.subscribe(metrics => {
				const sonarMetrics: Metric[] = [];
				metrics.metrics.forEach(element => {
					if (supported.includes(element.key)) {
						sonarMetrics.push(element);
					}
				});
				if (Constants.DEBUG) {
					console.groupCollapsed(sonarMetrics.length + ' supported metrics by the application');
					sonarMetrics.forEach(metric => console.log(metric.key, metric.name));
					console.groupEnd();
				}
				this.sonarMetrics$.next(sonarMetrics);
			});
	}

	/**
	 * Load the measures evaluated for a component.
	 * @param key the key of the evaluated componsent
	 * @param metrics list of metrics to be evaluated
	 */
	loadSonarComponentMeasures(key: string, metrics: string[]): Observable<ResponseComponentMeasures> {
		const params = new HttpParams().set('component', key).set('metricKeys', metrics.join(','));
		const apiMesures = '/api/measures/component';
		return this.httpClient
			.get<ResponseComponentMeasures>(this.urlSonar + apiMesures, { params: params })
			.pipe(
				tap(response => {
					if (Constants.DEBUG) {
						console.groupCollapsed(response.component.measures.length + ' measures obtained for component ' + response.component.key);
						response.component.measures.forEach(measure => console.log(measure.metric, measure.value));
						console.groupEnd();
					}
				}));
	}

	loadSonarVersion() {
		this.httpClient
			.get(this.backendSetupService.url() + '/admin/settings')
			.pipe(
				switchMap((settings: Settings) => {
					return this.httpClient
						.get(settings.urlSonar + '/api/server/version',
							{ responseType: 'text' as 'json' })
						.pipe(
							map((version: string) => {
								return {
									settings: settings,
									sonarOn: true,
									version: version
								};
							}),
							catchError((error) => {
								console.log('error', error);
								return of({ settings: settings, sonarOn: false, version: '' });
							})
						);
				}))
			.subscribe(
				(data: any) => {
					this.sonarVersion = data.version;
					this.urlSonar = data.settings.urlSonar;
					this.sonarOn = data.sonarOn;
					this.sonarIsAccessible$.next(this.sonarOn);
					if (this.sonarOn) {
						console.log('Sonar version ' + data.version + ' installed at the URL ' + data.settings.urlSonar);
					} else {
						console.log('Sonar is OFFLINE  at the URL ' + data.settings.urlSonar);
					}
				},
				error => console.log(error)
			);
	}

	/**
	 * Load the projects declared on the Sonar instance.
	 */
	loadProjects() {
		this.loadComponents('TRK').subscribe(components => {
			if (Constants.DEBUG) {
				console.groupCollapsed(components.components.length + ' components retrieved.');
				components.components.forEach(component => console.log(component.name, component.key));
				console.groupEnd();
			}
			this.allSonarProjects = components.components;
			this.allSonarProjects$.next(components.components);
		});
	}

	/**
	 * Load the components filtered on a passed type.
	 * @param type the given type.
	 */
	loadComponents(type: string): Observable<Components> {
		const params = new HttpParams().set('qualifiers', type).set('ps', '500');
		return this.httpClient
			.get<Components>(this.urlSonar + '/api/components/search', { params })
			.pipe(take(1));
	}

	/**
	 * Search the sonar project
	 * @param sonarProject the Sonar project name
	 */
	search(nameOfSonarProject: string): Component {
		if (this.allSonarProjects.length === 0) {
			console.error('the array containing all projects declared in Sonar is empty');
			return undefined;
		}
		return this.allSonarProjects.find(sp => sp.name === nameOfSonarProject);
	}

	/**
	 * Load the badge for the given metric.
	 * @param key the Sonar key project
	 * @param metric the current metric
	 */
	loadBadge(key: string, metric: string): Observable<string> {
		const params = new HttpParams().set('metric', metric).set('project', key);
		return this.httpClient
			.get<string>(this.urlSonar + '/api/project_badges/measure',
				{ params: params, responseType: 'text' as 'json' });
	}

	loadFiles(key: string): Observable<ILanguageCount> {
		const params = new HttpParams().set('component', key).set('qualifiers', 'FIL').set('ps', '500');
		return this.httpClient
			.get<ComponentTree>(this.urlSonar + '/api/components/tree', { params: params })
			.pipe(
				tap((response: ComponentTree) => {
					if (Constants.DEBUG) {
						console.groupCollapsed(response.components.length + ' FIL components retrieved');
						response.components.forEach(
							component => console.log(component.language + ' ' + component.name));
						console.groupEnd();
					}
				}),
				switchMap((response: ComponentTree) => {
					const languageCounts: ILanguageCount = {};
					response.components.forEach(element => {
						if (!languageCounts[element.language]) {
							languageCounts[element.language] = 1;
						} else {
							languageCounts[element.language]++;
						}
					});
					if (Constants.DEBUG) {
						console.groupCollapsed(key + ' files summary');
						Object.entries(languageCounts).forEach(([language, count]) => {
							console.log(language, count);
						});
						console.groupEnd();
					}
					return of(languageCounts);
				}), catchError((error) => {
					console.error('error', error);
					return of({});
				})
			);
	}

	/**
	 * set the Project available Sonar metrics.
	 * @param projectSonarMetrics  the given Sonar metrics
	 */
	setProjectSonarMetrics(projectSonarMetrics: ProjectSonarMetric[]) {
		this.projectSonarMetrics = projectSonarMetrics;
	}

	evaluateSonarProject(project: Project, sonarKey: string): number {
		const sonarProject = this.projectService.getSonarProject(project, sonarKey);
		let result = 0;
		sonarProject.projectSonarMetricValues.forEach(metricValues => {
			if (metricValues.weight) {
				switch (metricValues.key) {
					case 'bugs':
						result += (metricValues.value) ? 0 : metricValues.weight;
						break;
					case 'code_smells':
						result += metricValues.weight * Math.max(100 - Math.ceil(metricValues.value / 5) * 10, 0) / 100;
						break;
					case 'coverage':
						result += metricValues.weight * metricValues.value;
						break;
					case 'duplicated_lines_density':
						result += metricValues.weight * (1 - metricValues.value / 100);
						break;
					case 'sqale_index':
						if (metricValues.value < 60) {
							result += metricValues.weight;
						} else {
							if (metricValues.value < 1400) {
								result += metricValues.weight * 0.9;
							} else if (metricValues.value < 4200) {
								result += metricValues.weight * 0.5;
							} else {
								if (metricValues.value < 9800) {
									result += metricValues.weight * 0.1;
								}
							}
						}
						break;
					case 'sqale_rating':
						result += metricValues.weight * metricValues.value;
						break;
					case 'security_rating':
						result += metricValues.weight * metricValues.value;
						break;
					case 'reliability_rating':
						result += metricValues.weight * metricValues.value;
						break;
					case 'alert_status':
						result += metricValues.weight * metricValues.value;
						break;
					default:
						throw new Error('Unknown metric key ' + metricValues.key);
				}
			}
			console.log ('%s %d %d = %d', metricValues.key, metricValues.weight, metricValues.value,  result);
		});
		return result;
	}
}
