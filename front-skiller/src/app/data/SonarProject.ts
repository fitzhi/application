import { FilesStats } from './sonar/FilesStats';

export class SonarProject {
	key: string;
	name: string;
	projectFilesStats?: FilesStats[];
}
