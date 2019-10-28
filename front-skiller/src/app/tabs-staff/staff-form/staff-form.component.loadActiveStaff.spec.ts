import { async, ComponentFixture, TestBed, tick } from '@angular/core/testing';

import { StaffFormComponent } from './staff-form.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';
import { NoPreloading } from '@angular/router';
import { ReferentialService } from 'src/app/service/referential.service';
import { Profile } from '../../data/profile';
import { format } from 'url';
import { StaffComponent } from '../staff.component';
import { StaffDataExchangeService } from '../service/staff-data-exchange.service';
import { Collaborator } from 'src/app/data/collaborator';

describe('StaffFormComponent', () => {
	let component: StaffFormComponent;
	let fixture: ComponentFixture<StaffFormComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ ],
			imports: [RootTestModule]
		})
		.compileComponents();
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
				active: true, dateInactive: null,
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

		expect(field('#active').checked).toBeTruthy();
		expect(field('#active').readOnly).toBeFalsy();

		expect(field('#external').checked).toBeFalsy();
		expect(field('#external').readOnly).toBeFalsy();
	});

});
