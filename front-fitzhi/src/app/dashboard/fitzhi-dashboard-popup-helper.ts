import { selection } from './selection';

/**
 * This class is in charge of handling the display, or not, of the help-poppup
 */
export class FitzhiDashboardPopupHelper {

	public selection = selection;

	/**
	 * Selected button. End-user has moved the curcor on it.
	 *
	 * We uese this property to handle the help bubble display.
	 */
	public activated = this.selection.summary;

	/**
	 * Save the button activated in order to display the associated help-bubble.
	 *
	 * @param button the button which is visited by the end-user mouse.
	 */
	public mouseEnter(button: number) {
		this.activated = button;
	}

	/**
	 * Save the current turnover position for the given year.
	 *
	 * @param year the year associated to the highlighted panel
	 */
	 public mouseTurnoverEnter(year: number) {
		this.activated = this.correspondingSelection(year);
	}

	/**
	 * The end-user button has left a button.
	 *
	 * We've to hide the associated help-bubble.
	 */
	public mouseLeave() {
		this.activated = this.selection.summary;
	}

	/**
	 * Return **true** if the given button is activated
	 * @param button the given button identifier
	 */
	public isButtonActivated(button: number) {
		return (this.activated === button);
	}

	/**
	 * Return **true** if the given button is activated
	 * @param button the given button identifier
	 */
	public isTurnoverActivated(year: number) {
		return (this.activated === this.correspondingSelection(year));
	}

	private correspondingSelection(year: number): number {
		const currentYear = new Date(Date.now()).getFullYear();
		switch (year) {
			case currentYear:
				return selection.turnoverCurrentYear;
			case (currentYear-1):
				return selection.turnoverLastYear;
			case (currentYear-2):
				return selection.turnoverPenultimateYear;
		}
	}

}
