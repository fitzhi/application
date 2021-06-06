/**
 * This class is used to export a User specific setting in the local storage
 */
export class UserSetting {

	/**
	 * Value corresponding to the key
	 */
	public value = 0;

	constructor(
		public key: string,
		public defaultValue: number) {
			this.loadSetting();
	}

	/**
	 * Load the setting from the local storage
	 */
	private loadSetting() {
		if (localStorage.getItem(this.key)) {
			this.value = Number(localStorage.getItem(this.key));
		} else {
			this.value = this.defaultValue;
		}
	}

	/**
	 * Save the setting from the local storage
	 * @param new setting to be saved on the local storage
	 */
	public saveSetting(value: number) {
		localStorage.setItem(this.key, '' + value);
	}
}
