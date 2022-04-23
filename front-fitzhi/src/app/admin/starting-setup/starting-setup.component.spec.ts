import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSliderModule } from '@angular/material/slider';
import { MatStepperModule } from '@angular/material/stepper';
import { MatTableModule } from '@angular/material/table';
import { By } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { FirstConnection } from 'src/app/data/first-connection';
import { BackendSetupService } from 'src/app/service/backend-setup/backend-setup.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { SkillService } from 'src/app/skill/service/skill.service';
import { BackendSetupComponent } from '../backend-setup/backend-setup.component';
import { StartingSetupComponent } from './starting-setup.component';

describe('StartingSetupComponent', () => {
	let component: StartingSetupComponent;
	let fixture: ComponentFixture<StartingSetupComponent>;
	let backendSetupService: BackendSetupService;
	let referentialService: ReferentialService;
	let skillService: SkillService;

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [StartingSetupComponent, BackendSetupComponent],
			schemas: [ NO_ERRORS_SCHEMA ],
			providers: [ReferentialService, CinematicService, BackendSetupService],
			imports: [MatCheckboxModule, MatTableModule, FormsModule, MatPaginatorModule, MatGridListModule,
				HttpClientTestingModule, HttpClientModule, BrowserAnimationsModule, MatFormFieldModule,
				MatStepperModule,
				ReactiveFormsModule, MatSliderModule, MatInputModule, MatDialogModule,
				RouterTestingModule.withRoutes([])]
		})
		.compileComponents();
	}));

	function setUrl(value: string) {
		const url = fixture.debugElement.query(By.css('#url'));
		url.nativeElement.value = value;
		url.nativeElement.dispatchEvent(new Event('input'));
		fixture.detectChanges();
	}

	beforeEach(() => {
		fixture = TestBed.createComponent(StartingSetupComponent);
		component = fixture.componentInstance;

		backendSetupService = TestBed.inject(BackendSetupService);
		referentialService = TestBed.inject(ReferentialService);
		skillService = TestBed.inject(SkillService);

		fixture.detectChanges();

		setUrl('URL_OF_SERVER');

	});

	it('should create the component with error.', () => {
		expect(component).toBeTruthy();
	});

	it('should jump to the register form if the given backend URL is valid.', () => {

		const spy = spyOn(backendSetupService, 'isVeryFirstConnection$').and.returnValue(of(new FirstConnection(true, true, 'URL')));

		const spyLoadReferentials = spyOn(referentialService, 'loadAllReferentials').and.returnValue();
		const spySkillService = spyOn(skillService, 'loadSkills').and.returnValue();

		const submitButton = fixture.debugElement.nativeElement.querySelector('#submitButton');
		expect(submitButton).toBeDefined();
		submitButton.click();
		fixture.detectChanges();

		expect(spy).toHaveBeenCalled();
		expect(spyLoadReferentials).toHaveBeenCalled();
		expect(spySkillService).toHaveBeenCalled();

		expect(backendSetupService.url()).toBe('URL_OF_SERVER/api');

	});
});
