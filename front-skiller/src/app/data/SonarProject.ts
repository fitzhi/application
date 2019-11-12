import { FilesStats } from './sonar/FilesStats';
import { ProjectSonarMetric } from './sonar/project-sonar-metric';
import { ProjectSonarMetricValue } from './project-sonar-metric-value';
import { SonarEvaluation } from './sonar-evaluation';

export class SonarProject {
	key: string;
	name: string;
	sonarEvaluation: SonarEvaluation;
	projectSonarMetricValues?: ProjectSonarMetricValue[];
	projectFilesStats?: FilesStats[];
}
