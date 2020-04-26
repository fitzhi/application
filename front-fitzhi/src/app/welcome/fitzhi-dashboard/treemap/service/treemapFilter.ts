export class TreemapFilter {

	/**
	 * This boolean indicates that the user wants to filter the treemap chart with external staff members, or not.
	 *
	 * - if **true**, the external developers will take part in the scope of the chart.
	 * - if **false**, _ONLY internal_ developers will take part in the chart.
	 */
	public external: boolean;

	/**
	 * This number indicates the minimum level of skill required for developers to be part in the chart.
	 *
	 * If this number is equal to 0, all developers are involved in chart.
	 */
	public level: number;

}
