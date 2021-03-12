import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, EMPTY, forkJoin, Observable, of, Subject } from 'rxjs';
import { catchError, switchMap, take, takeUntil, tap } from 'rxjs/operators';
import { DeclaredSonarServer } from '../data/declared-sonar-server';
import { Project } from '../data/project';
import { SonarServer } from '../data/sonar-server';
import { Component } from '../data/sonar/component';
import { ComponentTree } from '../data/sonar/component-tree';
import { Components } from '../data/sonar/components';
import { Metric } from '../data/sonar/metric';
import { Metrics } from '../data/sonar/metrics';
import { ResponseComponentMeasures } from '../data/sonar/reponse-component-measures';
import { traceOn } from '../global';
import { InternalService } from '../internal-service';
import { ILanguageCount } from './ILanguageCount';
import { ProjectService } from './project.service';
import { ReferentialService } from './referential.service';

@Injectable({
	providedIn: 'root'
})
export class SonarService extends InternalService {

	/**
	 * List of Sonar declared inside the application
	 */
	sonarServers: SonarServer[] = [];

	/**
	 * This `behaviorSubject` informs the system that the array of sonarServers is loaded.
	 */
	allSonarServersLoaded$ = new BehaviorSubject<boolean>(false);

	/**
	 * Active connected Sonar server.
	 * 
	 * _In this first current implementation we assume that there is only **ONE** active Sonar server.
	 * Most often there will be only ONE declared Sonar server. 
	 * Future releases, **'more ambitious'**, might need a collection of active connected Sonar server._
	 *  
	 */
	public activeSonarServer: SonarServer = null;

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
		'sqale_rating':
			'We reproduce the Maintainability Sonar range rating in a numeric way :\n' +
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
		'sqale_index':
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

	constructor(
		private httpClient: HttpClient,
		private projectService: ProjectService,
		private referentialService: ReferentialService) {
		super();
	}

	/**
	 * Test if each declared server is reachable and load their versions.
	 * 
	 * This method load the field **this.sonarServers** with an array of SonarServer
	 */
	loadSonarsVersion() {
		this.referentialService.referentialLoaded$
			.pipe(
				switchMap(doneAndOk => doneAndOk ? this.referentialService.sonarServers$ : EMPTY))
			.pipe(
				switchMap( (declaredSonarServers: DeclaredSonarServer[]) => {
					const checkSonarServers = [];
					declaredSonarServers.forEach( server => 
						checkSonarServers.push(this.initSonarServer$(server)));
					return forkJoin(checkSonarServers);
				})
			)
			.pipe(take(1))
			.subscribe({
				next: (sonarServers: SonarServer[]) => {
					sonarServers.forEach(sonarServer => {
						this.sonarServers.push(sonarServer);
						sonarServer.sonarIsAccessible$.next(sonarServer.sonarOn);
					});
				},
				complete: () => {
					this.reportSituationSonars();
					this.allSonarServersLoaded$.next(true);
				}
			})

	}

	/**
	 * Report the situations for all Sonar servers declared in Fitzhi.
	 */
	private reportSituationSonars(): void {
		console.groupCollapsed('Sonar servers report');
		this.sonarServers.forEach((sonarServer: SonarServer) => {
			if (sonarServer.sonarOn) {
				console.log ("%s of version %s is ON", sonarServer.urlSonar, sonarServer.sonarVersion)
			} else {
				console.log ("%s is OFF", sonarServer.urlSonar)
			}
		});
		console.groupEnd();
	}

	/**
	 * Initialize the `SonarServer` object for future usage, and tests its avaibility by retrieving its version.
	 * 
	 * This method returns an observable emetting a **SonarServer** Object. 
	 * 
	 * @param urlSonar the URL of the Sonar server
	 */
	private initSonarServer$(sonar: DeclaredSonarServer): Observable<SonarServer> {
		if (traceOn()) {
			console.log ('initSonarServer(\'%s\')', sonar.urlSonarServer);
		}

		return this.httpClient
			.get(sonar.urlSonarServer + '/api/server/version', { responseType: 'text' as 'json' })
				.pipe(
					take(1),
					switchMap((version: string) => {
						const sonarServer = new SonarServer(version, sonar.urlSonarServer, true, sonar.user, sonar.password);
						if (traceOn()) {
							console.log('Sonar version ' + sonarServer.sonarVersion + ' installed at the URL ' + sonarServer.urlSonar);
						}
						this.loadProjects(this.httpClient, sonarServer);
						return of(sonarServer);
					}),
					catchError((error) => {
						if (traceOn()) {
							console.groupCollapsed ('Connection failed with the Sonar server %s', sonar.urlSonarServer);
							console.log('Error catched', error);
							console.groupEnd();
						}
						if (traceOn()) {
							console.log('Sonar is OFFLINE  at the URL ' + sonar.urlSonarServer);
						}
						const sonarServer = new SonarServer(undefined, sonar.urlSonarServer, false);
						return of(sonarServer);
					}));
	}

