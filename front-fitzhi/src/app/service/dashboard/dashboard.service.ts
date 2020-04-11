import { Injectable } from '@angular/core';
import { SkillService } from '../skill.service';
import { ProjectService } from '../project.service';
import { SkillProjectsAggregation } from './skill-projects-aggregration';
import { ProjectSkill } from 'src/app/data/project-skill';
import { Project } from 'src/app/data/project';
import {StaffListService} from '../../staff-list-service/staff-list.service';
import * as _ from 'lodash';
import { Collaborator } from 'src/app/data/collaborator';
import { Experience } from 'src/app/data/experience';
import { StaffSkills } from 'src/app/data/staff-skills';

/**
 * This service is in charge of the calculation for global staff & skill analysis.
 */
@Injectable({
	providedIn: 'root'
})
export class DashboardService {

	constructor(
		private skillService: SkillService,
		private staffListService: StaffListService,
		private projectService: ProjectService) { }

	/**
	 * Return an array containing the aggregation by skill of **numberOfFiles** & **totalFilesSize**
	 */
	public aggregateProjectsBySkills(): SkillProjectsAggregation[] {

		function skills(project: Project) {
			return Array.from(project.mapSkills.values());
		}

		// Flat the projects skills of all projects.
		const skillProjects = _.flatMap(_.filter(this.projectService.allProjects, 'active'), skills);

		// Aggregate by skills, all 'numberOfFiles' & 'totalFilesSize'.
		const aggregation = _(skillProjects)
			.groupBy('idSkill')
			.map((skill, id) => ({
				idSkill: id,
				sumNumberOfFiles: _.sumBy(skill, 'numberOfFiles'),
				sumTotalFilesSize: _.sumBy(skill, 'totalFilesSize')
			}))
			.value();

		return aggregation;
	}

	/**
	 * Return the number of staff member group by skill.
	 * @param includeExternal if it's **true**, all staff members are included in the analysis, otherwise only interns
	 * @param level the minimum level required to be included in the analysis. if the level is equal to 1, all levels (*) are involved
	 */
	public countStaffBySkills(includeExternal: boolean, level: number): any {

		const elligibleStaff = (includeExternal) ?
			_.filter(this.staffListService.allStaff, 'active') :
			_.filter(this.staffListService.allStaff, { 'active': true, 'external': false });

		function experiences(staff: Collaborator) {
			return staff.experiences;
		}

		const staffExperiences = _.flatMap(elligibleStaff, experiences);

		function skill(experience: Experience) {
			return experience.id;
		}

		// Aggregate by skills, all 'numberOfFiles' & 'totalFilesSize'.
		const aggregation = _.countBy(staffExperiences, skill);

		Object.keys(aggregation).forEach(key => {
			console.log (key, aggregation[key]);
		});

		return aggregation;
	}
}
