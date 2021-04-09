import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

/**
 * This service is declared for testing purpose.
 */
@Injectable()
export class DataService {
  static ROOT_URL = `http://jsonplaceholder.typicode.com`;

  constructor(private http: HttpClient) {}

  getPosts() {
    return this.http.get<string[]>(`${DataService.ROOT_URL}/posts`);
  }
}