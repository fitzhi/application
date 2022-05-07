import { SonarServer } from './sonar-server';
import { SonarProject } from './sonar-project';


describe('Testing the SonarServer object', () => {

	it('should be created without error.', () => {
		const sonarServer = new SonarServer('6.4', 'urlSonar', true, 'theOrganization')
		expect (sonarServer.login).toBeUndefined();
		expect (sonarServer.user).toBeUndefined();
		expect (sonarServer.password).toBeUndefined();
	});

	it('should evaluate the project to 0 if no metric has been gathered.', () => {
		const sonarServer = new SonarServer('6.4', 'urlSonar', true, 'theOrganization')
		const sonarProject = new SonarProject ();
		expect (sonarServer.evaluateSonarProject(sonarProject)).toBe(0);
	});

});
