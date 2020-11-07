import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ProjectFloor } from 'src/app/data/project-floor';
import { SkylineAnimation } from 'src/app/data/skyline-animation';

import { SkylineService } from './skyline.service';

describe('SkylineService', () => {
	let service: SkylineService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule]
		});
		service = TestBed.inject(SkylineService);
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
		expect(building.height).toBe(240);
		expect(building.index).toBe(5);

	});

	
});
