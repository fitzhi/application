import { TestBed, inject, TestModuleMetadata, async } from '@angular/core/testing';

import { StaffListService } from './staff-list.service';
import { InitTest } from '../../test/init-test';
import { Collaborator } from '../../data/collaborator';

describe('ListStaffService', () => {

	beforeEach(async(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [],
			providers: [],
			imports: []
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	it('should be created', inject([StaffListService], (service: StaffListService) => {
		expect(service).toBeTruthy();
	}));

	it('We should find Frédéric VIDAL', inject([StaffListService], (service: StaffListService) => {
		expect(service).toBeTruthy();

		service.allStaff.push(createStaff(1, 'Frédéric', 'VIDAL', 'frvidal', 'frvidal'));
		service.allStaff.push(createStaff(2, 'Jean-Paul', 'TWO', 'frvidal', 'frvidal'));
		service.allStaff.push(createStaff(3, 'Jacques', 'VIDAL', 'father', 'father'));

		expect(service.lookupSimilarStaff(createStaff(1, 'Fred', 'VIDAL', 'frvidal', 'frvidal'))).toBeUndefined();
		expect(service.lookupSimilarStaff(createStaff(2, 'frédéric', 'vidal', 'frvidal', 'frvidal'))).toBeDefined();

	}));

	it('We should find Jean-Paul 2 in the Staff team when a Jean Paul 2 has been saved',
		inject([StaffListService], (service: StaffListService) => {

		expect(service).toBeTruthy();

		service.allStaff.push(createStaff(1, 'Jean Paul', '2', 'jp2', 'jp2'));

		expect(service.lookupSimilarStaff(createStaff(1, 'Jean-Paul7', '2', 'jp2', 'jp2'))).toBeUndefined();

	}));


	function createStaff (idStaff: number, firstName: string, lastName: string, nickName: string, login: string) {
		const collab = new Collaborator();
		collab.idStaff = idStaff;
		collab.firstName = firstName;
		collab.lastName = lastName;
		collab.nickName = nickName;
		collab.login = login;
		return collab;
	}

});


