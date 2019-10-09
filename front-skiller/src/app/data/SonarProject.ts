import { FilesStats } from './sonar/FilesStats';
import { ProjectSonarMetric } from './sonar/project-sonar-metric';
import { ProjectSonarMetricValue } from './project-sonar-metric-value';

export class SonarProject {
	key: string;
	name: string;
	projectSonarMetricValues?: ProjectSonarMetricValue[];
	projectFilesStats?: FilesStats[];
}
