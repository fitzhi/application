import { TestBed, TestModuleMetadata } from '@angular/core/testing';

import { TabsStaffListService } from './tabs-staff-list.service';
import { InitTest } from 'src/app/test/init-test';
import { StaffService } from 'src/app/tabs-staff/service/staff.service';
import { of } from 'rxjs';
import { Collaborator } from 'src/app/data/collaborator';
import { take } from 'rxjs/operators';
import { SkillService } from 'src/app/skill/service/skill.service';
import { Skill } from 'src/app/data/skill';

describe('TabsStaffListService', () => {

	let staffService: StaffService;
	let skillService: SkillService;
	let service: TabsStaffListService;

	beforeEach(() => {
		const testConf: TestModuleMetadata =  {
				declarations: [],
				providers: [StaffService],
				imports: []
			};
			InitTest.addImports(testConf.imports);
			InitTest.addProviders(testConf.providers);
			TestBed.configureTestingModule(testConf).compileComponents();

			staffService = TestBed.inject(StaffService);
			skillService = TestBed.inject(SkillService);
			service = TestBed.inject(TabsStaffListService);

	});

	it('should be created without error', () => {
		expect(service).toBeTruthy();
	});

	it('should handle a simple search just on the lastname', done => {
		expect(service).toBeTruthy();
		
		const spy = spyOn(staffService, 'getAll').and.returnValue(
			of([
				{
					'idStaff': 1789,
					'firstName': 'Frédéric',
					'lastName': 'VIDAL',
					'login': 'frvidal',
					'active': true,
				}
			])
		);

		service.search('ViDa', true, service).pipe(take(1)).subscribe({
			next: c => {
				expect(c.length).toBe(1);
				expect(c[0].idStaff).toBe(1789);
				expect(c[0].firstName).toBe('Frédéric');
				expect(c[0].lastName).toBe('VIDAL');
				done();
			}
		});
	});

	it('should exclude inactive staff from the result list', done => {
		expect(service).toBeTruthy();
		
		const spy = spyOn(staffService, 'getAll').and.returnValue(
			of([
				{
					'idStaff': 1789,
					'firstName': 'Frédéric',
					'lastName': 'VIDAL',
					'login': 'frvidal',
					'active': false,
				}
			])
		);

		service.search('ViDa', true, service).pipe(take(1)).subscribe({
			next: c => {
				expect(c.length).toBe(0);
				done();
			}
		});
	});

	it('should treat a staff member with an undefined active/inactive state as inactive', done => {
		expect(service).toBeTruthy();
		
		const spy = spyOn(staffService, 'getAll').and.returnValue(
			of([
				{
					'idStaff': 1789,
					'firstName': 'Frédéric',
					'lastName': 'VIDAL',
					'login': 'frvidal',
				}
			])
		);

		service.search('ViDa', true, service).pipe(take(1)).subscribe({
			next: c => {
				expect(c.length).toBe(0);
				done();
			}
		});
	});

	it('should handle a simple search just on the firstname', done => {
		expect(service).toBeTruthy();
		
		const spy = spyOn(staffService, 'getAll').and.returnValue(
			of([
				{
					'idStaff': 1789,
					'firstName': 'Frédéric',
					'lastName': 'VIDAL',
					'login': 'frvidal',
					'active': true,
				}
			])
		);

		service.search('FRéd', true, service).pipe(take(1)).subscribe({
			next: c => {
				expect(c.length).toBe(1);
				expect(c[0].idStaff).toBe(1789);
				expect(c[0].firstName).toBe('Frédéric');
				expect(c[0].lastName).toBe('VIDAL');
				done();
			}
		});
	});

	it('should select a skill of any level', done => {
		expect(service).toBeTruthy();

		skillService.allSkills = [];
		skillService.allSkills.push(new Skill(1, 'one'));
		skillService.allSkills.push(new Skill(2, 'two'));
		skillService.allSkills.push(new Skill(3, 'three'));

		const spy = spyOn(staffService, 'getAll').and.returnValue(
			of([
				{
					'idStaff': 1789,
					'firstName': 'Frédéric',
					'lastName': 'VIDAL',
					'login': 'frvidal',
					'active': true,
					'experiences': [
						{'id': 1, 'title': 'one', 'level': 3},
						{'id': 2, 'title': 'two', 'level': 2}
					]
				}
			])
		);

		service.search('skill:one', true, service).pipe(take(1)).subscribe({
			next: c => {
				expect(c.length).toBe(1);
				expect(c[0].idStaff).toBe(1789);
				expect(c[0].firstName).toBe('Frédéric');
				expect(c[0].lastName).toBe('VIDAL');
				done();
			}
		});
	});
	
	it('should NOT select a skill of level 4', done => {
		expect(service).toBeTruthy();

		skillService.allSkills = [];
		skillService.allSkills.push(new Skill(1, 'one'));
		skillService.allSkills.push(new Skill(2, 'two'));
		skillService.allSkills.push(new Skill(3, 'three'));

		const spy = spyOn(staffService, 'getAll').and.returnValue(
			of([
				{
					'idStaff': 1789,
					'firstName': 'Frédéric',
					'lastName': 'VIDAL',
					'login': 'frvidal',
					'active': true,
					'experiences': [
						{'id': 1, 'title': 'one', 'level': 3},
						{'id': 2, 'title': 'two', 'level': 2}
					]
				}
			])
		);

		service.search('skill:one:4', true, service).pipe(take(1)).subscribe({
			next: c => {
				expect(c.length).toBe(0);
				done();
			}
		});
	});

	it('should select a skill of level 3', done => {
		expect(service).toBeTruthy();

		skillService.allSkills = [];
		skillService.allSkills.push(new Skill(1, 'one'));
		skillService.allSkills.push(new Skill(2, 'two'));
		skillService.allSkills.push(new Skill(3, 'three'));

		const spy = spyOn(staffService, 'getAll').and.returnValue(
			of([
				{
					'idStaff': 1789,
					'firstName': 'Frédéric',
					'lastName': 'VIDAL',
					'login': 'frvidal',
					'active': true,
					'experiences': [
						{'id': 1, 'title': 'one', 'level': 3},
						{'id': 2, 'title': 'two', 'level': 2}
					]
				}
			])
		);

		service.search('skill:one:3', true, service).pipe(take(1)).subscribe({
			next: c => {
				expect(c.length).toBe(1);
				expect(c[0].idStaff).toBe(1789);
				expect(c[0].firstName).toBe('Frédéric');
				expect(c[0].lastName).toBe('VIDAL');
				done();
			}
		});
	});

});
