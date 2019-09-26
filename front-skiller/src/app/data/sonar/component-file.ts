import { Measure } from './measure';

/**
 * This class is a unit representation retrived
 * from call '/api/measures/component_tree?metricKeys=files'
 */
export class ComponentFile {
	id: string;
	key: string;
	name: string;
	path: string;
	language: string;
	project: string;
	measures: Measure[];
}
