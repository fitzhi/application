export enum TagifyEditableState {

	/**
	 * The complete component is READ ONLY.
	 * You cannot :
	 *
	 * - either create or remove a TAG from the component
	 * - or change the active stars for each tag
	 *
	 */
	READ_ONLY = 1,

	/**
	 * The complete component is partialy READ ONLY.
	 *
	 * You are only allowed to change the active stars for each tag
	 */
	STARS_ALLOWED = 2,

	/**
	 * The complete component is completly editable.
	 *
	 * You can add, edit an remove any tag inside the component.
	 */
	ALL_ALLOWED = 3
}
