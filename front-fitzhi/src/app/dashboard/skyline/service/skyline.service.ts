import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Building } from 'rising-skyline';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { switchMap, take } from 'rxjs/operators';
import { Skyline } from 'src/app/data/skyline';
import { traceOn } from 'src/app/global';

const httpOptions = {
	headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
	providedIn: 'root'
})
export class SkylineService {

	/**
	 * Ths skyline is loaded.
	 */
	public skylineLoaded$ = new BehaviorSubject<boolean>(false);

	private skyline$ = new  BehaviorSubject<Building[]>([]);

	constructor(private httpClient: HttpClient) { }

	/**
	 * Load the skyline from the Fitzhì backend.
	 * @returns an observable of buildings
	 */
	public loadSkyline$(): Observable<Building[]> {
		return this.httpClient
			.get<Skyline>(localStorage.getItem('backendUrl') + '/api/skyline', httpOptions)
			.pipe(
				take(1),
				switchMap( skyline => {
					if (traceOn()) {
						console.log ('Receiving skykine', skyline);
					}
					console.log (skyline.floors[0]);
					return this.skyline$;
				}));
	}

}
