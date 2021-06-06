import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

/**
 * This service is declared for testing purpose.
 */
@Injectable()
export class DataService {
	public ROOT_URL = `http://jsonplaceholder.typicode.com`;

	constructor(private http: HttpClient) {}

	getPosts() {
		console.log ('url', `${this.ROOT_URL}/posts`);
		return this.http.get<any[]>(`${this.ROOT_URL}/posts`);
	}
}