import { Injectable } from '@angular/core';
import { Skill } from './data/skill';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, map, tap} from 'rxjs/operators';

import {Observable, of} from 'rxjs';
import {InternalService} from './internal-service';

const httpOptions = {
  headers: new HttpHeaders({'Content-Type': 'application/json'})
};

@Injectable({
  providedIn: 'root'
})
export class SkillService extends InternalService {

	private skillUrl = 'api/skill';  // URL to web api

	constructor(private httpClient: HttpClient) { super(); }
  
  /**
  * Save the skill
  */
  save(skill: Skill) {
  	if (skill.id == null) {
  		this.add (skill);
  	}
  }

  /** POST: add a new skill to the server */
  add(newSkill: Skill): Observable<Skill> {
    return this.httpClient.post<Skill>(this.skillUrl, newSkill, httpOptions).pipe(
      tap( (skill: Skill) =>
        this.log(`added skill w/ id=${skill.id}`)),
      catchError(this.handleError<Skill>('addSkill'))
    );
  }


}
