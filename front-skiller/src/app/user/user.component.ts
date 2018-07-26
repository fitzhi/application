import {AppModule} from '../app.module';
import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Router} from '@angular/router';
import { Subject, Observable, of } from 'rxjs';

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
        error => console.log(error),
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
          /*
          .subscribe(
                collab => {
                  foundCollab = collab;
                  if (typeof this.collaborator !== 'undefined') {
                    this.emitActualCollaboratorDisplay.next(id);
                  } else {
                    this.emitActualCollaboratorDisplay.next(undefined);
                  }
                },
                error => console.log (error),
                () => {
                        if (typeof foundCollab !== 'undefined') {
                          console.log ('No collaborator found for the id ' + id);
                          this.collaborator = foundCollab;
                        }
                        if (Constants.DEBUG) {
                          console.log('Loading comlete');
                        }
                      }
                );
           */
  }

  /**
	* The Validate Button has been activated
	*/
  save(): void {
    if (Constants.DEBUG) {
      console.log('Saving data for the collaborater below');
      console.log(this.collaborator);
    }
    this.collaborator.subscribe((collab: Collaborator) => console.log (collab));
//    this.collaborator.subscribe( (collab: Collaborator) => this.dataService.saveCollaborator (collab));
  }

}


