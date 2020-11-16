import { DatePipe } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { Project } from 'src/app/data/project';
import { ProjectFloor } from 'src/app/data/project-floor';
import { SkylineAnimation } from 'src/app/data/skyline-animation';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project.service';
import { ReferentialService } from 'src/app/service/referential.service';

import { SkylineService } from './skyline.service';

describe('SkylineService', () => {
	let service: SkylineService;
	let projectService: ProjectService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule, MatDialogModule],
			providers: [DatePipe, ReferentialService, CinematicService ]
		});
		service = TestBed.inject(SkylineService);
		projectService = TestBed.inject(ProjectService);
		projectService.allProjects = [];
		projectService.allProjects.push(new Project(1, 'One'));
	});

	it('should be created', () => {
		expect(service).toBeTruthy();

	});

	it('Testing the method function evaluateMaxNumberOfLines(...)', () => {
		expect(service).toBeTruthy();
		const floors: ProjectFloor[] = [];
		floors.push( {idProject: 1, year:2020, week: 10, linesActiveDevelopers: 10, linesInactiveDevelopers: 30})
		floors.push( {idProject: 1, year:2020, week: 11, linesActiveDevelopers: 20, linesInactiveDevelopers: 300})
		floors.push( {idProject: 1, year:2020, week: 12, linesActiveDevelopers: 30, linesInactiveDevelopers: 300})
		expect(service.evaluateMaxNumberOfLines(floors)).toBe(330);
	});

	it('Testing the method function generateSkylineToDraw(...)', () => {
		const skylineAnimation = new SkylineAnimation();
		skylineAnimation.floors = [];
		skylineAnimation.floors.push ( {idProject: 1, year:2020, week: 10, linesActiveDevelopers: 50, linesInactiveDevelopers: 50});
		const skylineToBeDrawn = service.generateSkylineToDraw(skylineAnimation, 1000, 300);
		console.table (skylineToBeDrawn);
		expect(skylineToBeDrawn.length).toBe(1);
		const building = skylineToBeDrawn[0];
		expect(building.id).toBe(1);
		expect(building.year).toBe(2020);
		expect(building.week).toBe(10);
		expect(building.width).toBe(40);
		expect(building.height).toBe(300);
		expect(building.index).toBe(50);
		expect(building.title).toBe('One');

	});

	
});
