/**
 * This class is used to propagate the Panel and the Sonar project selected.
 */
export class PanelSwitchEvent {

	constructor(
		public idPanel: number,
		public keySonar: string) {}
}
