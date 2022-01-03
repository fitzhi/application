import { ProjectSonarMetricValue } from './project-sonar-metric-value';
import { SonarEvaluation } from './sonar-evaluation';
import { FilesStats } from './sonar/FilesStats';

export class SonarProject {
	key: string;
	name: string;
	sonarEvaluation: SonarEvaluation;
	projectSonarMetricValues?: ProjectSonarMetricValue[];
	projectFilesStats?: FilesStats[];
}
