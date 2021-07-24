import { Injectable } from '@angular/core';
import { ProjectService } from 'src/app/service/project/project.service';
import { stringify } from 'querystring';

@Injectable({
	providedIn: 'root'
})
export class SunburstCacheService {

	constructor(private projectService: ProjectService) { }

	/**
	 * Construct & return the key od the cache entry associated for this chart.
	 */
	private keySessionStorage(): string {
		return 'sunburstDashboard-' + this.projectService.project.id;
	}

	/**
	 * Save the server response inside the sessionStorage.
	 * @param response the server response containing the chart data
	 */
	public saveResponse (response: any) {
		sessionStorage.setItem(this.keySessionStorage(), JSON.stringify(response));
	}

	/**
	 * Returns *true* if this chart data has already been saved for this project
	 */
	public hasResponse(): boolean {
		return (sessionStorage.getItem(this.keySessionStorage()) !== null);
	}

	/**
	 * Returns the cache data chart saved in the sessionStorage.
	 */
	public getReponse(): any {
		const json = sessionStorage.getItem(this.keySessionStorage());
		return JSON.parse(json);
	}

	/**
	 * Remove the entry key for the current project.
	 */
	public clearReponse() {
		sessionStorage.removeItem(this.keySessionStorage());
	}
}
