import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { traceOn } from 'src/app/global';

/**
 * This service is handling the fact that the installation on this desktop has been completed.
 */
@Injectable({
	providedIn: 'root'
})
export class InstallService {

	private installCompleteSubject$ = new BehaviorSubject<boolean>(false);

	/**
	 * This `observable` will emit a **true** if the installation has been completed.
	 */
	public installComplete$ = this.installCompleteSubject$.asObservable();

	/**
	 * Are we in the very first connection ?
	 */
	private veryFirstConnectionSubject$ = new Subject<boolean>();

	/**
	 * Are we in the very first connection ?
	 */
	public veryFirstConnection$ = this.veryFirstConnectionSubject$.asObservable();

	constructor() {
		this.installCompleteSubject$.next(('1' === localStorage.getItem('installation')));
	}

	/**
	 * Save the fact that the install is complete.
	 */
	public installComplete() {
		localStorage.setItem('installation', '1');
		this.installCompleteSubject$.next(true);
	}

	/**
	 * Simulate tthe fact that the installation is undone.
	 *
	 * This method has been created for testing purpose.
	 */
	public uninstall() {
		localStorage.clear();
		this.installCompleteSubject$.next(false);
	}

	/**
	 * @returns **TRUE** is the installation is complete, **FALSE** otherwise.
	 */
	public isComplete() {
		return (localStorage.getItem('installation') === '1');
	}

	/**
	 * @param veryFirstConnection a boolean representing the fact that we are currenty installing Fitzhi for the first time.
	 */
	public setVeryFirstConnection(veryFirstConnection: boolean) {
		if (traceOn()) {
			console.log ('setVeryFirstConnection(%s)', veryFirstConnection);
		}
		localStorage.setItem('firstInstallation', veryFirstConnection ? '1' : '0');
		this.veryFirstConnectionSubject$.next(veryFirstConnection);
	}

	/**
	 * @returns **TRUE** is this is the very first installation, _(this is the very first user connecting into Fitzhi)_,
	 * **FALSE** otherwise _(Fitzhi is already installed. This is just a new user.)_.
	 */
	public isVeryFirstInstall() {
		return (localStorage.getItem('firstInstallation') === '1');
	}

}
