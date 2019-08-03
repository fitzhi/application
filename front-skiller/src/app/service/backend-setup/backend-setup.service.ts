import { Injectable } from '@angular/core';

@Injectable({
	providedIn: 'root'
})
export class BackendSetupService {

	/**
     * Default URL for a local directory.
     */
	public defaultUrl = 'http://localhost:8080';

	/**
     * Current registered URL.
     */
	private currentUrl = null;

	constructor() {
		this.currentUrl = localStorage.getItem('backendUrl');
	}

	/**
     * A URL has already been saved in the localstorage.
     */
	hasSavedAnUrl() {
		return (this.currentUrl !== null);
	}

	/**
     * @return the back-end URL
     */
	public url() {
		return this.currentUrl;
	}

	/**
     * @param validated URL pointing to the back-end server.
     */
	public saveUrl(url: string) {
		this.currentUrl = url;
		localStorage.setItem('backendUrl', url);
	}

	/**
     * Remove the URL saved inside the localstorage
     */
	public removeUrl() {
		localStorage.removeItem('backendUrl');
	}
}
