import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ReferentialService } from 'src/app/service/referential.service';
import { InitTest } from 'src/app/test/init-test';
import { Profile } from '../../data/profile';
import { StaffService } from '../service/staff.service';
import { StaffFormComponent } from './staff-form.component';

/**
 * Testing the method StaffFormComponent.onChange(...)
 */
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

		const referentialService = TestBed.inject(ReferentialService);
		referentialService.profiles = [];
		referentialService.profiles.push (new Profile('one Code', 'labelOfCode for One'));
		referentialService.profiles.push (new Profile('code nope', 'another labelOfCode'));

		const staffDataExchangeService = TestBed.inject(StaffService);
		component.idStaff = 2019;

		staffDataExchangeService.changeCollaborator(
			{
				idStaff: 2019, firstName: 'Joe', lastName: 'DALTON',
				nickName: 'joe', login: 'jdalton',
				email: 'jdalton@gmail.com', level: 'one Code',
				forceActiveState: true, active: true, dateInactive: null,
				external: false,
				missions: [], experiences: []
			}
		);

		fixture.detectChanges();

	});

	function setField(id: string, content: string) {
		component.profileStaff.get(id).setValue(content);
	}

	function field(id: string): HTMLInputElement {
		return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
	}

	it('handle correctly the de-activation of a developer.', () => {
		console.log (field('#active'));
		component.onChange(component.IS_ACTIVE);
		expect(component.staff.active).toBeTruthy();

	});

});
