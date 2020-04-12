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
import { FilesStats } from 'src/app/data/sonar/FilesStats';
import { TreeMapSlice } from 'src/app/data/tree-map-slice';

enum StatTypes {
	NumberOfFiles = 1,
	FilesSize = 2,
}

/**
 * This service is in charge of the calculation for global staff & skill analysis.
 */
@Injectable({
	providedIn: 'root'
})
export class DashboardService {

	static MAX_NUMBER_SKILLS_IN_DIAGRAM = 10;

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
	 * @param minimumLevel the minimum level required to be included in the analysis. if the level is equal to 1, all levels (*) are involved
	 */
	public countStaffBySkills(includeExternal: boolean, minimumLevel: number): any {

		const elligibleStaff = (includeExternal) ?
			_.filter(this.staffListService.allStaff, 'active') :
			_.filter(this.staffListService.allStaff, { 'active': true, 'external': false });

		function experiences(staff: Collaborator) {
			return staff.experiences;
		}

		const activeStaffExperiences = _.flatMap(elligibleStaff, experiences);

		//
		// Filter the experiences on the given level.
		//
		function hasLevel(experience: Experience): boolean {
			return (experience.level >= minimumLevel);
		}

		const staffExperiences = _.filter(activeStaffExperiences, hasLevel);

		function skill(experience: Experience) {
			return experience.id;
		}

		// Count the number of staff member group by skills
		const aggregation = _.countBy(staffExperiences, skill);

		Object.keys(aggregation).forEach(key => {
			console.log (key, aggregation[key]);
		});

		return aggregation;
	}

	/**
	 * Process the skill distribution depending on the total of files size by skill.
	 * @param includeExternal if it's **true**, all staff members are included in the analysis, otherwise only interns
	 * @param minimumLevel the minimum level required to be included in the analysis. if the level is equal to 1, all levels (*) are involved
	 */
	public processSkillDistribution(includeExternal: boolean, minimumLevel: number, statTypes: StatTypes) {

		const aggregationProjects = this.aggregateProjectsBySkills();

		if (statTypes === StatTypes.FilesSize) {
			return this.processSkillDistributionFilesSize(aggregationProjects);
		}
		if (statTypes === StatTypes.NumberOfFiles) {
			return this.processSkillDistributionNumberOfFiles(aggregationProjects);
		}
		throw new Error('Unknown type of statistics ' + statTypes);
	}

	/**
	 * The diagram is limited to **MAX_NUMBER_SKILLS_IN_DIAGRAM** entries.
	 *
	 * Therefore, we aggregate all the smallest entries into the remainer area.
	 */
	aggregateRestOfData(entries: SkillProjectsAggregation[]): SkillProjectsAggregation[] {

		const cumulIndex = DashboardService.MAX_NUMBER_SKILLS_IN_DIAGRAM - 1;

		// Below the limit, we return the input array without aggregation
		if (entries.length <= DashboardService.MAX_NUMBER_SKILLS_IN_DIAGRAM) {
			return entries;
		}

		//
		// We limit the number of areas to MAX_NUMBER_SKILLS_IN_DIAGRAM.
		// Therefore, we aggregate all the smallest 'sumNumberOfFiles' to this tenth record
		//
		for (let ind = DashboardService.MAX_NUMBER_SKILLS_IN_DIAGRAM;  ind < entries.length; ind++) {
			entries[cumulIndex].sumNumberOfFiles += entries[ind].sumNumberOfFiles;
			entries[cumulIndex].sumTotalFilesSize += entries[ind].sumTotalFilesSize;
		}
		entries.splice(DashboardService.MAX_NUMBER_SKILLS_IN_DIAGRAM, entries.length - DashboardService.MAX_NUMBER_SKILLS_IN_DIAGRAM);
		return entries;
	}

	processSkillDistributionFilesSize(aggregationProjects: SkillProjectsAggregation[]) {

		const sortedRepo = _.sortBy(aggregationProjects, 'sumTotalFilesSize');
		const aggregateData = this.aggregateRestOfData(sortedRepo);

		const tiles  = [];
		aggregateData.forEach(projectAggregation =>
			tiles.push({name: 'skill ' +  projectAggregation.idSkill, value: projectAggregation.sumTotalFilesSize})
		);
		return tiles;
	}

	processSkillDistributionNumberOfFiles(entries: SkillProjectsAggregation[]) {
	}
}