	/**
	 * This is the given Sonar server is registered as active or not.
	 * @param url the Sonar URL
	 * @returns **TRUE** if the Sonar server is identified _REACHABLE_, **FALSE** otherwise
	 */
	public isActive(url: string): boolean {
		if (!url) {
			return false;
		}
		const servers = this.sonarServers.filter(sonarServer => sonarServer.urlSonar === url);
		if (servers && (servers.length != 1)) {
			if (traceOn()) {
				console.log ('WTF !!!', servers);
			}
			return false;
		}
		return servers[0].sonarOn;
	}


	/**
	 * Load & validate the supported metrics by the Sonar server & Fitzhì.
	 *
	 * ___This operation will be executed when all referential data will be loaded.___
	 */
	public loadSonarMetrics() {

		this.referentialService.referentialLoaded$
			.pipe(switchMap(doneAndOk => (doneAndOk) ? this.allSonarServersLoaded$ : EMPTY))
			.pipe(switchMap(doneAndOk => (doneAndOk) ? this.referentialService.supportedMetrics$ : EMPTY))
			.pipe(take(1)).subscribe((supportedMetrics) => {
				this.sonarServers.forEach(sonarServer => {
					if (sonarServer.sonarOn) {
						this.loadSonarSupportedMetrics(this.httpClient, sonarServer, supportedMetrics);
					} else {
						console.log('Cannot validate supported metrics for %s', sonarServer.urlSonar);
					}

			});
		});
	}

	/**
	 * Retrieve the `SonarServer` associated with the given project
	 * @param project the passed project
	 */
	getSonarServer(project: Project): SonarServer {

		const sonarServer = this.sonarServers.find(sonar => sonar.urlSonar === project.urlSonarServer );
		if (!sonarServer) {
			if (traceOn()) {
				console.log('Did not retrieve the Sonar server %s associated with the project %s', project.urlSonarServer, project.name);
			}
			return undefined;
		}
		return sonarServer;
	}

	/**
	 * Load the measures evaluated for a component.
	 * @param project Fitzhì project.
	 * @param key the key of the evaluated Sonar project
	 * @param metrics list of metrics to be evaluated
	 */
	public loadProjectSonarComponentMeasures$(project: Project, key: string, metrics: string[]): Observable<ResponseComponentMeasures> {
		if (!key) {
			return EMPTY;
		}

		const sonarServer = this.getSonarServer(project);
		if (!sonarServer) {
			return EMPTY;
		}
		return this.loadSonarComponentMeasures$(this.httpClient, sonarServer.urlSonar, key, metrics);
	}

	/**
	 * Process the Sonar evaluation for the selected metrics.
	 * @param project the current active project.
	 * @param sonarKey the Sonar key identifier on the Sonar server.
	 * @returns an evaluation of the Sonar project on a base of 100, **or -1 if an error occurs during the evaluation**
	 */
	evaluateSonarProject(project: Project, sonarKey: string): number {
		const sonarServer = this.getSonarServer(project);
		if (sonarServer) {
			const sonarProject = this.projectService.getSonarProject(project, sonarKey);
			return sonarServer.evaluateSonarProject(sonarProject);
		}
		return -1;
	}

	/**
	 * Return a `behaviorSubject` informing the application if Sonar server is available and accessible.
	 * @param project the current active project
	 */
	public sonarIsAccessible$(project: Project): Observable<boolean> {
		if ((!project)  || (!project.urlSonarServer)) {
			return of(false);
		}
		const sonarServer = this.getSonarServer(project);
		return (!sonarServer) ? of(false) : sonarServer.sonarIsAccessible$;
	}

	/**
	 * This function emits all **Sonar** projects declared on Sonar associated with the Fitzhì project.
	 */
	public allSonarProjects$(project: Project):  Observable<Component[]> {
		const sonarServer = this.getSonarServer(project);
		return (!sonarServer) ? of([]) : sonarServer.allSonarProjects$;
	}

