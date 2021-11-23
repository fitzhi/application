import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { TreemapSkillsFilter as filter } from './treemap-skills-filter';
import { TagStar } from 'src/app/tabs-staff/staff-form/tag-star';

/**
 * Service in charge the cinematic exchange between the tremap-chart & the treemap-header.
 */
@Injectable({
	providedIn: 'root'
})
export class TreemapSkillsService {

	public static TAG_LABEL = 'Minimal level :';

	private displaySettingsSubject$ = new BehaviorSubject<boolean>(false);

	public displaySettings$ = this.displaySettingsSubject$.asObservable();

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
		return new TagStar(TreemapSkillsService.TAG_LABEL, this.treemapFilter.level);
	}

	displaySettings(show: boolean) {
		this.displaySettingsSubject$.next(show);
	}
}
