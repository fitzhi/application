import { Measure } from './measure';

/**
* Buffer response for the URL /api/measures/component
*/
export class ComponentMeasures {
	id: string;
	key: string;
	name: string;
	qualifier: string;
	measures: Measure[];
}
