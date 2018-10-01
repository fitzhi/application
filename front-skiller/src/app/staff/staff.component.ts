import {AppModule} from '../app.module';
import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {Router} from '@angular/router';
import {Subject, Observable, of} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {HttpResponse} from '@angular/common/http';


import {CinematicService} from '../cinematic.service';
import {MessageService} from '../message.service';
import {StaffService} from '../staff.service';
import {ProjectService} from '../project.service';
import {SkillService} from '../skill.service';

import {Collaborator} from '../data/collaborator';

import {Level} from '../data/level';
import {Attribution} from '../data/attribution';
import {Project} from '../data/project';
import {Experience} from '../data/experience';
import {StaffDTO} from '../data/external/staffDTO';

import {FormGroup, FormControl, Validators} from '@angular/forms';

import {LIST_OF_LEVELS} from '../data/List_of_levels';
import {PROJECTS} from '../mock/mock-projects';
import {EXPERIENCE} from '../mock/mock-experience';
import {Constants} from '../constants';
import {Profile} from '../data/profile';
import {ListStaffService} from '../list-staff-service/list-staff.service';
import {ReferentialService} from '../referential.service';
import { StaffDataExchangeService } from './service/staff-data-exchange.service';

import {Ng2SmartTableModule} from 'ng2-smart-table';
import {LocalDataSource} from 'ng2-smart-table';
import {StarsSkillLevelRenderComponent} from './starsSkillLevelRenderComponent';
import {Ng2SmartTableComponent} from 'ng2-smart-table/ng2-smart-table.component';

@Component({
  selector: 'app-staff',
  templateUrl: './staff.component.html',
  styleUrls: ['./staff.component.css']
})
export class StaffComponent implements OnInit {

  /**
   * Staff member identifier shared with the child components (staffTabs, StaffForm)
   */
  private idStaff: number;
  private sub: any;

  private sourceProjects = new LocalDataSource([]);
  private sourceExperience = new LocalDataSource([]);
  private settings_experience = Constants.SETTINGS_EXPERIENCE_SMARTTABLE;
  private settings_projects = Constants.SETTINGS_PROJECTS_SMARTTABLE;

  private collaborator: Collaborator;

  constructor(
    private cinematicService: CinematicService,
    private route: ActivatedRoute,
    private listStaffService: ListStaffService,
    private messageService: MessageService,
    private staffService: StaffService,
    private projectService: ProjectService,
    private referentialService: ReferentialService,
    private skillService: SkillService,
    private staffDataExchangeService: StaffDataExchangeService) {}

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      if (Constants.DEBUG) {
        console.log('params[\'id\'] ' + params['id']);
      }
      if (params['id'] == null) {
        this.idStaff = null;
      } else {
        this.idStaff = + params['id']; // (+) converts string 'id' to a number
      }

