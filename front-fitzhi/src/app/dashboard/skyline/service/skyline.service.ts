import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { take } from 'rxjs/operators';

@Injectable({
	providedIn: 'root'
})
export class SkylineService {

	const httpOptions = {
		headers: new HttpHeaders({ 'Content-Type': 'application/json' })
	};

	constructor(private httpClient: HttpClient) { }

	loadSkykine() {
		return this.httpClient.get<Token>(
			localStorage.getItem('backendUrl') + '/oauth/token')
			.pipe(take(1))
			.subscribe({

			});
	}

}
