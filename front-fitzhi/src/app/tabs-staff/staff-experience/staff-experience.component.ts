import { Constants } from '../../constants';
import { Collaborator } from '../../data/collaborator';
import { MessageService } from '../../interaction/message/message.service';
import { SkillService } from '../../skill/service/skill.service';
import { StaffService } from '../service/staff.service';
import { StaffDataExchangeService } from '../service/staff-data-exchange.service';
import { Component, OnInit, OnDestroy, Input } from '@angular/core';

import { StaffUploadCvComponent } from './staff-upload-cv/staff-upload-cv.component';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { BaseComponent } from '../../base/base.component';
import { TabsStaffListService } from 'src/app/tabs-staff-list/service/tabs-staff-list.service';
import { TagStar } from '../staff-form/tag-star';
import { INTERNAL_SERVER_ERROR} from 'http-status-codes';
import { FileService } from 'src/app/service/file.service';
import { Subject } from 'rxjs';
import { DeclaredExperience } from 'src/app/data/declared-experience';
import { take } from 'rxjs/operators';
import { traceOn } from 'src/app/global';
import { TagifyEditableState } from './tagify-stars/tagify-editable-state';

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
	public editableState$ = new Subject<TagifyEditableState>();

	constructor(
		private staffDataExchangeService: StaffDataExchangeService,
		private tabsStaffListService: TabsStaffListService,
		private staffService: StaffService,
		private fileService: FileService,
		private messageService: MessageService,
		private dialog: MatDialog,
		private skillService: SkillService) {
		super();
	}

	ngOnInit() {

		if (!this.staff) {
			setTimeout(() => this.editableState$.next(TagifyEditableState.READ_ONLY), 0);
		}
		/***
         * We listen the parent component (StaffComponent) in charge of retrieving data from the back-end.
         */
		this.subscriptions.add(
			this.staffDataExchangeService.collaboratorLoaded$.subscribe({
				next: doneAndOk => {
					if (doneAndOk) {
						this.takeInAccountCollaborator();
					}
				}
			}));
	}

	private takeInAccountCollaborator() {
		this.staff = this.staffDataExchangeService.collaborator;
		if (traceOn()) {
			console.log ('staff member loaded', this.staff.firstName + ' ' + this.staff.lastName);
		}
		//
		// We transfert the experience into the array originalValues
		// in order to be displayed into the tagify-stars component
		// We subtract 1 from the level because the array in TagifyStars is numbered from 0 to 4
		//
		setTimeout(() => {
			const values: TagStar[] = [];
			this.staff.experiences.forEach(experience => {
				values.push(new TagStar(experience.title, experience.level - 1));
			});

			// The test is there, only to avoid an empty-warning from the tagify component
			if (values.length > 0) {
				this.values$.next(values);
			}

			if (!this.staff.idStaff || !this.staff.active) {
				this.editableState$.next(TagifyEditableState.READ_ONLY);
			} else {
				this.editableState$.next(TagifyEditableState.ALL_ALLOWED);
			}

		}, 0);

		// The title of the skill is not propagated by the server. We filled this property "live" on the desktop
		this.staff.experiences.forEach(exp => exp.title = this.skillService.title(exp.id));

		// We transfert all available skills into the whitelist of the tagify-stars component
		this.skillService.allSkills.forEach (
			skill => this.whitelist.push(skill.title)
		);
		this.image_downloadCV = this.fileService.getAssociatedIcon(this.staff.typeOfApplication);
	}

	/**
     * Refresh the skills content after an update.
     */
	reloadExperiences(idStaff: number): void {
		if (traceOn()) {
			console.log('Refreshing experiences for the staff\'s id ' + idStaff);
		}
		this.subscriptions.add(
			this.staffService.loadExperiences(idStaff).subscribe({
				next: experiences => {
						// The title of the skill is not propagated by the server. We filled this property "live" on the desktop
						experiences.forEach(exp => exp.title = this.skillService.title(exp.id));
					},
				error: error => console.log(error),
			}));
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
		if (traceOn()) {
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

	/**
	 * Upload the application file.
	 */
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
				
				// Taking in account in the front-end application
				const newExperiences = this.isolateNewExperiences(experiences);

				// Taking in account in the back-end application. Updating the developer if needed.
				this.updateStaffWithNewExperiences(this.staff.idStaff, newExperiences);
			}));
	}

	/**
	 * Isolabe and take in account the new skills detected for a developer.
	 * @param experiences  the experiences retrieved from the application file
	 * @returns ann array containg all new experiences for the current developer
	 */
	isolateNewExperiences(experiences: DeclaredExperience[]): DeclaredExperience[] {

		// Isolation
		const newExperiences = [];
		experiences.forEach(exp => {
			if (!this.isAlreadyPresent(exp.idSkill)){
				newExperiences.push(exp);
			}
		});

		if (traceOn() && (newExperiences.length > 0)) {
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
		
		return newExperiences;
	}

	/**
	 * Test if the skill is already registered for this staff member.
	 * @param idSkill the Skill identifier
	 * @returns **true** if this skill has been retrieved, **false** otherwise
	 */
	isAlreadyPresent (idSkill: number): boolean {
		const index = this.staff.experiences.findIndex(exp => exp.id === idSkill);
		return (index !== -1)
	}

	/**
	 * Update the staff record if needed with the new skills extracted from the application file;
	 * @param idStaff the staff identifier
	 * @param newExperiences  an array of (skill;level) to be added
	 */
	updateStaffWithNewExperiences(idStaff: number, newExperiences: DeclaredExperience[]) {
		// Nothing to add
		if (newExperiences.length === 0) {
			return;
		}
		this.staffService.addDeclaredExperience$ (idStaff, newExperiences)
			.pipe(take(1))
			.subscribe(
				staff => {
					if (traceOn()) {
						console.groupCollapsed('Registred skills for the staff member '	 
							+ staff.firstName + ' ' + staff.lastName);
							staff.experiences.forEach(
								element => console.log (this.skillService.title(element.id)));
						console.groupEnd();
					}
					newExperiences.forEach(element => {
						this.staff.experiences.push({id: element.idSkill, title: element.title, level: 1});
					});
				},
				response => {
						if (response.status === INTERNAL_SERVER_ERROR) {
							if (traceOn()) {
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
		if (traceOn()) {
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

	/**
	 * Update the experience of a staff member.
	 * 
	 * @param idSkill the skill identifier to update
	 * @param level the level obtained on this skill
	 */
	public updateExperience(idSkill: number, level: number) {
		this.staffService.updateExperience$({
			idStaff: this.staff.idStaff,
			idSkill: idSkill,
			level: level})
			.subscribe( (ret: boolean) => {
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
					this.messageService.success(this.staff.firstName + ' ' + this.staff.lastName +
					' has updated the skill ' + this.skillService.title(idSkill));

					/**
					 * If this staff member exists in pre-existing list of collaborators. We actualize the content.
					 */
					this.tabsStaffListService.actualizeCollaborator(this.staff);
				}
			}
		);
	}

	/**
	 * Add a new skill to a collaborator.
	 * 
	 * @param idSkill the skill identifier to update
	 * @param titleSkill the skill title
	 * @param level the level obtained on this skill
	 */
	public addExperience(idSkill: number, titleSkill: string, levelSkill: number) {
		this.staffService.addExperience$({
			idStaff: this.staff.idStaff,
			idSkill: idSkill,
			level: levelSkill})
			.subscribe( (ret: boolean) => {
				if (ret) {
					//
					// We update the experiences of the current staff member.
					// 
					this.staff.experiences.push({
						'id': idSkill,
						'title': titleSkill,
						'level': levelSkill
					});

					this.logExperiences(this.staff);
					this.messageService.success(this.staff.firstName + ' ' + this.staff.lastName +
					' has gained the skill ' + titleSkill);

					/**
					 * If this staff member exists in pre-existing list of collaborators. We actualize the content.
					 */
					this.tabsStaffListService.actualizeCollaborator(this.staff);
				}
			},
			error => {
				if (traceOn()) {
					console.log('Error', error);
				}
				this.reloadExperiences(this.staff.idStaff);
				this.messageService.error(error.message);
			}
		);
	}

	/**
	 * Add a new skill to a collaborator.
	 * 
	 * @param idSkill the skill identifier to update
	 * @param titleSkill the skill title
	 * @param level the level obtained on this skill
	 */
	public removeExperience(idSkill: number, title: string) {
		this.staffService.removeExperience$(this.staff.idStaff, idSkill).subscribe(
			(ret: boolean) => {
				if (ret) {
					this.messageService.success(this.staff.firstName + ' ' +
						this.staff.lastName + ' has no more the skill ' + title);

					const idx = this.staff.experiences.findIndex(exp => exp.title === title);
					this.staff.experiences.splice(idx, 1);
					this.logExperiences(this.staff);

					/**
				     * If this staff member exists in pre-existing list of collaborators. We actualize the content.
					 */
					this.tabsStaffListService.actualizeCollaborator(this.staff);
				}
			}
		);
	}

	onAddTagEvent(tagStar: TagStar) {
		if (traceOn()) {
			console.log ('Add event for ' + tagStar.tag + ' ' + tagStar.star);
		}
		if (this.checkStaffMemberExist()) {
			const idSkill = this.skillService.id(tagStar.tag);
			this.addExperience (idSkill, tagStar.tag, tagStar.star + 1);
		}
	}

	onEditTagEvent(tagStar: TagStar) {
		if (traceOn()) {
			console.log ('Edit event for ' + tagStar.tag + ' ' + tagStar.star);
		}
		if (this.checkStaffMemberExist()) {
			const idSkill = this.skillService.id(tagStar.tag);
			this.updateExperience (idSkill, tagStar.star + 1);
		}
	}

	onRemoveTagEvent(tag: string) {
		if (traceOn()) {
			console.log ('Remove event for ' + tag);
		}
		const idSkill = this.skillService.id(tag);
		this.removeExperience(idSkill, tag);
	}

	/**
	 * Return the class file
	 */
	classFile(): string {
		return 'downloadIcon ' +
				((this.staff.application) ?
			this.fileService.getAssociatedAwesomeFont(this.staff.typeOfApplication)
			: '');
	}

	ngOnDestroy() {
		super.ngOnDestroy();
	}
}