      // Either we are in creation mode, or we load the collaborator from the back-end...
      // We create an empty collaborator until the subscription is complete
      this.collaborator = {
        idStaff: null, firstName: null, lastName: null, nickName: null, login: null, email: null, level: null,
        isActive: true, dateInactive: null,
        projects: [], experiences: []
      };
      /*
       * By default, you cannot add a project/skill for an unregistered developer.
       */
      document.querySelector('body').style.cssText = '--actions-button-visible: hidden';
      if (this.idStaff != null) {
        this.listStaffService.getCollaborator(this.idStaff).subscribe(
          (collab: Collaborator) => {
            this.staffDataExchangeService.changeCollaborator(collab);
            this.collaborator = collab;
            if (collab.isActive) {
              document.querySelector('body').style.cssText = '--actions-button-visible: visible';
            } else {
              document.querySelector('body').style.cssText = '--actions-button-visible: hidden';
            }
            this.sourceExperience.load(this.collaborator.experiences);
            this.sourceProjects.load(this.collaborator.projects);
            this.cinematicService.setForm(Constants.DEVELOPPERS_CRUD);
          },
          error => {
            if (error.status === 404) {
              if (Constants.DEBUG) {
                console.log('404 : cannot found a collaborator for the id ' + this.idStaff);
              }
              this.messageService.error('There is no staff member for id ' + this.idStaff);
              this.collaborator = {
                idStaff: null, firstName: null, lastName: null, nickName: null, login: null, email: null, level: null,
                isActive: true, dateInactive: null, projects: [], experiences: []
              };
            } else {
              console.error(error.message);
            }
          },
          () => {
            if (this.collaborator.idStaff === 0) {
              console.log('No collaborator found for the id ' + this.idStaff);
            }
            if (Constants.DEBUG) {
              console.log('Loading complete for id ' + this.idStaff);
            }
          }
        );
      }
    });
    this.cinematicService.setForm(Constants.DEVELOPPERS_CRUD);
  }

  /*
	* Refresh the projects content after an update.
	*/
  reloadProjects(idStaff: number): void {
    if (Constants.DEBUG) {
      console.log('Refreshing projects for the staff\'s id ' + idStaff);
    }
    this.staffService.loadProjects(idStaff).subscribe(
      projects => this.sourceProjects.load(projects),
      error => console.log(error),
    );
  }
  /*
  * Refresh the skills content after an update.
  */
  reloadExperiences(idStaff: number): void {
    if (Constants.DEBUG) {
      console.log('Refreshing experiences for the staff\'s id ' + idStaff);
    }
    this.staffService.loadExperiences(idStaff).subscribe(
      assets => this.sourceExperience.load(assets),
      error => console.log(error),
    );
  }

  onConfirmCreateFromProject(event) {
    if (Constants.DEBUG) {
      console.log('onConfirmCreateFromProject for event : ' + event.newData.name);
    }
    if (this.checkStaffMemberExist(event)) {
      this.staffService.addProject(this.collaborator.idStaff, event.newData.name).subscribe(
        (staffDTO: StaffDTO) => {
          this.messageService.info(staffDTO.staff.firstName + ' ' + staffDTO.staff.lastName +
            ' is involved now in project ' + event.newData.name);
          this.reloadProjects(this.collaborator.idStaff);
          event.confirm.resolve();
        },
        response_error => {
          if (Constants.DEBUG) {
            console.log('Error ' + response_error.error.code + ' ' + response_error.error.message);
          }
          this.reloadProjects(this.collaborator.idStaff);
          this.messageService.error(response_error.error.message);
          event.confirm.reject();
        }
      );
    } else {
      event.confirm.reject();
    }
  }

  onConfirmEditFromProject(event) {
    if (Constants.DEBUG) {
      console.log('onConfirmEditFromProject for event from ' + event.data.name + ' to ' + event.newData.name);
    }
    if (this.checkStaffMemberExist(event)) {
      this.projectService.lookup(event.newData.name).subscribe(

        project_transfered => {
          this.staffService.changeProject(this.collaborator.idStaff, event.data.name, event.newData.name).subscribe(
            (staffDTO: StaffDTO) => {
              this.messageService.info(staffDTO.staff.firstName + ' ' +
                staffDTO.staff.lastName + ' is involved now in project ' + event.newData.name);
              this.reloadProjects(this.collaborator.idStaff);
              event.confirm.resolve();
            },
            response_error => {
              if (Constants.DEBUG) {
                console.log('Error ' + response_error.error.code + ' ' + response_error.error.message);
              }
              this.reloadProjects(this.collaborator.idStaff);
              event.confirm.reject();
              this.messageService.error(response_error.error.message);
            }
          );
        },
        response_error => {
          if (Constants.DEBUG) {
            console.error(response_error);
          }
          this.messageService.error(response_error.error.message);
          event.confirm.reject();
        });
    } else {
      event.confirm.reject();
    }
  }

  onBeforeAddStaffSkill(event) {
    if (Constants.DEBUG) {
      console.log('onBeforeAddStaffSkill for event ' + event.newData.title);
    }
    if (this.isAlreadyDeactived()) {
      event.confirm.reject();
    } else {
      event.confirm.confirm();
    }
  }

  onConfirmAddStaffSkill(event) {
    if (Constants.DEBUG) {
      console.log('onConfirmAddStaffSkill for event ' + event.newData.title);
    }
    if (this.checkStaffMemberExist(event)) {
      this.staffService.addExperience(this.collaborator.idStaff, event.newData.title, event.newData.level).subscribe(
        (staffDTO: StaffDTO) => {
          this.messageService.info(staffDTO.staff.firstName + ' ' + staffDTO.staff.lastName +
            ' has gained the skill ' + event.newData.title);
          this.reloadExperiences(this.collaborator.idStaff);
          event.confirm.resolve();
        },
        response_error => {
          if (Constants.DEBUG) {
            console.log('Error ' + response_error.error.code + ' ' + response_error.error.message);
          }
          this.reloadExperiences(this.collaborator.idStaff);
          this.messageService.error(response_error.error.message);
          event.confirm.reject();
        }
      );
    } else {
      event.confirm.reject();
    }
  }

  onConfirmEditStaffSkill(event) {
    if (Constants.DEBUG) {
      console.log('onConfirmEditStaffSkill for event from ' + event.data.title + ' to ' + event.newData.title);
    }
    if (this.checkStaffMemberExist(event)) {
      this.skillService.lookup(event.newData.title).subscribe(

        project_transfered => {
          this.staffService.changeExperience(this.collaborator.idStaff, event.data.title, event.newData.title,
            event.newData.level).subscribe(
            (staffDTO: StaffDTO) => {
              this.messageService.info(staffDTO.staff.firstName + ' ' +
                staffDTO.staff.lastName + ' has now the experience ' + event.newData.titile);
              this.reloadExperiences(this.collaborator.idStaff);
              event.confirm.resolve();
            },
            response_error => {
              if (Constants.DEBUG) {
                console.log('Error ' + response_error.error.code + ' ' + response_error.error.message);
              }
              this.reloadExperiences(this.collaborator.idStaff);
              event.confirm.reject();
              this.messageService.error(response_error.error.message);
            }
            );
        },
        response_error => {
          if (Constants.DEBUG) {
            console.error(response_error);
          }
          this.messageService.error(response_error.error.message);
          event.confirm.reject();
        });
    } else {
      event.confirm.reject();
    }
  }

  /**
   * Check if the staff member available in this form is a brand new, unregistered, staff member or an already registered one.
   * To add or remove skills, projects, the staff object must have an id.
   */
  checkStaffMemberExist(event): boolean {
    if (this.collaborator.idStaff === null) {
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
      /*
       * After the addition into a project of a staff member, and before the reloadProjects has been completed,
       * there is a very little delay with a project without ID into the projects list.
       */
      if (typeof event.data['id'] !== 'undefined') {
        this.staffService.removeFromProject(this.collaborator.idStaff, event.data['id']).subscribe(
          (staffDTO: StaffDTO) => {
            this.messageService.info(staffDTO.staff.firstName + ' ' +
              staffDTO.staff.lastName + ' is not more involved in project ' + event.data.name);
            this.reloadProjects(this.collaborator.idStaff);
            event.confirm.resolve();
          },
          response_error => {
            if (Constants.DEBUG) {
              console.log('Error ' + response_error.error.code + ' ' + response_error.error.message);
            }
            this.reloadProjects(this.collaborator.idStaff);
            event.confirm.reject();
            this.messageService.error(response_error.error.message);
          }
        );
      }
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
      + event.data['title'] + ' for '
      + this.collaborator.firstName + ' '
      + this.collaborator.lastName
      + '?')) {
      /*
       * After the addition of an experience to a staff member, and before the reloadExperiences has been completed,
       * there is a little laps of time without id in the experiences list.
       */
      if (typeof event.data['id'] !== 'undefined') {
        this.staffService.revokeExperience(this.collaborator.idStaff, event.data['id']).subscribe(
          (staffDTO: StaffDTO) => {
            this.messageService.info(staffDTO.staff.firstName + ' ' +
              staffDTO.staff.lastName + ' has no more the skill ' + event.data.title);
            this.reloadExperiences(this.collaborator.idStaff);
            event.confirm.resolve();
          },
          response_error => {
            if (Constants.DEBUG) {
              console.log('Error ' + response_error.error.code + ' ' + response_error.error.message);
            }
            this.reloadExperiences(this.collaborator.idStaff);
            this.messageService.error(response_error.error.message);
            event.confirm.reject();
          }
        );
      }
    } else {
      event.confirm.reject();
    }
  }

  /**
   * Test if the collaborator has been already deactivated on the database.
   * You can test this state by testing the dateInactive, filled by the back-end during the deactivation process.
   */
  public isAlreadyDeactived(): boolean {
    return (this.collaborator.dateInactive != null);
  }

}


