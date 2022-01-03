import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { Constants } from 'src/app/constants';
import { Project } from 'src/app/data/project';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { PanelSwitchEvent } from '../../sonar-thumbnails/panel-switch-event';
import { QuotationBadgeComponent } from './quotation-badge/quotation-badge.component';
import { SonarQuotationComponent } from './sonar-quotation.component';


describe('SonarQuotationComponent', () => {

	let component: SonarQuotationComponent;
	let fixture: ComponentFixture<SonarQuotationComponent>;
	let projectService: ProjectService;

	beforeEach(waitForAsync(() => {
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
