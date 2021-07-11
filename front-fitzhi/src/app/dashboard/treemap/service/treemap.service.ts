import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { TreemapFilter as filter } from './treemapFilter';
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
	public treemapFilter: filter = {external: true, level: 0};

	/**
	 * This observable indicates that the user has updated the filter.
	 *
	 * The treemap has to be redrawn.
	 */
	public filterUpdated$ = new BehaviorSubject<boolean>(true);

	/**
	 * Build and return the filter tag of the component.
	 */
	public buildTag(): TagStar {
		return new TagStar(TreemapService.TAG_LABEL, this.treemapFilter.level);
	}
}
