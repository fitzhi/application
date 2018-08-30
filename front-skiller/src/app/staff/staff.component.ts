import {AppModule} from '../app.module';
import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Router} from '@angular/router';
import {Subject, Observable, of } from 'rxjs';
import {catchError} from 'rxjs/operators';
import {HttpResponse} from '@angular/common/http';


import {CinematicService} from '../cinematic.service';
import {DataService} from '../data.service';
import {MessageService} from '../message.service';
import {CollaboratorService} from '../collaborator.service';

import {Collaborator} from '../data/collaborator';

import {Level} from '../data/level';
import {Attribution} from '../data/attribution';
import {Project} from '../data/project';
import {Experience} from '../data/experience';
import {StaffDTO} from '../data/external/staffDTO';

import {FormGroup, FormControl} from '@angular/forms';

import {LIST_OF_LEVELS} from '../data/List_of_levels';
import {PROJECTS} from '../mock/mock-projects';
import {EXPERIENCE} from '../mock/mock-experience';
import {Constants} from '../constants';

import { Ng2SmartTableModule } from 'ng2-smart-table';
import { LocalDataSource } from 'ng2-smart-table';
import {StarsSkillLevelRenderComponent} from './starsSkillLevelRenderComponent';

@Component({
  selector: 'app-staff',
  templateUrl: './staff.component.html',
  styleUrls: ['./staff.component.css']
})
export class StaffComponent implements OnInit {

  private id: number;
  private sub: any;

  private levels: Level[] = LIST_OF_LEVELS;
  private sourceProjects = new LocalDataSource([]);
  private sourceExperience = new LocalDataSource([]);
  private settings_experience = Constants.SETTINGS_EXPERIENCE_SMARTTABLE;
  private settings_projects = Constants.SETTINGS_PROJECTS_SMARTTABLE;

  private collaborator: Collaborator;

  private profileStaff = new FormGroup({
    firstName: new FormControl(''),
    lastName: new FormControl(''),
    nickName: new FormControl(''),
    email: new FormControl(''),
    level: new FormControl('')
  });

  constructor(
    private cinematicService: CinematicService,
    private route: ActivatedRoute,
    private dataService: DataService,
    private messageService: MessageService,
    private collaboratorService: CollaboratorService) {}

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
      this.collaborator = {id: null, firstName: null, lastName: null, nickName: null, email: null, level: null,
        projects: [], experience: []};
      if (this.id != null) {
        this.dataService.getCollaborator(this.id).subscribe(
          (collab: Collaborator) => {
           	this.collaborator = collab;
	        this.profileStaff.get('firstName').setValue(collab.firstName);
	        this.profileStaff.get('lastName').setValue(collab.lastName);
	        this.profileStaff.get('nickName').setValue(collab.nickName);
	        this.profileStaff.get('email').setValue(collab.email);
	        this.profileStaff.get('level').setValue(collab.level);
            this.sourceExperience.load(this.collaborator.experience);
            this.sourceProjects.load(this.collaborator.projects);
           this.cinematicService.setForm(Constants.DEVELOPPERS_CRUD);
          },
          error => {
            if (error.status === 404) {
              if (Constants.DEBUG) {
                console.log ('404 : cannot found a collaborator for the id ' + this.id);
              }
              this.messageService.error('There is no staff member for id ' + this.id);
              this.collaborator = {id: null, firstName: null, lastName: null, nickName: null, email: null, level: null,
                projects: [], experience: []};
            } else {
                console.error (error.message);
            }
          },
          () => {
                    if (this.collaborator.id === 0) {
                      console.log ('No collaborator found for the id ' + this.id);
                    }
                    if (Constants.DEBUG) {
                      console.log('Loading complete for id ' + this.id);
                    }
                  }
            );
      }

      this.sourceProjects.onRemoved().subscribe(element => console.log('Delete project ' + element));
      this.sourceProjects.onAdded().subscribe(element => {
			this.collaboratorService.addProject (this.collaborator.id, element.name).subscribe(
				(staffDTO: StaffDTO) => {
		              if (Constants.DEBUG) {
		                console.log ('404 : cannot found a collaborator for the id ' + this.id);
		              }
		              this.messageService.info(staffDTO.staff.firstName + ' ' + staffDTO.staff.lastName + ' is involved now in project ' + element.name);
				},
	          	response_error => {
		              if (Constants.DEBUG) {
		                console.log ('Error');
		                console.log ('Code ' + response_error.error.code);
		                console.log ('Message ' + response_error.error.message);
		              }
	              this.messageService.error(response_error.error.message);
	            }  
      		);
		});
		this.sourceProjects.onUpdated().subscribe(element => console.log('Update project ' + element));

