import { Constants } from '../../../constants';
import { Collaborator } from '../../../data/collaborator';
import { StaffDTO } from '../../../data/external/staffDTO';
import { MessageService } from '../../../message.service';
import { ProjectService } from '../../../project.service';
import { StaffService } from '../../../staff.service';
import { StaffDataExchangeService } from '../../service/staff-data-exchange.service';
import { Component, OnInit, Input } from '@angular/core';

import {Ng2SmartTableModule} from 'ng2-smart-table';
import {StarsSkillLevelRenderComponent} from './../../starsSkillLevelRenderComponent';
import {Ng2SmartTableComponent} from 'ng2-smart-table/ng2-smart-table.component';
import { LocalDataSource } from 'ng2-smart-table';

@Component({
  selector: 'app-staff-projects',
  templateUrl: './staff-projects.component.html',
  styleUrls: ['./staff-projects.component.css']
})
export class StaffProjectsComponent implements OnInit {

  /*
   * Data store associated with the projects grid
   */
  private sourceProjects = new LocalDataSource([]);

  /*
   * Settings of the projects grid
   */
  private settings_projects = Constants.SETTINGS_PROJECTS_SMARTTABLE;

  /**
   * Employee retrieve from StaffComponent access.
   */
  private collaborator: Collaborator;

  constructor(
      private messageService: MessageService,
      private staffService: StaffService,
      private projectService: ProjectService,
      private staffDataExchangeService: StaffDataExchangeService ) { }

  ngOnInit() {
    // Either we are in creation mode, or we load the collaborator from the back-end...
    // We create an empty collaborator until the subscription is complete
    this.collaborator = {
      idStaff: null, firstName: null, lastName: null, nickName: null, login: null, email: null, level: null,
      isActive: true, dateInactive: null,
      projects: [], experiences: []
    };
    /**
     * We listen the parent component (StaffComponent) in charge of retrieving data from the back-end.
     */
    this.staffDataExchangeService.collaboratorObserver
      .subscribe((collabRetrieved: Collaborator) => {
        this.collaborator = collabRetrieved;
        this.sourceProjects.load(this.collaborator.projects);
      });
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
}
