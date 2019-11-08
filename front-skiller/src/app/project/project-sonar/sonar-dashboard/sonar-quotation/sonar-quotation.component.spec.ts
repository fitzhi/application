import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RootTestModule } from 'src/app/root-test/root-test.module';
import { BehaviorSubject, Subject } from 'rxjs';
import { Project } from 'src/app/data/project';
import { PanelSwitchEvent } from '../../sonar-thumbnails/panel-switch-event';
import { SonarQuotationComponent } from './sonar-quotation.component';
import { Constants } from 'src/app/constants';

describe('SonarQuotationComponent', () => {

	let component: SonarQuotationComponent;
	let fixture: ComponentFixture<SonarQuotationComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [],
			imports: [RootTestModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {

		fixture = TestBed.createComponent(SonarQuotationComponent);
		component = fixture.componentInstance;
		const project = new Project();
		project.sonarProjects = [];
		component.project$ = new BehaviorSubject<Project>(project);
		const pse = new PanelSwitchEvent( Constants.PROJECT_SONAR_PANEL.SONAR, 'void');
		component.panelSwitchTransmitter$ = new Subject<PanelSwitchEvent>();
		component.panelSwitchTransmitter$.next(pse);
		fixture.detectChanges();

	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

});