	/**
	 * Search the Sonar project.
	 *
	 * @param project the current project
	 * @param sonarProject the Sonar project name
	 */
	public search(project: Project, nameOfSonarProject: string): Component {

		const sonarServer = this.getSonarServer(project);
		if (!sonarServer) {
			return undefined;
		}

		if (sonarServer.allSonarProjects.length === 0) {
			console.error('the array containing all projects declared in Sonar is empty');
			return undefined;
		}

		return sonarServer.allSonarProjects.find(sp => sp.name === nameOfSonarProject);
	}

	/**
	 * Load the badge for the given metric.
	 * @param project current project
	 * @param key the Sonar key project
	 * @param metric the current metric
	 */
	public loadProjectBadge$(project: Project, key: string, metric: string): Observable<string> {
		const sonarServer = this.getSonarServer(project);
		return (sonarServer) ? this.loadBadge(this.httpClient, sonarServer.urlSonar, key, metric) : of(null);
	}

	/**
	 * Load the total number of lines of code for the given key.
	 * @param project the current project
	 * @param key the Sonar project key
	 */
	public loadProjectTotalNumberLinesOfCode$(project: Project, key: string): Observable<number> {
		const sonarServer = this.getSonarServer(project);
		return (sonarServer) ? this.loadTotalNumberLinesOfCode$(this.httpClient, sonarServer.urlSonar, key) : of(null);
	}

	/**
	 * Load & count the number of files for the given key.
	 * @param project current project
	 * @param key the type of FILE from which we are agregating data
	 */
	loadProjectFiles(project: Project, key: string): Observable<ILanguageCount> {
		const sonarServer = this.getSonarServer(project);
		return (sonarServer) ? this.loadFiles(this.httpClient, sonarServer.urlSonar, key) : of(null);
	}

