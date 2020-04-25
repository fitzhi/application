import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
/**
 * Service in charge the cinematic exchange between the tremap-chart & the treemap-header.
 */
@Injectable({
	providedIn: 'root'
})
export class TreemapService {

	/**
	 * This observable indicates that the user wants to filter the treemap chart with external staff members, or not.
	 *
	 * - if **true**, the external developers will take part in the scope of the chart.
	 * - if **false**, _ONLY internal_ developers will take part in the chart.
	 */
	public external$ = new BehaviorSubject<boolean>(true);

	/**
	 * This observable indicates the level number of developers for each skill to be part of the chart.
	 *
	 * If this number is equal to 0, all developers are involved in chart.
	 */
	public level$ = new BehaviorSubject<number>(0);

}