      this.sourceExperience.onRemoved().subscribe(element => console.log('Delete experience ' + element));
      this.sourceExperience.onAdded().subscribe(element => console.log('Add experience ' + element));
      this.sourceExperience.onUpdated().subscribe(element => console.log('Update experience ' + element));
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

  onConfirmCreateFromProject(event) {
    if (Constants.DEBUG) {
      console.log ('onConfirmCreateFromProject for event : ' + event.newData.name);
    }
    if (this.checkStaffMemberExist(event)) {
		event.confirm.resolve();
    } else {
        event.confirm.reject();
    }
  }

  onConfirmEditFromProject(event) {
    if (Constants.DEBUG) {
      console.log ('onConfirmEditFromProject for event from ' + event.data.name + ' to ' + event.newData.name);
    }
    if (this.checkStaffMemberExist(event)) {
        event.confirm.resolve();
    } else {
        event.confirm.reject();
    }
  }

  onConfirmAddStaffSkill(event) {
    if (Constants.DEBUG) {
      console.log ('onConfirmAddStaffSkill for event ' + event.newData.title);
    }
    if (this.checkStaffMemberExist(event)) {
        event.confirm.resolve();
    } else {
        event.confirm.reject();
    }
  }

  onConfirmEditStaffSkill(event) {
    if (Constants.DEBUG) {
      console.log ('onConfirmEditStaffSkill for event from ' + event.data.name + ' to ' + event.newData.name);
    }
    if (this.checkStaffMemberExist(event)) {
        event.confirm.resolve();
    } else {
        event.confirm.reject();
    }
  }

  checkStaffMemberExist(event): boolean {
    if (this.collaborator.id === null) {
      this.messageService.error('You cannot update a skill, or a project, of an unregistered staff member. '
      + 'Please saved this new member first !');
      return false;
    } else {
      return true;
    }
  }

  onConfirmRemoveFromProject(event) {
    if (!this.checkStaffMemberExist(event)) {
        event.confirm.reject();
        return;
    }
    if (window.confirm('Are you sure you want to remove '
        + this.collaborator.firstName + ' '
        + this.collaborator.lastName
        + ' from the project '
        + event.data['name']
        + '?')) {
        event.confirm.resolve();
      } else {
        event.confirm.reject();
      }
  }

   onConfirmRemoveSkill(event) {
    if (!this.checkStaffMemberExist(event)) {
        event.confirm.reject();
        return;
    }
    if (window.confirm('Are you sure you want to remove the skill '
        + event.data['name'] + 'for '
        + this.collaborator.firstName + ' '
        + this.collaborator.lastName
        + '?')) {
        event.confirm.resolve();
      } else {
        event.confirm.reject();
      }
  }

  /**
	* The Validate Button has been activated
	*/
  save(): void {
  /*
    if (Constants.DEBUG) {
      console.log('Saving data for the collaborator below');
      console.log(this.collaborator);
    }
    this.dataService.saveCollaborator (this.collaborator)
      .subscribe(
        staff => {
          this.collaborator = staff;
          this.messageService.info('Staff member ' + this.collaborator.firstName + ' ' + this.collaborator.lastName + ' saved');
        });
  */
  }
  
  /**
	* The Submit Button has been activated
	*/
  onSubmit(): void {
    if (Constants.DEBUG) {
      console.log('Saving data for the collaborator below');
      console.log(this.collaborator);
    }
    this.collaborator.firstName = this.profileStaff.get('firstName').value;
    this.collaborator.lastName = this.profileStaff.get('lastName').value;
    this.collaborator.nickName = this.profileStaff.get('nickName').value;
    this.collaborator.email = this.profileStaff.get('email').value;
    this.collaborator.level = this.profileStaff.get('level').value;
    
    this.dataService.saveCollaborator (this.collaborator)
      .subscribe(
        staff => {
          this.collaborator = staff;
          this.messageService.info('Staff member ' + this.collaborator.firstName + ' ' + this.collaborator.lastName + ' saved');
        });
  }
}


