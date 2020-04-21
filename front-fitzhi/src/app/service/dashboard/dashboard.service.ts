import { Injectable } from '@angular/core';
import { SkillService } from '../skill.service';
import { ProjectService } from '../project.service';
import { SkillProjectsAggregation } from './skill-projects-aggregration';
import { Project } from 'src/app/data/project';
import {StaffListService} from '../../staff-list-service/staff-list.service';
import { Collaborator } from 'src/app/data/collaborator';
import { Experience } from 'src/app/data/experience';
import { StatTypes } from './stat-types';
import * as _ from 'lodash';
import { traceOn } from 'src/app/global';

/**
 * This service is in charge of the calculation for global staff & skill analysis.
 */
@Injectable({
	providedIn: 'root'
})
export class DashboardService {

	static MAX_NUMBER_SKILLS_IN_DIAGRAM = 10;

	static OPTIMAL_NUMBER_OF_STAFF_PER_1000_K_OF_CODE = 3;

	static red (index: number): string {
		const s = Math.round(28 + ((139 - 28) * index) / 10).toString(16).toUpperCase();
		return (s.length === 1) ? '0' + s : s;
	}

	static green (index: number) {
		const s = Math.round((183 - (183 * index) / 10)).toString(16).toUpperCase();
		return (s.length === 1) ? '0' + s : s;
	}

	static blue (index: number) {
		const s = Math.round((69 - (69 * index) / 10)).toString(16).toUpperCase();
		return (s.length === 1) ? '0' + s : s;
	}

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

		if (traceOn()) {
			console.groupCollapsed('count all staff group by skill');
			Object.keys(aggregation).forEach(key => {
				console.log (this.skillService.title(Number(key)), aggregation[key]);
			});
			console.groupEnd();
		}

		return aggregation;
	}

	/**
	 * Process the skill distribution depending on the total of files size by skill.
	 * @param includeExternal if it's **true**, all staff members are included in the analysis, otherwise only interns
	 * @param minimumLevel the minimum level required to be included in the analysis. if the level is equal to 1, all levels (*) are involved
	 */
	public processSkillDistribution(includeExternal: boolean, minimumLevel: number, statTypes: StatTypes): any[] {

		// Count the number of projects group by skills;
		const aggregationProjects = this.aggregateProjectsBySkills();

		// Calculate the number of staff group by skills
		const aggregationStaff = this.countStaffBySkills(includeExternal, minimumLevel);

		if (statTypes === StatTypes.FilesSize) {
			return this.processSkillDistributionFilesSize(aggregationProjects, aggregationStaff);
		}
		if (statTypes === StatTypes.NumberOfFiles) {
			return this.processSkillDistributionNumberOfFiles(aggregationProjects, aggregationStaff);
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

	processSkillDistributionFilesSize(aggregationProjects: SkillProjectsAggregation[], aggregationStaff: any) {

		const sumAllTotalFilesSize = _.sumBy(aggregationProjects, 'sumTotalFilesSize');
		if (traceOn()) {
			console.log ('sumAllTotalFilesSize', sumAllTotalFilesSize);
		}
		const sortedRepo = _.sortBy(aggregationProjects, [ function(o) { return -o.sumTotalFilesSize; }]);
		const aggregateData = this.aggregateRestOfData(sortedRepo);

		const tiles  = [];

		aggregateData.forEach (projectAggregation => {
			const title = this.skillService.title(Number(projectAggregation.idSkill));
			const size = Math.round((projectAggregation.sumTotalFilesSize * 100 / sumAllTotalFilesSize));
			//
			// The skill proves to have file with signifant sizes
			//
			const staffCount = (aggregationStaff[projectAggregation.idSkill]) ? aggregationStaff[projectAggregation.idSkill] : 0;
			if (size > 0) {
				const color = this.colorTile(projectAggregation.sumTotalFilesSize, staffCount);
				tiles.push({name: title, value: size, color: color});
			}
		});

		if (traceOn()) {
			console.groupCollapsed('%d staff tiles', tiles.length);
			tiles.forEach(tile => console.log (tile.name, 'size : ' + tile.value + ' & color : ' + tile.color));
			console.groupEnd();
		}

		return tiles;
	}

	processSkillDistributionNumberOfFiles(aggregationProjects: SkillProjectsAggregation[], aggregationStaff: any) {
		const sortedRepo = _.sortBy(aggregationProjects, 'sumNumberOfFiles');
		const aggregateData = this.aggregateRestOfData(sortedRepo);

		const tiles  = [];
		aggregateData.forEach(projectAggregation => {
			const title = this.skillService.title(Number(projectAggregation.idSkill));
			tiles.push({name: title, value: projectAggregation.sumTotalFilesSize});
		});
		return tiles;
	}

	/**
	 * Calculate and return the color of a tile.
	 *
	 * This color figures the number of active developers available for this skill
	 * @param sumTotalFilesSize the total files size in this skill
	 * @param countStaff the number of staff members with this
	 */
	colorTile(sumTotalFilesSize: number, countStaff: any): string {
		// The rate might be negative if we exceed the perfection. The response is NO.
		const rate = Math.max(
			1 - countStaff / (sumTotalFilesSize * DashboardService.OPTIMAL_NUMBER_OF_STAFF_PER_1000_K_OF_CODE / 1000000), 0);
		const indexColor = Math.round(rate * 10);
		const color = '#' + DashboardService.red(indexColor) + DashboardService.green(indexColor) + DashboardService.blue(indexColor);

//
// These lines are commented, too chatty...
// 		if (traceOn()) {
// 			console.log('Calculated rate for %d %d : %d producing the index %d', sumTotalFilesSize, countStaff, rate, indexColor);
// 		}
		return color;
	}

}

