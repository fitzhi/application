import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { take } from 'rxjs/operators';
import { Skyline } from 'src/app/data/Skyline';
import { traceOn } from 'src/app/global';

const httpOptions = {
	headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
	providedIn: 'root'
})
export class SkylineService {


	constructor(private httpClient: HttpClient) { }

	loadSkykine() {
		return this.httpClient.get<Skyline>(localStorage.getItem('backendUrl') + '/api/skyline', httpOptions)
			.pipe(take(1))
			.subscribe({
				next: skyline => {
					if (traceOn()) {
						console.log ('Receibing skykine', skyline);
					}
				}
			});
	}

}
