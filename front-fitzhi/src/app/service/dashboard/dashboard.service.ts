import { Injectable } from '@angular/core';
import { SkillService } from '../../skill/service/skill.service';
import { ProjectService } from '../project/project.service';
import { SkillProjectsAggregation } from './skill-projects-aggregration';
import { Project } from 'src/app/data/project';
import { StaffListService } from '../staff-list-service/staff-list.service';
import { Collaborator } from 'src/app/data/collaborator';
import { Experience } from 'src/app/data/experience';
import { StatTypes } from './stat-types';
import * as _ from 'lodash';
import { traceOn } from 'src/app/global';
import { ReferentialService } from '../referential/referential.service';
import { DashboardColor } from './dashboard-color';

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
		private referentialService: ReferentialService,
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
				console.log(this.skillService.title(Number(key)), aggregation[key]);
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
			return this.processSkillDistributionFilesSize(minimumLevel, aggregationProjects, aggregationStaff);
		}
		if (statTypes === StatTypes.NumberOfFiles) {
			return this.processSkillDistributionNumberOfFiles(minimumLevel, aggregationProjects, aggregationStaff);
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
		for (let ind = DashboardService.MAX_NUMBER_SKILLS_IN_DIAGRAM; ind < entries.length; ind++) {
			entries[cumulIndex].sumNumberOfFiles += entries[ind].sumNumberOfFiles;
			entries[cumulIndex].sumTotalFilesSize += entries[ind].sumTotalFilesSize;
		}
		entries.splice(DashboardService.MAX_NUMBER_SKILLS_IN_DIAGRAM, entries.length - DashboardService.MAX_NUMBER_SKILLS_IN_DIAGRAM);
		return entries;
	}

	/**
	 * Process the developers distribution per skill in order to evaluate the level of risk for each skill.
	 * The calculation is based on a minimum level.
	 * Therefore, the result might be applicable either for **all** developers, or for **senior** developers.
	 *
	 * **The data aggregated in this method is the total files size**.
	 *
	 * This method aims to answer questions as :
	 * - _Do we have enough developers in Java?_
	 * - _Do we have enough skilled developers in Java?_
	 *
	 * @param minimumLevel the starting level required in the skill
	 * @param aggregationProjects the projects data
	 * @param aggregationStaff the set of developers filtered on the minimum level
	 * @returns the tiles ready to be drawn in the treeMap chart
	 */
	processSkillDistributionFilesSize(minimumLevel: number, aggregationProjects: SkillProjectsAggregation[], aggregationStaff: any) {

		const sumAllTotalFilesSize = _.sumBy(aggregationProjects, 'sumTotalFilesSize');
		if (traceOn()) {
			console.log ('processSkillDistributionFilesSize(%d, ...) -> %d', minimumLevel, sumAllTotalFilesSize);
		}

		const sortedRepo = _.sortBy(aggregationProjects, [function (o) { return -o.sumTotalFilesSize; }]);
		const aggregateData = this.aggregateRestOfData(sortedRepo);

		const tiles = [];

		aggregateData.forEach(projectAggregation => {
			const title = this.skillService.title(Number(projectAggregation.idSkill));
			const size = Math.round((projectAggregation.sumTotalFilesSize * 100 / sumAllTotalFilesSize));
			//
			// The skill proves to have file with signifant sizes
			//
			const staffCount = (aggregationStaff[projectAggregation.idSkill]) ? aggregationStaff[projectAggregation.idSkill] : 0;
			if (size > 0) {
				const color = this.colorTile(minimumLevel, projectAggregation.sumTotalFilesSize, staffCount);
				tiles.push({ name: title, value: size, color: color });
			}
		});

		if (traceOn()) {
			console.groupCollapsed('%d staff tiles', tiles.length);
			tiles.forEach(tile => console.log(tile.name, 'size : ' + tile.value + ' & color : ' + tile.color));
			console.groupEnd();
		}

		return tiles;
	}

	/**
	 * Process the developers distribution per skill in order to evaluate the level of risk for each skill.
	 * The calculation is based on a minimum level.
	 * Therefore, the result might be applicable either for **all** developers, or for **senior** developers.
	 *
	 * **The data aggregated in this method is the number of files**.
	 *
	 * This method aims to answer questions as :
	 * - _Do we have enough developers in Java?_
	 * - _Do we have enough skilled developers in Java?_
	 *
	 * @param minimumLevel the starting level required in the skill
	 * @param aggregationProjects the projects data
	 * @param aggregationStaff the set of developers filtered on the minimum level
	 * @returns the tiles ready to be drawn in the treeMap chart
	 */
	processSkillDistributionNumberOfFiles(minimumLevel: number, aggregationProjects: SkillProjectsAggregation[], aggregationStaff: any) {

		if (traceOn()) {
			console.log ('processSkillDistributionNumberOfFiles(%d, ...)', minimumLevel);
		}

		const sortedRepo = _.sortBy(aggregationProjects, 'sumNumberOfFiles');
		const aggregateData = this.aggregateRestOfData(sortedRepo);

		const tiles = [];
		aggregateData.forEach(projectAggregation => {
			const title = this.skillService.title(Number(projectAggregation.idSkill));
			tiles.push({ name: title, value: projectAggregation.sumTotalFilesSize });
		});
		return tiles;
	}

	/**
	 * Calculate and return the color of a tile.
	 *
	 * This color figures the number of active developers available for this skill.
	 *
	 * @param minimumLevel the starting level required in the skill
	 * @param sumTotalFilesSize the total files size in this skill
	 * @param countStaff the number of staff members with this skill
	 *
	 * @returns the string representation of a color in HTML
	 */
	colorTile(minimumLevel: number, sumTotalFilesSize: number, countStaff: any): string {

		// The rate might be negative if we exceed the perfection. Is perfection is possible ? The answer is NO.
		const rate = Math.max(
			1 - countStaff / (sumTotalFilesSize * this.referentialService.optimalStaffNumberPerMoOfCode[minimumLevel - 1] / 1000000), 0);
		const indexColor = Math.round(rate * 10);

		const color = DashboardColor.rgb(indexColor); 
		//
		// These lines are commented, too chatty...
		// 		if (traceOn()) {
		// 			console.log('Calculated rate for %d %d : %d producing the index %d', sumTotalFilesSize, countStaff, rate, indexColor);
		// 		}
		return color;
	}

	/**
	 * Calculate the distribution of projects with their level of risk.
	 *
	 * @returns the distribution array
	 */
	public processProjectsDistribution(): any[] {

		const distribution = [];

		function sizeOfProject(project: Project) {
			if (project.mapSkills.size === 0) {
				return 0;
			}

			return Array.from(project.mapSkills.values())
				.map(pj => pj.totalFilesSize)
				.reduce(function (a, b) {
					return a + b;
				});
		}

		if (traceOn()) {
			console.groupCollapsed('Global evaluation for each project :');
			this.projectService.allProjects
				.filter((project: Project) => project.active)
				.forEach(project => {
					console.log(project.name, this.projectService.globalEvaluation(project));
				});
			console.groupEnd();
		}
		this.projectService.allProjects
			.filter((project: Project) => project.active)
			.forEach(project => {
				distribution.push({
					id: project.id,
					name: project.name,
					value: sizeOfProject(project),
					color: this.projectService.getRiskColor(this.projectService.globalEvaluation(project))
				});
			});

		return distribution;
	}

}

