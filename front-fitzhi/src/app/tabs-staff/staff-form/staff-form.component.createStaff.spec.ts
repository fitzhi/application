import { ComponentFixture, TestBed, TestModuleMetadata, waitForAsync } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { InitTest } from 'src/app/test/init-test';
import { Profile } from '../../data/profile';
import { StaffFormComponent } from './staff-form.component';


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

		fixture.detectChanges();
	});

	it('should be successfully created.', () => {
		expect(component).toBeTruthy();
	});

	function okDisabled(): boolean {
		return fixture.nativeElement.querySelector('#ok').disabled;
	}

	function setField(id: string, content: string) {
		component.profileStaff.get(id).setValue(content);
	}

	function field(id: string) {
		return component.profileStaff.controls[id];
	}

	it('should implement the tests of surface.', () => {
		expect(component).toBeTruthy();

		expect(component.classOkButton()).toEqual('okButton okButtonInvalid');
		const ok: HTMLElement = fixture.nativeElement.querySelector('#ok');
		expect(ok.getAttribute('disabled')).toEqual('');

		// FirstName is not mandatory.
		expect(field('firstName').valid).toBeTruthy();

		expect(field('lastName').valid).toBeFalsy();
		setField('lastName', 'VLAID');
		expect(field('lastName').valid).toBeTruthy();
		expect(component.profileStaff.valid).toBeFalsy();

		expect(field('login').valid).toBeFalsy();
		setField('login', 'frvlaid');
		expect(field('login').valid).toBeTruthy();
		expect(component.profileStaff.valid).toBeFalsy();

		expect(field('email').valid).toBeFalsy();
		// Email cannot exceed 64 caracters.
		setField('email', 'X'.repeat(62) + '@gmail.com');
		expect(field('email').valid).toBeFalsy();

		setField('email', 'frvlaid@gmail.com');
		expect(field('email').valid).toBeTruthy();
		expect(component.profileStaff.valid).toBeFalsy();

		expect(field('profile').valid).toBeFalsy();
		setField('profile', 'code nope');
		expect(field('profile').valid).toBeTruthy();

		// The form is complete
		expect(component.profileStaff.valid).toBeTruthy();

		expect(component.classOkButton()).toEqual('okButton okButtonValid');

		fixture.detectChanges();

		expect(ok.getAttribute('disabled')).toBeNull();
	});

});
