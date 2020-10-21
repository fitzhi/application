import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BehaviorSubject, Subject } from 'rxjs';
import { Project } from 'src/app/data/project';
import { PanelSwitchEvent } from '../../sonar-thumbnails/panel-switch-event';
import { SonarQuotationComponent } from './sonar-quotation.component';
import { Constants } from 'src/app/constants';
import { ProjectService } from 'src/app/service/project.service';
import { QuotationBadgeComponent } from './quotation-badge/quotation-badge.component';
import { ReferentialService } from 'src/app/service/referential.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';

describe('SonarQuotationComponent', () => {

	let component: SonarQuotationComponent;
	let fixture: ComponentFixture<SonarQuotationComponent>;
	let projectService: ProjectService;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [SonarQuotationComponent, QuotationBadgeComponent],
			providers: [ReferentialService, CinematicService],
			imports: [HttpClientTestingModule, MatDialogModule, FormsModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {

		fixture = TestBed.createComponent(SonarQuotationComponent);
		component = fixture.componentInstance;
		const project = new Project();
		project.sonarProjects = [];
		projectService = TestBed.inject(ProjectService);
		projectService.projectLoaded$.next(true);
		const pse = new PanelSwitchEvent( Constants.PROJECT_SONAR_PANEL.SONAR, 'void');
		component.panelSwitchTransmitter$ = new Subject<PanelSwitchEvent>();
		component.panelSwitchTransmitter$.next(pse);
		fixture.detectChanges();

	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

});
