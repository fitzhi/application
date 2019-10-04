import { Injectable } from '@angular/core';
import { Settings } from '../data/settings';
import { switchMap, map, catchError, take, tap } from 'rxjs/operators';
import { HttpClient, HttpHeaders, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { InternalService } from '../internal-service';
import { BackendSetupService } from './backend-setup/backend-setup.service';
import { Constants } from '../constants';
import { Metric } from '../data/sonar/metric';
import { of, BehaviorSubject, Subject, Observable, EMPTY } from 'rxjs';
import { Metrics } from '../data/sonar/metrics';
import { Components } from '../data/sonar/components';
import { Component } from '../data/sonar/component';
import { SonarProject } from '../data/SonarProject';
import { ResponseComponentMeasures } from '../data/sonar/reponse-component-measures';
import { ComponentTree } from '../data/sonar/component-tree';
import { ILanguageCount } from './ILanguageCount';
import { SupportedMetric } from '../data/supported-metric';
import { ReferentialService } from './referential.service';

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
	public sonarIsAccessible$ = new Subject<boolean>();

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

	constructor(
		private httpClient: HttpClient,
		private referentialService: ReferentialService,
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
				tap (
					metrics => {
						if (Constants.DEBUG) {
							console.groupCollapsed(metrics.metrics.length + ' (all) metrics available on Sonar');
							metrics.metrics.forEach(metric => console.log (metric.key, metric.name));
							console.groupEnd();
						}
					}),
				take(1))
			.subscribe(metrics => {
				const sonarMetrics: Metric[]  = [];
				metrics.metrics.forEach(element => {
					if (supported.includes(element.key)) {
						sonarMetrics.push(element);
					}
				});
				if (Constants.DEBUG) {
					console.groupCollapsed(sonarMetrics.length + ' suported metrics by the application');
					sonarMetrics.forEach(metric => console.log (metric.key, metric.name));
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
		const params = new HttpParams().set('component', key).set('metricKeys', metrics.join(''));
		return this.httpClient
			.get<ResponseComponentMeasures>(this.urlSonar + '/api/measures/component', {params: params})
			.pipe(
				tap ( response => {
					if (Constants.DEBUG) {
						console.groupCollapsed(response.component.measures.length + ' measures obtained for component ' + response.component.key);
						response.component.measures.forEach(measure => console.log (measure.metric, measure.value));
						console.groupEnd();
					}
			}));
	}

	loadSonarVersion() {
		this.httpClient
			.get(this.backendSetupService.url() + '/admin/settings')
				.pipe(switchMap( (settings: Settings) => {
					return this.httpClient
						.get(settings.urlSonar + '/api/server/version',
								{responseType: 'text' as 'json'})
						.pipe (
							map( (version: string) => {
								return {
									settings: settings,
									sonarOn: true,
									version: version
								};
							}),
							catchError( (error)  => {
								console.log ('error', error);
								return of( {settings: settings, sonarOn: false, version: ''});
							})
					); }))
				.subscribe(
					(data: any) => {
						this.sonarVersion = data.version;
						this.urlSonar =  data.settings.urlSonar;
						this.sonarOn = data.sonarOn;
						this.sonarIsAccessible$.next(this.sonarOn);
						if (this.sonarOn) {
							console.log('Sonar version ' + data.version + ' installed at the URL ' + data.settings.urlSonar);
						} else {
							console.log('Sonar is OFFLINE  at the URL ' + data.settings.urlSonar);
						}
					},
					error => console.log (error)
				);
	}

	/**
	 * Load the projects declared on the Sonar instance.
	 */
	loadProjects () {
		this.loadComponents('TRK').subscribe ( components => {
				if (Constants.DEBUG) {
					console.groupCollapsed(components.components.length + ' components retrieved.');
					components.components.forEach(component => console.log (component.name, component.key));
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
	loadComponents (type: string): Observable<Components> {
		const params = new HttpParams().set('qualifiers', type).set('ps', '500');
		return this.httpClient
			.get<Components>(this.urlSonar + '/api/components/search', {params})
			.pipe(take(1));
	}

	/**
	 * Search the sonar project
	 * @param sonarProject the Sonar project name
	 */
	search (nameOfSonarProject: string): SonarProject {
		if (this.allSonarProjects.length === 0) {
			console.error('the array containing all projects declared in Sonar is empty');
			return undefined;
		}
		return this.allSonarProjects.find (sp => sp.name === nameOfSonarProject);
	}

	loadFiles(key: string): Observable<ILanguageCount> {
		const params = new HttpParams().set('component', key).set('qualifiers', 'FIL').set('ps', '500');
		return this.httpClient
			.get<ComponentTree>(this.urlSonar + '/api/components/tree', {params: params})
			.pipe(
				tap ( (response: ComponentTree) => {
					if (Constants.DEBUG) {
						console.groupCollapsed(response.components.length + ' FIL components retrieved');
						response.components.forEach(
							component => console.log (component.language + ' ' + component.name));
						console.groupEnd();
					}
			}),
			switchMap ( (response: ComponentTree) => {
				const languageCounts: ILanguageCount = {};
				response.components.forEach(element => {
					if (!languageCounts[element.language]) {
						languageCounts[element.language] = 1;
					} else {
						languageCounts[element.language]++;
					}
				});
				if (Constants.DEBUG) {
					console.groupCollapsed ( key + ' files summary');
					Object.entries(languageCounts).forEach( ([language, count]) => {
						console.log(language, count);
					});
					console.groupEnd ();
				}
				return of(languageCounts);
			}), catchError( (error)  => {
				console.error ('error', error);
				return of({});
			})
		);
	}


}
