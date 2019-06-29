import { Constants } from '../../constants';
import { Collaborator } from '../../data/collaborator';
import { StaffDTO } from '../../data/external/staffDTO';
import { MessageService } from '../../message/message.service';
import { SkillService } from '../../service/skill.service';
import { StaffService } from '../../service/staff.service';
import { StaffDataExchangeService } from '../service/staff-data-exchange.service';
import { Component, OnInit, OnDestroy } from '@angular/core';

import { LocalDataSource } from 'ng2-smart-table';
import { StaffUploadCvComponent } from './staff-upload-cv/staff-upload-cv.component';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { BaseComponent } from '../../base/base.component';
import { TabsStaffListService } from 'src/app/tabs-staff-list/service/tabs-staff-list.service';

@Component({
	selector: 'app-staff-experience',
	templateUrl: './staff-experience.component.html',
	styleUrls: ['./staff-experience.component.css']
})
export class StaffExperienceComponent extends BaseComponent implements OnInit, OnDestroy {

	/***
     * Image used by the button for upload the application to retrieve the skills.
     */
	image_upLoadCV = './assets/img/uploadCV.png';

	/***
     * Image used by the button for upload the application to retrieve the skills.
     */
	image_downloadCV: string;
	images_dir = './assets/img/';
	image_winword = 'word.png';
	image_pdf = 'pdf.png';

	/**
     * Data store associated with the projects grid
     */
	sourceExperience = new LocalDataSource([]);

	/**
     * Settings of the projects grid
     */
	settings_experience = Constants.SETTINGS_EXPERIENCE_SMARTTABLE;

	/***
     * Employee retrieve from StaffComponent access.
     */
	public staff: Collaborator;

	constructor(
		private staffDataExchangeService: StaffDataExchangeService,
		private tabsStaffListService: TabsStaffListService,
		private staffService: StaffService,
		private messageService: MessageService,
		private dialog: MatDialog,
		private skillService: SkillService) {
		super();
	}

	ngOnInit() {
		/***
         * We listen the parent component (StaffComponent) in charge of retrieving data from the back-end.
         */
		this.subscriptions.add(
			this.staffDataExchangeService.collaboratorObserver
				.subscribe((collabRetrieved: Collaborator) => {
					this.staff = collabRetrieved;

					// The title of the skill is not propagated by the server. We filled this property "live" on the desktop
					this.staff.experiences.forEach(exp => exp.title = this.skillService.title(exp.id));

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
				})
		);
	}

	/**
     * Refresh the skills content after an update.
     */
	reloadExperiences(idStaff: number): void {
		if (Constants.DEBUG) {
			console.log('Refreshing experiences for the staff\'s id ' + idStaff);
		}
		this.subscriptions.add(
			this.staffService.loadExperiences(idStaff).subscribe(
				experiences => {

					// The title of the skill is not propagated by the server. We filled this property "live" on the desktop
					experiences.forEach(exp => exp.title = this.skillService.title(exp.id));

					this.sourceExperience.load(experiences);
				},
				error => console.log(error),
			));
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

	/**
     * Confirm to the end-user that his operation succeeds. The skill is added to the collaborator.
     * @param staff the staff member concerned
     * @param skillTitle the title of the skill added.
     * @param event the active JS event thrown by the framework.
     */
	messageConfirmationSkillAdded(staff: Collaborator, skillTitle: string, event: any) {
		this.messageService.info(staff.firstName + ' ' + staff.lastName +
			' has gained the skill ' + skillTitle);
		this.reloadExperiences(this.staff.idStaff);
		event.confirm.resolve();
	}

	onConfirmAddStaffSkill($event) {
		if (Constants.DEBUG) {
			console.log('onConfirmAddStaffSkill for event ' + $event.newData.title);
		}
		if (this.checkStaffMemberExist($event)) {
			this.subscriptions.add(
				this.staffService.addExperience(this.staff.idStaff, $event.newData.title, $event.newData.level).subscribe(
					(staffDTO: StaffDTO) => {
						/**
                         * If this staff member exists in pre-existing list of collaborators. We actualize the content.
                         */
						this.tabsStaffListService.actualizeCollaborator(staffDTO.staff);
						this.messageConfirmationSkillAdded(staffDTO.staff, $event.newData.title, $event);
					},
					response_error => {
						if (Constants.DEBUG) {
							console.log('Error ' + response_error.error.code + ' ' + response_error.error.message);
						}
						this.reloadExperiences(this.staff.idStaff);
						this.messageService.error(response_error.error.message);
						$event.confirm.reject();
					}
				));
		} else {
			$event.confirm.reject();
		}
	}

	onConfirmEditStaffSkill(event) {
		if (Constants.DEBUG) {
			console.log('onConfirmEditStaffSkill for event from ' + event.data.title + ' to ' + event.newData.title);
		}
		if (this.checkStaffMemberExist(event)) {
			this.subscriptions.add(
				this.skillService.lookup(event.newData.title).subscribe(
					() => {
						this.subscriptions.add(
							this.staffService.changeExperience(this.staff.idStaff, event.data.title, event.newData.title,
								event.newData.level).subscribe(
									(staffDTO: StaffDTO) => {
										this.messageConfirmationSkillAdded(staffDTO.staff, event.newData.title, event);
										this.reloadExperiences(this.staff.idStaff);
										/**
                                         * If this staff member exists in pre-existing list of collaborators. We actualize the content.
                                         */
										this.tabsStaffListService.actualizeCollaborator(staffDTO.staff);
									},
									response_error => {
										if (Constants.DEBUG) {
											console.log('Error ' + response_error.error.code + ' ' + response_error.error.message);
										}
										this.reloadExperiences(this.staff.idStaff);
										event.confirm.reject();
										this.messageService.error(response_error.error.message);
									}
								));
					},
					response_error => {
						if (Constants.DEBUG) {
							console.error(response_error);
						}
						this.messageService.error(response_error.error.message);
						event.confirm.reject();
					}));
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
				this.subscriptions.add(
					this.staffService.revokeExperience(this.staff.idStaff, event.data['id']).subscribe(
						(staffDTO: StaffDTO) => {
							this.messageService.info(staffDTO.staff.firstName + ' ' +
								staffDTO.staff.lastName + ' has no more the skill ' + event.data.title);
							this.reloadExperiences(this.staff.idStaff);
							event.confirm.resolve();
						/**
                             * If this staff member exists in pre-existing list of collaborators. We actualize the content.
                             */
							this.tabsStaffListService.actualizeCollaborator(staffDTO.staff);
						},
						response_error => {
							if (Constants.DEBUG) {
								console.log('Error ' + response_error.error.code + ' ' + response_error.error.message);
							}
							this.reloadExperiences(this.staff.idStaff);
							this.messageService.error(response_error.error.message);
							event.confirm.reject();
						}
					));
			}
		} else {
			event.confirm.reject();
		}
	}

	/***
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
		this.subscriptions.add(
			dialogReference.afterClosed().subscribe(returnCodeMessage => {
				if (returnCodeMessage.code === Constants.ERROR) {
					this.messageService.error(returnCodeMessage.message);
				}
				if (returnCodeMessage.code === Constants.OK) {
					this.reloadExperiences(this.staff.idStaff);
					this.messageService.info(returnCodeMessage.message);
				}
			}));
	}

	/***
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

	ngOnDestroy() {
		super.ngOnDestroy();
	}
}
