import { Constants } from '../../constants';
import { Collaborator } from '../../data/collaborator';
import { StaffDTO } from '../../data/external/staffDTO';
import { MessageService } from '../../message/message.service';
import { SkillService } from '../../service/skill.service';
import { StaffService } from '../../service/staff.service';
import { StaffDataExchangeService } from '../service/staff-data-exchange.service';
import { Component, OnInit, OnDestroy, Input } from '@angular/core';

import { StaffUploadCvComponent } from './staff-upload-cv/staff-upload-cv.component';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { BaseComponent } from '../../base/base.component';
import { TabsStaffListService } from 'src/app/tabs-staff-list/service/tabs-staff-list.service';
import { TagStar } from '../staff-form/tag-star';
import { BehaviorSubject, Subject } from 'rxjs';
import { THIS_EXPR } from '@angular/compiler/src/output/output_ast';
import { Experience } from 'src/app/data/experience';
import { BooleanDTO } from 'src/app/data/external/booleanDTO';
import { INTERNAL_SERVER_ERROR} from 'http-status-codes';
import { take } from 'rxjs/operators';
import { DeclaredExperience } from 'src/app/data/declared-experience';

@Component({
	selector: 'app-staff-experience',
	templateUrl: './staff-experience.component.html',
	styleUrls: ['./staff-experience.component.css']
})
export class StaffExperienceComponent extends BaseComponent implements OnInit, OnDestroy {

	/**
	 * Selected TAB.
	 * This observable is fired by the StaffComponent class when the user changes the tab selected.
	 */
	@Input() selectedTab$;

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

	/***
     * Employee retrieve from StaffComponent access.
     */
	public staff: Collaborator;

	whitelist = [];

	blacklist = [];

	/**
	 * Original Values which will be added to the component TagifyStar on startup.
	 */
	public originalValues = [];

	/**
	 * Additional Values which might be added to the component TagifyStar.
	 */
	public additionalValues$ = new Subject<TagStar[]>();

	/**
	 * Values to replace the content of he component TagifyStar.
	 */
	public values$ = new Subject<TagStar[]>();

	/**
	 * If this this staff member is inactive, the tag stars component should be inactive readonly.
	 */
	public readOnly$ = new Subject<boolean>();

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

