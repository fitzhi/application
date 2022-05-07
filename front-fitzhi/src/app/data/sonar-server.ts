import { BehaviorSubject } from 'rxjs';
import { traceOn } from '../global';
import { Component } from './sonar/component';
import { Metric } from './sonar/metric';
import { ProjectSonarMetric } from './sonar/project-sonar-metric';
import { SonarProject } from './sonar-project';

/**
 * This class represents a Sonar server available in the infrastructure.
 */
export class SonarServer {

	/**
	 * Construction of the SonarServer object, representing a Sonar server available from the desktop point of view.
	 * @param sonarVersion the version of the Sonar
	 * @param urlSonar the Sonar URL
	 * @param sonarOn `TRUE` if we cannot access the SONAR
	 * @param user the user to be used for connection
	 * @param password the password associated to this user
	 * @param login the login token as a replacement of the login user/password.
	 */
	constructor(
		public sonarVersion: string,
		public urlSonar: string,
		public sonarOn = false,
		public organization?: string,
		public user?: string,
		public password?: string,
		public login?: string) {}

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

	/**s
	 * Process the Sonar evaluation for the selected metrics.
	 * @param sonarProject the given Sonar project
	 * @returns an evaluation of the Sonar project on a base of 100.
	 */
	evaluateSonarProject(sonarProject: SonarProject): number {
		let result = 0;
		if (!sonarProject.projectSonarMetricValues) {
			if (traceOn()) {
				console.log ('WTF Sonar project %s without metrics', sonarProject.name);
			}
			return 0;
		}

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
