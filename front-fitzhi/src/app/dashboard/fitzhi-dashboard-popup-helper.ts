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

}
