import { Injectable } from '@angular/core';
import { Project } from 'src/app/data/project';
import { Slice } from '../slice';
import { TypeSlice } from '../type-slice';
import { ProjectService } from 'src/app/service/project.service';
import { Constants } from 'src/app/constants';

/**
 * This service is in charge of the generation of the slices.
 */
@Injectable({
	providedIn: 'root'
})
export class PieDashboardService {

	constructor(private projectService: ProjectService) { }

	/**
	 * Generate the slices building the summary dashboard for an array of projects.
	 * @param projects array of projects (in reality, it will be an array with **ALL** projects)
	 */
	public generatePieSlices (projects: Project[]): Slice[] {

		const greyProjects: Map<TypeSlice, Project[]> = new Map([
			[TypeSlice.Staff, []], [TypeSlice.Audit, []], [TypeSlice.Sonar, []]]);
		const greenProjects: Map<TypeSlice, Project[]> = new Map([
			[TypeSlice.Staff, []], [TypeSlice.Audit, []], [TypeSlice.Sonar, []]]);
		const redProjects: Map<TypeSlice, Project[]> = new Map([
			[TypeSlice.Staff, []], [TypeSlice.Audit, []], [TypeSlice.Sonar, []]]);
		const orangeProjects: Map<TypeSlice, Project[]> = new Map([
			[TypeSlice.Staff, []], [TypeSlice.Audit, []], [TypeSlice.Sonar, []]]);
		projects.forEach(project => {

			/**
			 * Distribution of projects on the staffEvaluation criteria
			 */
			if (project.staffEvaluation === -1) {
				greyProjects.get(TypeSlice.Staff).push(project);
			} else if (project.staffEvaluation <= 3) {
					greenProjects.get(TypeSlice.Staff).push(project);
				} else if (project.staffEvaluation <= 6) {
					orangeProjects.get(TypeSlice.Staff).push(project);
				} else {
					redProjects.get(TypeSlice.Staff).push(project);
			}

			/**
			 * Distribution of projects on the staff Evaluation criteria
			 */
			if (Object.keys(project.audit).length === 0) {
				greyProjects.get(TypeSlice.Audit).push(project);
			} else if (project.auditEvaluation <= 30) {
					redProjects.get(TypeSlice.Audit).push(project);
				} else if (project.auditEvaluation <= 60) {
					orangeProjects.get(TypeSlice.Audit).push(project);
				} else {
					greenProjects.get(TypeSlice.Audit).push(project);
			}

			const sonarEvaluation = this.projectService.calculateSonarEvaluation(project);
			if (sonarEvaluation === 0) {
					greyProjects.get(TypeSlice.Sonar).push(project);
				} else if (sonarEvaluation <= 30) {
					greenProjects.get(TypeSlice.Sonar).push(project);
				} else if (sonarEvaluation <= 60) {
					orangeProjects.get(TypeSlice.Sonar).push(project);
				} else {
					redProjects.get(TypeSlice.Sonar).push(project);
			}

			if (Constants.DEBUG) {
				console.groupCollapsed ('Project %s evaluations :', project.name);
				console.log ('Sonar evaluation %d', sonarEvaluation);
				console.log ('Audit evaluation %d', project.auditEvaluation);
				console.log ('Staff evaluation %d', project.staffEvaluation);
				console.groupEnd ();
			}
		});

		const slices: Slice[] = [];
		this.evaluateSlices(slices, TypeSlice.Sonar, greenProjects, orangeProjects, redProjects, greyProjects);
		this.evaluateSlices(slices, TypeSlice.Audit, greenProjects, orangeProjects, redProjects, greyProjects);
		this.evaluateSlices(slices, TypeSlice.Staff, greenProjects, orangeProjects, redProjects, greyProjects);
		return slices;

	}

	/**
	 * Process the Pie slice
	 * @param type type of slice
	 * @param ...projects 4 maps of projects ___in a specific order___.
	 * - the 'green' projects
	 * - the 'orange' projects
	 * - the 'red' projects
	 * - the 'grey' projects
	 */

	private evaluateSlices(slices: Slice[], type: TypeSlice, ...projects: Map<TypeSlice, Project[]>[])  {
		const green = this.count(type, projects[0]);
		const orange = this.count(type, projects[1]);
		const red = this.count(type, projects[2]);
		const grey = this.count(type, projects[3]);

		const step = 120 / (green + orange + red + grey);

		slices.push(new Slice(slices.length, type, this.nextOffset(slices), step * green, 'green'));
		slices.push(new Slice(slices.length, type, this.nextOffset(slices), step * orange, 'orange'));
		slices.push(new Slice(slices.length, type, this.nextOffset(slices), step * red, 'red'));
		slices.push(new Slice(slices.length, type, this.nextOffset(slices), step * grey, 'grey'));
	}

	/**
	 * Next available offset.
	 * @param slices array of slices
	 */
	private nextOffset(slices: Slice[]) {
		return (slices.length === 0) ? 0 : slices[slices.length - 1].offset + slices[slices.length - 1].angle;
	}

	/**
	 * Count the number of project of the given type.
	 * @param type the type of slice (`Staff`, `Audit`, or `Sonar`)
	 * @param map the given map of projects.
	 */
	private count(type: TypeSlice, map: Map<TypeSlice, Project[]>) {
		return map.get(type).length;
	}


}
