import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class BackendSetupService {

    /**
     * Default URL for a local directory.
     */
    private defaultUrl = 'http://localhost:8080';

    /**
     * Current registered URL.
     */
    private currentUrl = null;

    constructor() {
        this.currentUrl = localStorage.getItem('backendUrl');
    }

    /**
     * @return the back-end URL
     */
    public url() {
        return (this.currentUrl === null) ? this.defaultUrl : this.currentUrl;
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
