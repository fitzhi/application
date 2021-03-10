import { Injectable } from '@angular/core';
import { Settings } from '../data/settings';
import { switchMap, map, catchError, take, tap, retry, takeUntil } from 'rxjs/operators';
import { HttpClient, HttpHeaders, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { InternalService } from '../internal-service';
import { BackendSetupService } from './backend-setup/backend-setup.service';
import { Constants } from '../constants';
import { Metric } from '../data/sonar/metric';
import { of, BehaviorSubject, Subject, Observable, EMPTY, pipe } from 'rxjs';
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
import { SonarServer } from '../data/sonar-server';
import { MessageService } from '../interaction/message/message.service';
import { SupportedMetric } from '../data/supported-metric';
import { RegisterUserComponent } from '../admin/register-user/register-user.component';
import { DeclaredSonarServer } from '../data/declared-sonar-server';
import { traceOn } from '../global';

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
	 * Load the Sonar versions of ALL Sonar servers.
	 */
	loadSonarsVersion() {
		this.referentialService.sonarServers$
			.pipe(switchMap( (declaredSonarServers: DeclaredSonarServer[]) => of(...declaredSonarServers)))
			.subscribe({
				next: declaredSonarServer => this.initSonarServer(declaredSonarServer.urlSonarServer),
				complete: () => {
					this.reportSituationSonars();
					this.allSonarServersLoaded$.next(true);
				}
			});
	}

	/**
	 * Report the situations for all Sonar servers declared in Fitzhi.
	 */
	private reportSituationSonars(): void {
		this.sonarServers.forEach((sonarServer: SonarServer) => {
			if (sonarServer.sonarOn) {
				console.log ("%s of version %s is ON", sonarServer.urlSonar, sonarServer.sonarVersion)
			} else {
				console.log ("%s is OF", sonarServer.urlSonar, sonarServer.sonarVersion)
			}
		})
	}

	/**
	 * Initialize the `SonarServer` object for future usage, and tests its avaibility by retrieving its version.
	 * @param urlSonar the URL of the Sonar server
	 */
	private initSonarServer(urlSonar: string) {
		if (traceOn()) {
			console.log ('initSonarServer(\'%s\')', urlSonar);
		}

		this.httpClient
			.get(urlSonar + '/api/server/version', { responseType: 'text' as 'json' })
				.pipe(
					take(1),
					switchMap((version: string) => {
						const sonarServer = new SonarServer(version, urlSonar, true);
						this.sonarServers.push(sonarServer);
						console.log('Sonar version ' + sonarServer.sonarVersion + ' installed at the URL ' + sonarServer.urlSonar);
						sonarServer.loadProjects(this.httpClient);
						return of({sonarServer: sonarServer, accessible: true});
					}),
					catchError((error) => {
						if (traceOn()) {
							console.groupCollapsed ('Connection failed with the Sonar server %s', urlSonar);
							console.log('Error catched', error);
							console.groupEnd();
						}
						console.log('Sonar is OFFLINE  at the URL ' + urlSonar);
						const sonarServer = new SonarServer('', urlSonar, false);
						this.sonarServers.push(sonarServer);
						return of({sonarServer: sonarServer, accessible: false});
					}))
				.subscribe({
					next: result => result.sonarServer.sonarIsAccessible$.next(result.accessible)
				});
	}

	/**
	 * Load & validate the supported metrics by the Sonar server & Fitzhì.
	 *
	 * ___This operation will be executed when all referential data will be loaded.___
	 */
	public loadSonarMetrics() {

		/**
		 * This observable will emirt `true` when the metrics will be loaded.
		 */
		const metricsLoaded$ = new Subject<boolean>();

		this.referentialService.referentialLoaded$
			.pipe(
				takeUntil(metricsLoaded$),
				switchMap(
					(doneAndOk: boolean) => {
						if (doneAndOk) {
							return this.referentialService.supportedMetrics$;
						} else {
							return EMPTY;
						}
					}))
			.subscribe((supportedMetrics) => {
				this.sonarServers.forEach(sonarServer => {
					if (sonarServer.sonarOn) {
						sonarServer.loadSonarSupportedMetrics(this.httpClient, supportedMetrics);
						metricsLoaded$.next(true);
					} else {
						console.warn('Cannot validate supported metrics for %s', sonarServer.urlSonar);
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
	public loadSonarComponentMeasures$(project: Project, key: string, metrics: string[]): Observable<ResponseComponentMeasures> {
		if (!key) {
			return EMPTY;
		}

		const sonarServer = this.getSonarServer(project);
		if (!sonarServer) {
			return EMPTY;
		}
		return sonarServer.loadSonarComponentMeasures$(this.httpClient, key, metrics);
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
	 * This function emtis all **Sonar** projects declared on Sonar associated with the Fitzhì project.
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
	public loadBadge(project: Project, key: string, metric: string): Observable<string> {
		const sonarServer = this.getSonarServer(project);
		return (sonarServer) ? sonarServer.loadBadge(this.httpClient, key, metric) : of(null);
	}

	/**
	 * Load the total number of lines of code for the given key.
	 * @param project the current project
	 * @param key the Sonar project key
	 */
	public loadTotalNumberLinesOfCode$(project: Project, key: string): Observable<number> {
		const sonarServer = this.getSonarServer(project);
		return (sonarServer) ? sonarServer.loadTotalNumberLinesOfCode$(this.httpClient, key) : of(null);
	}

	/**
	 * Load & count the number of files for the given key.
	 * @param project current project
	 * @param key the type of FILE from which we are agregating data
	 */
	loadFiles(project: Project, key: string): Observable<ILanguageCount> {
		const sonarServer = this.getSonarServer(project);
		return (sonarServer) ? sonarServer.loadFiles(this.httpClient, key) : of(null);
	}
}
