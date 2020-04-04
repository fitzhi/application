import { async, ComponentFixture, TestBed, tick, TestModuleMetadata } from '@angular/core/testing';

import { StaffFormComponent } from './staff-form.component';
import { ReferentialService } from 'src/app/service/referential.service';
import { Profile } from '../../data/profile';
import { StaffDataExchangeService } from '../service/staff-data-exchange.service';
import { InitTest } from 'src/app/test/init-test';
import { RouterTestingModule } from '@angular/router/testing';

describe('StaffFormComponent', () => {
	let component: StaffFormComponent;
	let fixture: ComponentFixture<StaffFormComponent>;

	beforeEach(async(() => {
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

		const referentialService = TestBed.get(ReferentialService);
		referentialService.profiles = [];
		referentialService.profiles.push (new Profile('one Code', 'labelOfCode for One'));
		referentialService.profiles.push (new Profile('code nope', 'another labelOfCode'));

		const staffDataExchangeService = TestBed.get(StaffDataExchangeService);
		component.idStaff = 2019;

		staffDataExchangeService.changeCollaborator(
			{
				idStaff: 2019, firstName: 'Joe', lastName: 'DALTON',
				nickName: 'joe', login: 'jdalton',
				email: 'jdalton@gmail.com', level: 'one Code',
				forceActiveState: true, active: true, dateInactive: null,
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

	it('load an existing staff member', () => {
		expect(component).toBeTruthy();
		expect(field('#firstName').value).toEqual('Joe');
		expect(field('#firstName').readOnly).toBeFalsy();

		expect(field('#lastName').value).toEqual('DALTON');
		expect(field('#lastName').readOnly).toBeFalsy();

		expect(field('#nickName').value).toEqual('joe');
		expect(field('#nickName').readOnly).toBeFalsy();

		expect(field('#login').value).toEqual('jdalton');
		expect(field('#login').readOnly).toBeFalsy();

		expect(field('#email').value).toEqual('jdalton@gmail.com');
		expect(field('#email').readOnly).toBeFalsy();

		expect(field('#profile').value).toEqual('one Code');
		expect(field('#profile').readOnly).toBeFalsy();

		expect(field('#forceActiveState-input').checked).toBeTruthy();
		expect(field('#forceActiveState-input').readOnly).toBeFalsy();

		expect(field('#active-input').checked).toBeTruthy();
		expect(field('#active-input').readOnly).toBeFalsy();

		expect(field('#external').checked).toBeFalsy();
		expect(field('#external').readOnly).toBeFalsy();
	});

});
