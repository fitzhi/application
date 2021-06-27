import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { Collaborator } from 'src/app/data/collaborator';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
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
	let staffService: StaffService;
	let messageBoxService: MessageBoxService;

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

		messageBoxService = TestBed.inject(MessageBoxService);

		staffService = TestBed.inject(StaffService);

		const staff = new Collaborator();
		staff.idStaff = 1789;
		staff.firstName = 'Emmanuel';
		staff.lastName = 'Macron';
		staffService.changeCollaborator(staff);

		fixture.detectChanges();

	});

	function setField(id: string, content: any) {
		component.profileStaff.get(id).setValue(content);
	}

	function field(id: string): HTMLInputElement {
		return (fixture.nativeElement.querySelector(id) as HTMLInputElement);
	}

	it('handle correctly the ACTIVATION of a developer.', () => {

		const spyswitchActiveStatus = spyOn(staffService, 'switchActiveStatus').and.returnValue();
		const spyDisplayActiveOrInactiveLabels = spyOn(component, 'displayActiveOrInactiveLabels').and.returnValue();
		const spyEnableDisableWidgets = spyOn(component, 'enableDisableWidgets').and.returnValue();

		// active is set to false
		component.staff.active = false;
		setField('active', true);
		fixture.detectChanges();

		component.onChange(component.IS_ACTIVE);
		expect(component.staff.active).toBeTruthy();
		expect(component.staff.dateInactive).toBeNull();

		expect(spyswitchActiveStatus).toHaveBeenCalled();
		expect(spyDisplayActiveOrInactiveLabels).toHaveBeenCalled();
		expect(spyEnableDisableWidgets).toHaveBeenCalled();
	});

	it('handle correctly the DEACTIVATION of a developer.', () => {

		const spyswitchActiveStatus = spyOn(staffService, 'switchActiveStatus').and.returnValue();
		const spyDisplayActiveOrInactiveLabels = spyOn(component, 'displayActiveOrInactiveLabels').and.returnValue();
		const spyEnableDisableWidgets = spyOn(component, 'enableDisableWidgets').and.returnValue();

		// active is set to false
		component.staff.active = true;
		setField('active', false);
		fixture.detectChanges();

		component.onChange(component.IS_ACTIVE);
		expect(component.staff.active).toBeFalse();
		expect(component.staff.dateInactive.getSeconds()).toEqual(new Date().getSeconds());

		expect(spyswitchActiveStatus).toHaveBeenCalled();
		expect(spyDisplayActiveOrInactiveLabels).toHaveBeenCalled();
		expect(spyEnableDisableWidgets).toHaveBeenCalled();
	});

	it('ENABLE the status which allows a developer to activate/deactivate a developer.', () => {

		const spyProcessActiveStatus = spyOn(staffService, 'processActiveStatus').and.returnValue();
		const spyHandleCheckbox = spyOn(component, 'handleCheckbox').and.callThrough();

		// active is set to false
		component.staff.forceActiveState = false;
		setField('forceActiveState', true);
		fixture.detectChanges();

		component.onChange(component.FORCEACTIVESTATE);
		expect(component.staff.forceActiveState).toBeTrue();

		expect(spyProcessActiveStatus).not.toHaveBeenCalled();
		expect(spyHandleCheckbox).toHaveBeenCalled();
	});

	it('DISABLE the status which allows a developer to activate/deactivate a developer.', () => {

		const spyProcessActiveStatus = spyOn(staffService, 'processActiveStatus').and.returnValue();
		const spyHandleCheckbox = spyOn(component, 'handleCheckbox').and.callThrough();

		// active is set to false
		component.staff.forceActiveState = true;
		setField('forceActiveState', false);
		fixture.detectChanges();

		component.onChange(component.FORCEACTIVESTATE);
		expect(component.staff.forceActiveState).toBeFalse();
		expect(component.profileStaff.get('active').disabled).toBeTrue();

		expect(spyProcessActiveStatus).toHaveBeenCalled();
		expect(spyHandleCheckbox).toHaveBeenCalled();
	});

	it('display a question message if the firstname & the lastname have changed.', () => {

		const spyQuestion = spyOn(messageBoxService, 'question').and.returnValue(of(true));

		setField('firstName', 'Frédéric');
		setField('lastName', 'VIDAL');
		fixture.detectChanges();

		component.onChange(component.LAST_NAME);

		expect(spyQuestion).toHaveBeenCalled();
	});

	it('do not display a question message if only the firstname or the password have changed.', () => {

		const spyQuestion = spyOn(messageBoxService, 'question').and.returnValue(of(true));

		setField('firstName', 'Frédéric');
		fixture.detectChanges();

		component.onChange(component.LAST_NAME);

		expect(spyQuestion).not.toHaveBeenCalled();
	});

});