	/**
	 * Load the supported metrics of this Sonar server, which are supported by the application.
	 * @param sonarServer The Sonar server object
	 * @param httpClient HTTP client for retrieving data from the Sonar server.
	 * @param applicationSupportedMetrics Array of Sonar metrics supported by Fitzhì.
	 */
	 loadSonarSupportedMetrics(httpClient: HttpClient, sonarServer: SonarServer, applicationSupportedMetrics: string[]) {
		httpClient.get<Metrics>(sonarServer.urlSonar + '/api/metrics/search?ps=500')
			.pipe(
				tap(
					metrics => {
						if (traceOn()) {
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
				if (traceOn()) {
					console.groupCollapsed(sonarMetrics.length + ' supported metrics by the application');
					sonarMetrics.forEach(metric => console.log(metric.key, metric.name));
					console.groupEnd();
				}
				sonarServer.sonarMetrics$.next(sonarMetrics);
			});
	}

	/**
	 * Load the measures evaluated for a component.
	 * @param httpClient HTTP client for retrieving data from the Sonar server.
	 * @param urlSonar The Sonar URL to access
	 * @param key the key of the evaluated Sonar project
	 * @param metrics list of metrics to be evaluated
	 */
	loadSonarComponentMeasures$(httpClient: HttpClient, urlSonar: string, key: string, metrics: string[]): Observable<ResponseComponentMeasures> {
		if (traceOn()) {
			console.log('Loading mesures for Sonar project %s', key);
		}
		const params = new HttpParams().set('component', key).set('metricKeys', metrics.join(','));
		const apiMesures = '/api/measures/component';
		return httpClient
			.get<ResponseComponentMeasures>(urlSonar + apiMesures, { params: params })
			.pipe(
				tap(response => {
					if (traceOn()) {
						console.groupCollapsed(response.component.measures.length + ' measures obtained for component ' + response.component.key);
						response.component.measures.forEach(measure => console.log(measure.metric, measure.value));
						console.groupEnd();
					}
				}));
	}

	/**
	 * Load the total number of code
	 * @param httpClient HTTP client for gathering data from the Sonar server.
	 * @param urlSonar The Sonar URL to access
	 * @param key the Sonar project key
	 * @returns an observable emiting the total number of lines of code in this project
	 */
	public loadTotalNumberLinesOfCode$(httpClient: HttpClient, urlSonar: string, key: string): Observable<number> {
		return this.loadSonarComponentMeasures$(httpClient, urlSonar, key, ['ncloc']).
			pipe(
				switchMap( (response: ResponseComponentMeasures) => {
					if ((!response.component) || (!response.component.measures) || (response.component.measures.length === 0)) {
						return of(0);
					}
					return of(Number(response.component.measures[0].value));
				})
			);
	}

	/**
	 * Load the components filtered on a passed type.
	 * @param httpClient HTTP client for gathering data from the Sonar server.
	 * @param urlSonar The Sonar URL to access
	 * @param type the given type.
	 */
	loadComponents$(httpClient: HttpClient, urlSonar: string, type: string): Observable<Components> {
		const params = new HttpParams().set('qualifiers', type).set('ps', '500');
		return httpClient
			.get<Components>(urlSonar + '/api/components/search', { params })
			.pipe(take(1));
	}

	/**
	 * Load the projects declared on the Sonar instance.
	 * @param httpClient HTTP client for gathering data from the Sonar server.
	 * @param sonarServer the Sonar server whose projects we are looking for
	 */
	 loadProjects(httpClient: HttpClient, sonarServer: SonarServer) {

		this.connectSonar$(sonarServer).pipe(
			switchMap(
				(doneAndOk: boolean) => {
					if (doneAndOk) {
						return this.loadComponents$(httpClient, sonarServer.urlSonar, 'TRK');
					} else {
						return EMPTY;
					}
				}
			)
		).subscribe({
			next: components => {
				if (traceOn()) {
					console.groupCollapsed(components.components.length + ' components retrieved.');
					components.components.forEach(component => console.log(component.name, component.key));
					console.groupEnd();
				}
				sonarServer.allSonarProjects = components.components;
				sonarServer.allSonarProjects$.next(components.components);
			}
		});
	}

	/**
	 * Authenticate the current user to given Sonar if it's necessary or possible.
	 * @param sonarServer the Sonar server whose projects we are looking for
	 */
	 connectSonar$(sonarServer: SonarServer): Observable<boolean> {

		if (traceOn()) {
			console.log ('connectSonar$ (%s)', sonarServer.urlSonar);
		}
		if (!sonarServer.sonarOn) {
			return of(false);
		}

		if ((this.activeSonarServer) && (this.activeSonarServer.urlSonar === sonarServer.urlSonar)) {
			return of(true);
		}

		if ( (!this.activeSonarServer) || (this.activeSonarServer.urlSonar !== sonarServer.urlSonar) ) {
			// If not user provided for this sonar server,
			// we assume that this Sonar server is a publinc unsecured server.
			// We do not memorize this Sonar server because no authentification has been done.
			if (!sonarServer.user) {
				return of(true);
			} else {
				return this.authenticate$(sonarServer.urlSonar, sonarServer.user, sonarServer.password)
					.pipe(tap(r => this.activeSonarServer = sonarServer));
			}
		}
		return of(true);
	}

	private authenticate$(urlSonar:string, user: string, password: string) {

		if (traceOn()) {
			console.log ('Trying to Authenticate to %s', urlSonar);
		}
		let params = new HttpParams()
			.set('login', user)
			.set('password', password);

		return this.httpClient
			.post<any>(urlSonar + '/api/authentication/login', '', {responseType: 'text' as 'json',  params: params})
			.pipe(
				take(1),
				switchMap(r => of(true)),
				catchError( error => of(false))
		);
	
	}

	/**
	 * Load the badge for the given metric.
	 * @param httpClient HTTP client for gathering data from the Sonar server.
	 * @param urlSonar The Sonar URL to access
	 * @param key the Sonar key project
	 * @param metric the current metric
	 */
	 loadBadge(httpClient: HttpClient, urlSonar: string, key: string, metric: string): Observable<string> {
		const params = new HttpParams().set('metric', metric).set('project', key);
		return httpClient
			.get<string>(urlSonar + '/api/project_badges/measure',
				{ params: params, responseType: 'text' as 'json' });
	}

	/**
	 * Load & count the number of files for the given key.
	 * @param httpClient HTTP client for gathering data from the Sonar server.
	 * @param urlSonar The Sonar URL to access
	 * @param key the type of FILE from which we are agregating data
	 */
	loadFiles(httpClient: HttpClient, urlSonar: string, key: string): Observable<ILanguageCount> {
		const params = new HttpParams().set('component', key).set('qualifiers', 'FIL').set('ps', '500');
		return httpClient
			.get<ComponentTree>(urlSonar + '/api/components/tree', { params: params })
			.pipe(
				tap((response: ComponentTree) => {
					if (traceOn()) {
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
					if (traceOn()) {
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



}
