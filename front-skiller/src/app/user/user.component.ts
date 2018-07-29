import {AppModule} from '../app.module';
import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Router} from '@angular/router';
import {Subject, Observable, of } from 'rxjs';
import {catchError} from 'rxjs/operators';

import {CinematicService} from '../cinematic.service';
import {Collaborator} from '../data/collaborator';

import {Level} from '../data/level';
import {Attribution} from '../data/attribution';
import {Project} from '../data/project';

import {LIST_OF_LEVELS} from '../data/List_of_levels';
import {PROJECTS} from '../mock/mock-projects';
import {Constants} from '../constants';

import {DataService} from '../data.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {

  private id: number;
  private sub: any;

  private levels: Level[] = LIST_OF_LEVELS;
  private projects: Project[] = PROJECTS;

  private collaborator: Collaborator;

  constructor(
    private cinematicService: CinematicService,
    private route: ActivatedRoute,
    private dataService: DataService) {}

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      if (Constants.DEBUG) {
        console.log('params[\'id\'] ' + params['id']);
      }
      if (params['id'] == null) {
        this.id = null;
      } else {
        this.id = + params['id']; // (+) converts string 'id' to a number
      }

      // Either we are in creation mode, or we load the collaborator from the back-end...
      // We create an empty collaborator until the subscription is complete
      this.collaborator = {id: null, firstName: null, lastName: null, nickName: null, email: null, level: null, projects: []};
      this.dataService.getCollaborator(this.id).subscribe(
        (collab: Collaborator) => this.collaborator = collab,
        error => {
          if (error.status === 404) {
            if (Constants.DEBUG) {
              console.log ('404 : cannot found a collaborator for the id ' + this.id);
            }
            this.collaborator = {id: null, firstName: null, lastName: null, nickName: null, email: null, level: null, projects: []};
          } else {
              console.error (error.message);
          }
        },
        () => {
                  if (this.collaborator.id === 0) {
                    console.log ('No collaborator found for the id ' + this.id);
                  }
                  if (Constants.DEBUG) {
                    console.log('Loading comlete for id ' + this.id);
                  }
                }
          );
    });
    this.cinematicService.setForm(Constants.DEVELOPPERS_CRUD);
  }

/**
 * Handle Http operation that failed.
 * Let the app continue.
 * @param operation - name of the operation that failed
 * @param result - optional value to return as the observable result
 */
  handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

    // TODO: send the error to remote logging infrastructure
    console.error(error); // log to console instead

    // TODO: better job of transforming error for user consumption
    console.log(`${operation} failed: ${error.message}`);

    // Let the app keep running by returning an empty result.
    return of(result as T);

    };
  }

  /**
	* The Validate Button has been activated
	*/
  save(): void {
    if (Constants.DEBUG) {
      console.log('Saving data for the collaborater below');
      console.log(this.collaborator);
    }
    this.dataService.saveCollaborator (this.collaborator)
      .subscribe(staff => this.collaborator = staff);
  }

}


