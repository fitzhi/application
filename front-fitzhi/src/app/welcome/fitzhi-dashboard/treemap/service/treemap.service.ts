import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { TreemapFilter } from './treemapFilter';
/**
 * Service in charge the cinematic exchange between the tremap-chart & the treemap-header.
 */
@Injectable({
	providedIn: 'root'
})
export class TreemapService {

	/**
	 * Filter necessaries to process the chart.
	 */
	treeMapFilter: TreemapFilter = {external: true, level: 0};

	/**
	 * This observable indicates that the user wants to filter the treemap chart with external staff members, or not.
	 *
	 * - if **true**, the external developers will take part in the scope of the chart.
	 * - if **false**, _ONLY internal_ developers will take part in the chart.
	 */
	public filter$ = new BehaviorSubject<TreemapFilter>(this.treeMapFilter);

}
