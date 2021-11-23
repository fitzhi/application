import { ComponentFixture, TestBed, tick, TestModuleMetadata, waitForAsync } from '@angular/core/testing';

import { StaffFormComponent } from './staff-form.component';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { Profile } from '../../data/profile';
import { InitTest } from 'src/app/test/init-test';
import { RouterTestingModule } from '@angular/router/testing';
import { Collaborator } from 'src/app/data/collaborator';
import { StaffService } from '../service/staff.service';

describe('StaffFormComponent', () => {
	let component: StaffFormComponent;
	let fixture: ComponentFixture<StaffFormComponent>;

	beforeEach(waitForAsync(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [StaffFormComponent],
			providers: [],
			imports: [RouterTestingModule.withRoutes([])]
		};
		InitTest.addImports(testConf.imports);
		InitTest.addProviders(testConf.providers);
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {

		fixture = TestBed.createComponent(StaffFormComponent);
		component = fixture.componentInstance;

		const referentialService = TestBed.inject(ReferentialService);
		referentialService.profiles = [];
		referentialService.profiles.push (new Profile('one Code', 'labelOfCode for One'));
		referentialService.profiles.push (new Profile('code nope', 'another labelOfCode'));

		const staffService = TestBed.inject(StaffService);

		staffService.changeCollaborator(
			{
				idStaff: 2019, firstName: 'Joe', lastName: 'DALTON',
				nickName: 'joe', login: 'jdalton',
				email: 'jdalton@gmail.com', level: 'one Code',
				active: false, dateInactive: new Date('2019-11-01'),
				forceActiveState: false, external: false,
				missions: [], experiences: []
			}
		);
		fixture.detectChanges();
	});

	function okDisabled(): boolean {
		return fixture.nativeElement.querySelector('#ok').disabled;
	}

	function setField(id: string, content: string) {
		component.profileStaff.get(id).setValue(content);
	}

	function field(id: string): HTMLInputElement {
		return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
	}

	it('set all fields readonly for an existing INACTIVE staff member.', () => {
		expect(component).toBeTruthy();

		expect(field('#firstName').value).toEqual('Joe');
		expect(field('#firstName').readOnly).toBeTruthy();

		expect(field('#lastName').value).toEqual('DALTON');
		expect(field('#lastName').readOnly).toBeTruthy();

		expect(field('#nickName').value).toEqual('joe');
		expect(field('#nickName').readOnly).toBeTruthy();

		expect(field('#login').value).toEqual('jdalton');
		expect(field('#login').readOnly).toBeTruthy();

		expect(field('#email').value).toEqual('jdalton@gmail.com');
		expect(field('#email').readOnly).toBeTruthy();

		expect(field('#profile').value).toEqual('one Code');
		expect(field('#profile').disabled).toBeTruthy();

		expect(field('#active').checked).toBeFalsy();
		expect(field('#active').disabled).toBeFalsy();

		expect(field('#external').checked).toBeFalsy();
		expect(field('#external').disabled).toBeTruthy();

		fixture.detectChanges();
		expect(component.isAlreadyDesactivated()).toBeTruthy();
		expect(field('#ok').disabled).toBeTruthy();

	});

});
