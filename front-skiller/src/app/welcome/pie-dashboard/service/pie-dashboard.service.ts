import { Injectable } from '@angular/core';
import { Project } from 'src/app/data/project';
import { Slice } from '../slice';
import { TypeSlice } from '../type-slice';
import { ProjectService } from 'src/app/service/project.service';
import { Constants } from 'src/app/constants';
import { Subject, BehaviorSubject } from 'rxjs';

/**
 * This service is in charge of the generation of the slices.
 */
@Injectable({
	providedIn: 'root'
})
export class PieDashboardService {

	public subject = {
		none: 0,
		staff: 1,
		sonar: 2,
		project: 3
	};

	constructor(private projectService: ProjectService) { }

	/**
	 * `BehaviorSubject` emetting the distributions of projects within slices in the pie.
	 */
	public slices$ = new BehaviorSubject<Slice[]>([]);

	/**
	 * Projects activated when the mouse pointer flies over a slice.
	 */
	public projectsActivated$ = new BehaviorSubject<Project[]>([]);

	/**
	 * Projects header color depending on the slice activated.
	 */
	public projectsHeaderColor$ = new BehaviorSubject<string>('white');

	/**
	 * Subject highlighted.
	 */
	public projectsSubject$ = new BehaviorSubject<number>(0);

	/**
	 * Observable emetting the __last year__ archived `slices$` of the pie.
	 */
	public slicesLastYear$ = new BehaviorSubject<Slice[]>([]);

	/**
	 * Observable emetting the __last month__ archived `slices$` of the pie.
	 */
	public slicesLastMonth$ = new BehaviorSubject<Slice[]>([]);

	/**
	 * Generate the slices building the summary dashboard for an array of projects.
	 * @param projects array of projects (in reality, it will be an array with **ALL** projects)
	 */
	public generatePieSlices (projects: Project[]) {

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

			const sonarEvaluation = this.projectService.calculateSonarEvaluation();
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

		this.slices$.next(slices);
	}

	/**
	 * Process the Pie slice
	 * @param slices the slice
	 * @param typeSlice type of slice
	 * @param projects 4 maps of projects ___in a specific order___
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

		slices.push(new Slice(slices.length, type, this.nextOffset(slices), step * green, 'green', projects[0].get(type)));
		slices.push(new Slice(slices.length, type, this.nextOffset(slices), step * orange, 'orange', projects[1].get(type)));
		slices.push(new Slice(slices.length, type, this.nextOffset(slices), step * red, 'red', projects[2].get(type)));
		slices.push(new Slice(slices.length, type, this.nextOffset(slices), step * grey, 'grey', projects[3].get(type)));
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

	/**
	 * The mouse pointer has moved on a slice.
	 * @param slice the slice activated by the mouse.
	 */
	public onSliceMouseOver(slice: Slice) {

		this.projectsActivated$.next(slice.projects);
		this.projectsHeaderColor$.next(slice.color);
		this.projectsSubject$.next(slice.type);
	}
}