		if (!this.staff) {
			setTimeout(() => this.readOnly$.next(true), 0);
		}
		/***
         * We listen the parent component (StaffComponent) in charge of retrieving data from the back-end.
         */
		this.subscriptions.add(
			this.staffDataExchangeService.collaborator$
				.subscribe((collabRetrieved: Collaborator) => {
					this.staff = collabRetrieved;
					if (Constants.DEBUG) {
						console.log ('staff member loaded', this.staff.firstName + ' ' + this.staff.lastName);
					}
					// We transfert the experience into the array originalValues
					// in order to be displayed into the tagify-stars component
					// We subtract 1 from the level because the array in TagifyStars is numbered from 0 to 4
					setTimeout(() => {
						const values: TagStar[] = [];
						this.staff.experiences.forEach(experience => {
							values.push(new TagStar(experience.title, experience.level - 1));
						});
						this.values$.next(values);
						this.readOnly$.next(!this.staff.idStaff || !this.staff.active);
					}, 0);

					// The title of the skill is not propagated by the server. We filled this property "live" on the desktop
					this.staff.experiences.forEach(exp => exp.title = this.skillService.title(exp.id));

					// We transfert all available skills into the whitelist of the tagify-stars component
					this.skillService.allSkills.forEach (
						skill => this.whitelist.push(skill.title)
					);

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
				},
				error => console.log(error),
			));
	}

	/**
     * Check if the staff member available in this form is a brand new, unregistered, staff member or an already registered one.
     * To add or remove skills, projects, the staff must exist and have an id.
     */
	checkStaffMemberExist(): boolean {
		if (this.staff.idStaff === null) {
			this.messageService.error('You cannot update a skill, or a project, of an unregistered staff member. '
				+ 'Please saved this new member first !');
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Log the experiences of this staff member into the console.
	 * @param staff the given staff member
	 */
	private logExperiences(staff: Collaborator) {
		if (Constants.DEBUG) {
			console.groupCollapsed('Skills registered for ' + this.staff.lastName);
			this.staff.experiences.forEach (exp => console.log (exp.title));
			console.groupEnd();
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
			dialogReference.afterClosed().subscribe(experiences => {
				const newExperiences = experiences.filter(function(experience) {
					return (this.staff.experiences.findIndex(exp => exp.id === experience.idSkill) === -1);
				}.bind(this));
				if (Constants.DEBUG) {
					console.groupCollapsed(newExperiences.length + ' NEW experiences detected : ');
					newExperiences.forEach(element => console.log (element.title));
					console.groupEnd();
				}

				// We update the tagify-stars component.
				const tagStars = [];
				newExperiences.forEach(element => {
					tagStars.push(new TagStar(element.title, 0));
				});
				this.additionalValues$.next(tagStars);

				this.updateStaffWithNewExperiences(this.staff.idStaff, newExperiences);
			}));
	}

	updateStaffWithNewExperiences(idStaff: number, newExperiences: DeclaredExperience[]) {
		this.staffService.addDeclaredExperience (idStaff, newExperiences)
			.pipe(take(1))
			.subscribe(
				staffDTO => {
					if (Constants.DEBUG) {
						console.groupCollapsed('Registred skills for the staff member '						+ staffDTO.staff.firstName + ' ' + staffDTO.staff.lastName);
							staffDTO.staff.experiences.forEach(
								element => console.log (this.skillService.title(element.id)));
						console.groupEnd();
					}
					newExperiences.forEach(element => {
						this.staff.experiences.push({id: element.idSkill, title: element.title, level: 1});
					});
				},
				response => {
						if (response.status === INTERNAL_SERVER_ERROR) {
							if (Constants.DEBUG) {
								console.log('500 : Error returned ' + response.error.message);
							}
							this.messageService.error(response.error.code + ' : ' + response.error.message);
						} else {
							console.error(response.error);
							this.messageService.error('-1 : WTF Enormous !!' );
						}
				});
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

	/**
	 * Returns TRUE if the given skill already registered in the experience of the given staff member, FALSE otherwise.
	 * @param staff - the current staff
	 * @param idSkill - the given skill identifier
	 */
	skillAlreadyRegistered (staff: Collaborator, idSkill: number): boolean {
		return (staff.experiences.findIndex(ex => ex.id === idSkill) !== -1);
	}

	updateExperienceLevel(staff: Collaborator, idSkill: number, level: number) {
		this.staffService.updateExperienceLevel({
			idStaff: this.staff.idStaff,
			idSkill: idSkill,
			level: level})
			.subscribe( (ret: Boolean) => {
				if (ret) {
					/**
					 * If this staff member exists in pre-existing list of collaborators. We actualize the content.
					 */
					const currentExperience = this.staff.experiences.find( exp => exp.id === idSkill);
					if (!currentExperience) {
						console.error ('SHOULD NOT PASS HERE : No staff found for id ' + idSkill);
					}
					currentExperience.level = level;

					this.logExperiences(this.staff);
					this.messageService.info(this.staff.firstName + ' ' + this.staff.lastName +
					' has updated the skill ' + this.skillService.title(idSkill));

					/**
					 * If this staff member exists in pre-existing list of collaborators. We actualize the content.
					 */
					this.tabsStaffListService.actualizeCollaborator(this.staff);
				}
			}
		);
	}

	addNewSkill(staff: Collaborator, idSkill: number, titleSkill: string, levelSkill: number) {
		this.staffService.addExperience({
			idStaff: this.staff.idStaff,
			idSkill: idSkill,
			level: levelSkill})
			.subscribe( (ret: Boolean) => {
				if (ret) {
					/**
					 * If this staff member exists in pre-existing list of collaborators. We actualize the content.
					 */
					this.staff.experiences.push({
						'id': idSkill,
						'title': titleSkill,
						'level': levelSkill
					});

					this.logExperiences(this.staff);
					this.messageService.info(this.staff.firstName + ' ' + this.staff.lastName +
					' has gained the skill ' + titleSkill);

					/**
					 * If this staff member exists in pre-existing list of collaborators. We actualize the content.
					 */
					this.tabsStaffListService.actualizeCollaborator(this.staff);
				}
			},
			response_error => {
				if (Constants.DEBUG) {
					console.log('Error', response_error);
				}
				this.reloadExperiences(this.staff.idStaff);
				this.messageService.error(response_error.error.message);
			}
		);
	}

	onAddTagEvent(tagStar: TagStar) {
		if (Constants.DEBUG) {
			console.log ('Add event for ' + tagStar.tag + ' ' + tagStar.star);
		}
		if (this.checkStaffMemberExist()) {
			const idSkill = this.skillService.id(tagStar.tag);
			this.addNewSkill (this.staff, idSkill, tagStar.tag, tagStar.star + 1);
		}
	}

	onEditTagEvent(tagStar: TagStar) {
		if (Constants.DEBUG) {
			console.log ('Edit event for ' + tagStar.tag + ' ' + tagStar.star);
		}
		if (this.checkStaffMemberExist()) {
			const idSkill = this.skillService.id(tagStar.tag);
			this.updateExperienceLevel (this.staff, idSkill, tagStar.star + 1);
		}
	}

	onRemoveTagEvent(tag: string) {
		if (Constants.DEBUG) {
			console.log ('Remove event for ' + tag);
		}
		const idSkill = this.skillService.id(tag);
		this.staffService.removeExperience(this.staff.idStaff, idSkill).subscribe(
			(ret: Boolean) => {
				if (ret) {
					this.messageService.info(this.staff.firstName + ' ' +
						this.staff.lastName + ' has no more the skill ' + tag);

					const idx = this.staff.experiences.findIndex(exp => exp.title === tag);
					this.staff.experiences.splice(idx, 1);
					this.logExperiences(this.staff);

					/**
				     * If this staff member exists in pre-existing list of collaborators. We actualize the content.
					 */
					this.tabsStaffListService.actualizeCollaborator(this.staff);
				}
			});
	}

	ngOnDestroy() {
		super.ngOnDestroy();
	}
}
