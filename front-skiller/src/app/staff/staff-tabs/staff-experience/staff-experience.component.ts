import {Constants} from '../../../constants';
import {Collaborator} from '../../../data/collaborator';
import {StaffDTO} from '../../../data/external/staffDTO';
import {MessageService} from '../../../message/message.service';
import {SkillService} from '../../../service/skill.service';
import {StaffService} from '../../../service/staff.service';
import {StaffDataExchangeService} from '../../service/staff-data-exchange.service';
import {Component, OnInit} from '@angular/core';

import {LocalDataSource} from 'ng2-smart-table';
import {StaffUploadCvComponent} from './staff-upload-cv/staff-upload-cv.component';
import {MatDialog, MatDialogConfig} from '@angular/material';

@Component({
  selector: 'app-staff-experience',
  templateUrl: './staff-experience.component.html',
  styleUrls: ['./staff-experience.component.css']
})
export class StaffExperienceComponent implements OnInit {

  /**
   * Image used by the button for upload the application to retrieve the skills.
   */
  image_upLoadCV = '/assets/img/uploadCV.png';

  /**
   * Image used by the button for upload the application to retrieve the skills.
   */
  image_downloadCV: string;
  images_dir = '/assets/img/';
  image_winword = 'word.png';
  image_pdf = 'pdf.png';

  /*
   * Data store associated with the projects grid
   */
  sourceExperience = new LocalDataSource([]);

  /*
   * Settings of the projects grid
   */
  settings_experience = Constants.SETTINGS_EXPERIENCE_SMARTTABLE;

  /**
   * Employee retrieve from StaffComponent access.
   */
  public staff: Collaborator;


  constructor(
    private staffDataExchangeService: StaffDataExchangeService,
    private staffService: StaffService,
    private messageService: MessageService,
    private dialog: MatDialog,
    private skillService: SkillService) {}

  ngOnInit() {
    /**
     * We listen the parent component (StaffComponent) in charge of retrieving data from the back-end.
     */
    this.staffDataExchangeService.collaboratorObserver
      .subscribe((collabRetrieved: Collaborator) => {
        this.staff = collabRetrieved;
        this.sourceExperience.load(this.staff.experiences);

        switch (this.staff.typeOfApplication) {
          case Constants.FILE_TYPE_DOC:
          case Constants.FILE_TYPE_DOCX:
            {
              this.image_downloadCV = this.images_dir + this.image_winword;
              break;
            }
          case Constants.FILE_TYPE_PDF:
            {
              this.image_downloadCV = this.images_dir + this.image_pdf;
              break;
            }
        }
      });
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
      this.staffService.addExperience(this.staff.idStaff, event.newData.title, event.newData.level).subscribe(
        (staffDTO: StaffDTO) => {
          this.messageService.info(staffDTO.staff.firstName + ' ' + staffDTO.staff.lastName +
            ' has gained the skill ' + event.newData.title);
          this.reloadExperiences(this.staff.idStaff);
          event.confirm.resolve();
        },
        response_error => {
          if (Constants.DEBUG) {
            console.log('Error ' + response_error.error.code + ' ' + response_error.error.message);
          }
          this.reloadExperiences(this.staff.idStaff);
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
          this.staffService.changeExperience(this.staff.idStaff, event.data.title, event.newData.title,
            event.newData.level).subscribe(
            (staffDTO: StaffDTO) => {
              this.messageService.info(staffDTO.staff.firstName + ' ' +
                staffDTO.staff.lastName + ' has now the experience ' + event.newData.title);
              this.reloadExperiences(this.staff.idStaff);
              event.confirm.resolve();
            },
            response_error => {
              if (Constants.DEBUG) {
                console.log('Error ' + response_error.error.code + ' ' + response_error.error.message);
              }
              this.reloadExperiences(this.staff.idStaff);
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
    if (this.staff.idStaff === null) {
      this.messageService.error('You cannot update a skill, or a project, of an unregistered staff member. '
        + 'Please saved this new member first !');
      return false;
    } else {
      return true;
    }
  }

  onConfirmRemoveSkill(event) {
    if (!this.checkStaffMemberExist(event)) {
      event.confirm.reject();
      return;
    }
    if (window.confirm('Are you sure you want to remove the skill '
      + event.data['title'] + ' for '
      + this.staff.firstName + ' '
      + this.staff.lastName
      + '?')) {
      /*
       * After the addition of an experience to a staff member, and before the reloadExperiences has been completed,
       * there is a little laps of time without id in the experiences list.
       */
      if (typeof event.data['id'] !== 'undefined') {
        this.staffService.revokeExperience(this.staff.idStaff, event.data['id']).subscribe(
          (staffDTO: StaffDTO) => {
            this.messageService.info(staffDTO.staff.firstName + ' ' +
              staffDTO.staff.lastName + ' has no more the skill ' + event.data.title);
            this.reloadExperiences(this.staff.idStaff);
            event.confirm.resolve();
          },
          response_error => {
            if (Constants.DEBUG) {
              console.log('Error ' + response_error.error.code + ' ' + response_error.error.message);
            }
            this.reloadExperiences(this.staff.idStaff);
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
    return (this.staff.dateInactive != null);
  }

  upload() {
    if (this.isAlreadyDeactived()) {
      this.messageService.error(this.staff.firstName + ' ' + this.staff.lastName + ' is desactivated!');
      return false;
    }

    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = true;
    dialogConfig.autoFocus = true;
    dialogConfig.panelClass = 'default-dialog-container-class';
    dialogConfig.data = this.staff;
    const dialogReference = this.dialog.open(StaffUploadCvComponent, dialogConfig);
    dialogReference.afterClosed().subscribe(returnCodeMessage => {
      if (returnCodeMessage.code === Constants.ERROR) {
        this.messageService.error(returnCodeMessage.message);
      }
      if (returnCodeMessage.code === Constants.OK) {
        this.reloadExperiences(this.staff.idStaff);
        this.messageService.info(returnCodeMessage.message);
      }
    });
  }

  /**
   * Download the application file for this staff member is any.
   */
  download() {
    if (Constants.DEBUG) {
      console.log('Downloading the application filename '
        + this.staff.application
        + ' for '
        + this.staff.firstName
        + ' '
        + this.staff.lastName);
    }
    this.staffService.downloadApplication(this.staff);
  }
}
