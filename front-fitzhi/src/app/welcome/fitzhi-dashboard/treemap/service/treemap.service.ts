import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { TreemapFilter } from './treemapFilter';
import { TagStar } from 'src/app/tabs-staff/staff-form/tag-star';
/**
 * Service in charge the cinematic exchange between the tremap-chart & the treemap-header.
 */
@Injectable({
	providedIn: 'root'
})
export class TreemapService {

	public static TAG_LABEL = 'Minimal level :';

	/**
	 * Filter necessaries to process the chart.
	 */
	treemapFilter: TreemapFilter = {external: true, level: 0};

	/**
	 * This observable indicates that the user wants to filter the treemap chart with external staff members, or not.
	 *
	 * - if **true**, the external developers will take part in the scope of the chart.
	 * - if **false**, _ONLY internal_ developers will take part in the chart.
	 */
	public filter$ = new BehaviorSubject<TreemapFilter>(this.treemapFilter);

	/**
	 * Build and return the filter tag of the component.
	 */
	public buildTag(): TagStar {
		return new TagStar(TreemapService.TAG_LABEL, this.treemapFilter.level);
	}
}
