import { Observable } from 'rxjs';
import { ProjectSkill } from 'src/app/data/project-skill';
import { traceOn } from 'src/app/global';
import { MessageService } from 'src/app/interaction/message/message.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { SkillService } from 'src/app/skill/service/skill.service';

/**
 * This class contains some features & controls regarding the skills and used by the Project form component.
 *
 * This is not an Angular Global Service because its scope is limited to this component.
 *
 * @author Frédéric VIDAL
 */
export class ProjectFormSkillHandler {

	constructor(
		private projectService: ProjectService,
		private skillService: SkillService,
		private messageService: MessageService) {}

	/**
	 * Add a skill inside the project.
	 *
	 * @param event ADD event fired by the tagify component.
	 */
	public addSkill(event: CustomEvent) {

		if ((!this.projectService.project.id) || (this.projectService.project.id === -1)) {
			if (traceOn()) {
				console.log ('Adding a skill is impossible for an unregistered project.');
			}
			this.messageService.error('Adding a skill is impossible for an unregistered project!');
			return;
		}

		const idSkill = this.skillService.id(event.detail.data.value);
		if (idSkill === -1) {
			throw new Error(`SEVERE ERROR : Unregistered skill ${event.detail.data.value}`);
		}

		// This skills is already registered for this project.
		if (this.projectService.project.mapSkills.has(idSkill)) {
			return;
		}

		if (traceOn()) {
			console.log('Adding the skill', event.detail.data.value);
		}

		this.projectService.project.mapSkills.set(idSkill, new ProjectSkill(idSkill, 0, 0));

		// We have already loaded or saved the project, so we can add each new skill as they appear, one by one.
		if (this.projectService.project.id)  {
			this.updateSkill(
				this.projectService.project.id,
				idSkill,
				this.projectService.addSkill$.bind(this.projectService));
		}

		// Log the resulting collection.
		this.logProjectSkills();
	}

	/**
	 * Remove a skill from the project.
	 * @param event ADD event fired by the tagify component.
	 */
	removeSkill(event: CustomEvent) {

		const idSkill = this.skillService.id(event.detail.data.value);
		if (idSkill === -1) {
			throw new Error(`SEVERE ERROR : Unregistered skill ${event.detail.data.value}`);
		}

		// This skills is NOT already registered for this project.
		if (!this.projectService.project.mapSkills.has(idSkill)) {
			console.log('SEVERE ERROR : Unregistered skill %s for the project %s',
				event.detail.data.value,
				this.projectService.project.name);
			return;
		}

		if (traceOn()) {
			console.log('Removing the skill', event.detail.data.value);
		}

		this.projectService.project.mapSkills.delete(idSkill);

		// We have already loaded or saved the project, so we can remove each new skill one by one.
		if (this.projectService.project.id) {
			this.updateSkill(
				this.projectService.project.id,
				idSkill,
				this.projectService.delSkill$.bind(this.projectService));
		}

		// Log the resulting collection.
		this.logProjectSkills();
	}

	/**
	 * Update a skill inside a project. This might be an addition or a removal.
	 * @param idProject the project identifier
	 * @param idSkill the skill identifier
	 * @param callback the callback function, which might be **projectService.addSkill** or **projectService.delSkill**
	 */
	public updateSkill(idProject: number, idSkill: number,
		callback: (idProject: number, idSkill: number) => Observable<Boolean>) {
		callback(idProject, idSkill)
			.subscribe({
				next: result => {
					if (result) {
						this.projectService.actualizeProject(idProject);
					}
				},
				error: error => {
					if (traceOn()) {
						console.log(`Error ${error.code} ${error.message}`);
					}
					this.messageService.error(error.message);
				}
			}
		);
	}

	/**
	 * Log the skills of the current project in DEBUG mode.
	 */
	logProjectSkills() {
		if (traceOn()) {
			console.groupCollapsed('list of skills for project ' + this.projectService.project.name);
			for (const [idSkill, profilSkill] of this.projectService.project.mapSkills) {
				console.log(idSkill, this.skillService.title(idSkill));
			}
			console.groupEnd();
		}
	}
}
