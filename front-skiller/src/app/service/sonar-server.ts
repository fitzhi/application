import { BehaviorSubject, Observable, of } from 'rxjs';
import { Metric } from '../data/sonar/metric';
import { take, switchMap, tap, catchError } from 'rxjs/operators';
import { ResponseComponentMeasures } from '../data/sonar/reponse-component-measures';
import { HttpParams, HttpClient } from '@angular/common/http';
import { Constants } from '../constants';
import { Metrics } from '../data/sonar/metrics';
import { Components } from '../data/sonar/components';
import { Component } from '../data/sonar/component';
import { ILanguageCount } from './ILanguageCount';
import { ComponentTree } from '../data/sonar/component-tree';
import { ProjectSonarMetric } from '../data/sonar/project-sonar-metric';
import { Project } from '../data/project';
import { SonarProject } from '../data/SonarProject';

/**
 * This class represents a Sonar server available in the infrastructure.
 */
export class SonarServer {

	/**
	 * Construction of the SonarServer object, representing a Sonar server available from the desktop point of view.
	 * @param sonarVersion the version of the Sonar
	 * @param urlSonar the Sonar URL
	 * @param sonarOn `TRUE` if we cannot access the SONAR;
	 */
	constructor(
		public sonarVersion: string,
		public urlSonar: string,
		public sonarOn = false) {}

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

	/**
	 * Sonar metrics currently available inside our application.
	 */
	projectSonarMetrics: ProjectSonarMetric[] = [];

	/**
	 * Load the supported metrics of this Sonar server, which are supported by the application.
	 * @param httpClient HTTP client for retrieving data from the Sonar server.
	 * @param applicationSupportedMetrics Array of Sonar metrics supported by techxhì.
	 */
	loadSonarSupportedMetrics(httpClient: HttpClient, applicationSupportedMetrics: string[]) {
		httpClient.get<Metrics>(this.urlSonar + '/api/metrics/search?ps=500')
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
					if (applicationSupportedMetrics.includes(element.key)) {
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
	 * @param httpClient HTTP client for retrieving data from the Sonar server.
	 * @param key the key of the evaluated Sonar project
	 * @param metrics list of metrics to be evaluated
	 */
	loadSonarComponentMeasures$(httpClient: HttpClient, key: string, metrics: string[]): Observable<ResponseComponentMeasures> {
		const params = new HttpParams().set('component', key).set('metricKeys', metrics.join(','));
		const apiMesures = '/api/measures/component';
		return httpClient
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

	/**
	 * Load the total number of code
	 * @param httpClient HTTP client for gathering data from the Sonar server.
	 * @param key the Sonar project key
	 * @returns an observable emiting the total number of lines of code in this project
	 */
	public loadTotalNumberLinesOfCode$(httpClient: HttpClient, key: string): Observable<number> {
		return this.loadSonarComponentMeasures$(httpClient, key, ['ncloc']).
			pipe(
				take(1),
				switchMap( (response: ResponseComponentMeasures) => {
					return of(Number(response.component.measures[0].value));
				})
			);
	}

	/**
	 * Load the projects declared on the Sonar instance.
	 * @param httpClient HTTP client for gathering data from the Sonar server.
	 */
	loadProjects(httpClient: HttpClient) {
		this.loadComponents(httpClient, 'TRK').subscribe(components => {
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
	 * @param httpClient HTTP client for gathering data from the Sonar server.
	 * @param type the given type.
	 */
	loadComponents(httpClient: HttpClient, type: string): Observable<Components> {
		const params = new HttpParams().set('qualifiers', type).set('ps', '500');
		return httpClient
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
	 * @param httpClient HTTP client for gathering data from the Sonar server.
	 * @param key the Sonar key project
	 * @param metric the current metric
	 */
	loadBadge(httpClient: HttpClient, key: string, metric: string): Observable<string> {
		const params = new HttpParams().set('metric', metric).set('project', key);
		return httpClient
			.get<string>(this.urlSonar + '/api/project_badges/measure',
				{ params: params, responseType: 'text' as 'json' });
	}

	/**
	 * Load & count the number of files for the given key.
	 * @param httpClient HTTP client for gathering data from the Sonar server.
	 * @param key the type of FILE from which we are agregating data
	 */
	loadFiles(httpClient: HttpClient, key: string): Observable<ILanguageCount> {
		const params = new HttpParams().set('component', key).set('qualifiers', 'FIL').set('ps', '500');
		return httpClient
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

	/**
	 * @param key the given metric key
	 * @returns the explicite titile for this key, or null if the projectSonarMetrics is not already initialized.
	 */
	getMetricTitle(key: string) {
		if ((this.projectSonarMetrics) && (this.projectSonarMetrics.length > 0)) {
			const psm = this.projectSonarMetrics.find(metric => (metric.key === key));
			if (!psm) {
				throw new Error('Cannot retrieved the metric ' + key + ' inside the collection');
			}
			return psm.name;
		}
		return null;
	}

	/**
	 * Process the Sonar evaluation for the selected metrics.
	 * @param sonarProject the given Sonar project
	 * @returns an evaluation of the Sonar project on a base of 100.
	 */
	evaluateSonarProject(sonarProject: SonarProject): number {
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
							if (metricValues.value < 480) {
								result += metricValues.weight * 0.9;
							} else if (metricValues.value < 1440) {
								result += metricValues.weight * 0.5;
							} else {
								if (metricValues.value < 3360) {
									result += metricValues.weight * 0.1;
								}
							}
						}
						break;
					case 'sqale_rating':
						// A=0-0.05, B=0.06-0.1, C=0.11-0.20, D=0.21-0.5, E=0.51-1
						// During first initialization, the value has not yet been retrieved from the Sonar server
						if (metricValues.value !== 0) {
							result += metricValues.weight *
								(((6 - metricValues.value) * 20) / 100);
						}
						break;
					case 'security_rating':
						/*
						Security Rating (security_rating)
							1 = 0 Vulnerabilities
							2 = at least 1 Minor Vulnerability
							3 = at least 1 Major Vulnerability
							4 = at least 1 Critical Vulnerability
							5 = at least 1 Blocker Vulnerability
						*/
						// During first initialization, the value has not yet been retrieved from the Sonar server
						if (metricValues.value !== 0) {
							result += metricValues.weight *
							(((6 - metricValues.value) * 20) / 100);
						}
						break;
					case 'reliability_rating':
						// During first initialization, the value has not yet been retrieved from the Sonar server
						if (metricValues.value !== 0) {
							result += metricValues.weight *
							(((6 - metricValues.value) * 20) / 100);
						}
						break;
					case 'alert_status':
						result += metricValues.weight * metricValues.value;
						break;
					default:
						throw new Error('Unknown metric key ' + metricValues.key);
				}
			}
		});
		return result;
	}

}
