import { Paging } from './paging';
import { ComponentFile } from './component-file';

export class ComponentTree {
	paging: Paging;
	baseComponent: ComponentFile;
	components: ComponentFile[];
}
