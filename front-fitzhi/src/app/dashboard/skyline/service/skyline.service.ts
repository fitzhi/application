import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Building } from 'rising-skyline';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { switchMap, take } from 'rxjs/operators';
import { ProjectFloor } from 'src/app/data/project-floor';
import { SkylineAnimation } from 'src/app/data/skyline-animation';
import { traceOn } from 'src/app/global';
import { ProjectService } from 'src/app/service/project.service';

const httpOptions = {
	headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
	providedIn: 'root'
})
export class SkylineService {

	/**
	 * Default width for each project building.
	 */
	private defaultWidth = 40.

	/**
	 * Ths skyline is loaded.
	 */
	public skylineLoaded$ = new BehaviorSubject<boolean>(false);

	/**
	 * Thes skyline to be drawn.
	 */
	public skyline$ = new  BehaviorSubject<Building[]>([]);

	constructor(
		private httpClient: HttpClient,
		private projectService: ProjectService) { }

	/**
	 * Load the skyline from the Fitzhì backend.
	 * @param width the width of the skyline. _this parameter is not actually used_ 
	 * @param height the height of the skyline
	 * @returns an observable of buildings
	 */
	public loadSkyline$(width: number, height: number): Observable<Building[]> {
		return this.httpClient
			.get<SkylineAnimation>(localStorage.getItem('backendUrl') + '/api/skyline', httpOptions)
			.pipe(
				take(1),
				switchMap( skyline => {
					if (traceOn()) {
						console.groupCollapsed ('Receiving skykine');
						console.table(skyline.floors);
						console.groupEnd();
					}
					const skylineToDraw = this.generateSkylineToDraw(skyline, width, height);
					if (traceOn()) {
						skylineToDraw.sort((a, b) => a.year * 100 + a.week - b.year * 100 - b.week);
						console.groupCollapsed('Skyline ready to be drawn');
						console.table(skylineToDraw);
						console.groupEnd();
					}
					this.skyline$.next(skylineToDraw);
					return this.skyline$;
				}));
	}

	/**
	 * Generate the skyline to be drawn in the skyline component.
	 * @param skyline the skyline received from the Fitzhì backend.
	 * @param width the width of the skyline container.
	 * @param height the height of the skyline container.
	 */
	public generateSkylineToDraw(skyline: SkylineAnimation, width: number, height: number): Building[] {
		const maxNumberOfLines = this.evaluateMaxNumberOfLines(skyline.floors);
		const heightOneLine = height / maxNumberOfLines;
		if (traceOn()) {
			console.log ('The height of the skykine container is', height);
			console.log ('The height of one line is', heightOneLine);
		}
		let i = 0;
		const skylineToDraw = [];

		skyline.floors.forEach(element => {
			const project = this.projectService.getProjectById(element.idProject);
			skylineToDraw.push(
				new Building(
					element.idProject,
					element.year,
					element.week,
					this.defaultWidth,
					Math.floor (heightOneLine * (element.linesActiveDevelopers + element.linesInactiveDevelopers)),
					100 - Math.floor(100 * element.linesActiveDevelopers / (element.linesActiveDevelopers + element.linesInactiveDevelopers)),
					(project) ? project.name : 'undefined'
				)
			)
		});
		return skylineToDraw;
	}


	/**
	 * Evaluate the lines of the largest building
	 * @param floors an array of history floors (step of project)
	 */
	public evaluateMaxNumberOfLines (floors: ProjectFloor[]):  number {
		let maxNumberOfLines = 0;
		floors.forEach(floor => {
			maxNumberOfLines = Math.max(maxNumberOfLines, floor.linesActiveDevelopers + floor.linesInactiveDevelopers);
		});
		if (traceOn()) {
			console.log ('maxNumberOfLines', maxNumberOfLines);
		}
		return maxNumberOfLines;
	}

	public evaluateHeightOfLine() {
		const maxLine = this
	}
}
