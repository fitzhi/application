import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Component } from '@angular/core';
import { ComponentFixture, TestBed, TestModuleMetadata, waitForAsync } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { BehaviorSubject, of } from 'rxjs';
import { SonarServer } from 'src/app/data/sonar-server';
import { MessageBoxService } from 'src/app/interaction/message-box/service/message-box.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { SonarService } from 'src/app/service/sonar/sonar.service';
import { PanelSwitchEvent } from '../sonar-thumbnails/panel-switch-event';
import { SonarDashboardComponent } from './sonar-dashboard.component';



describe('SonarDashboardComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;
	let sonarService: SonarService;

	@Component({
		selector: 'app-host-component',
		template:
			`
			<h1 style="margin:30px;color:var(--color-success)">Sonar dashboard</h1>
			<div style="width:80%;height:50%">
				<app-sonar-dashboard  [panelSwitchTransmitter$]="panelSwitchTransmitter$">
				</app-sonar-dashboard>
			</div>
			`
	})
	class TestHostComponent {
		panelSwitchTransmitter$ =  new BehaviorSubject<PanelSwitchEvent>(new PanelSwitchEvent(1, 'Sonar project'));
	}

	beforeEach(waitForAsync(() => {
		const testConf: TestModuleMetadata =  {
			declarations: [TestHostComponent, SonarDashboardComponent],
			providers: [ProjectService, ReferentialService, CinematicService, MessageBoxService],
			imports: [HttpClientTestingModule, MatDialogModule]
		};
		TestBed.configureTestingModule(testConf).compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		sonarService = TestBed.inject(SonarService);
	});

	it('should create, even if no Sonar server is declared, or reachable', () => {
		fixture.detectChanges();
		expect(component).toBeTruthy();
		expect(TestHostComponent).toBeDefined();
		expect(document.getElementById('sonarDashboard')).toBeNull();
		expect(document.getElementById('sonarTooOld')).toBeNull();
		expect(document.getElementById('sonarUnreachable')).toBeDefined();
	});

	it('should handle an old Sonar version', () => {
		const sonarServer = new SonarServer('6.5', 'url', true);
		const spySonarServiceOne = spyOn(sonarService, 'sonarIsAccessible$').and.returnValue(of(true));
		const spySonarServiceTwo = spyOn(sonarService, 'getSonarServer').and.returnValue(sonarServer);

		fixture.detectChanges();

		expect(component).toBeTruthy();
		expect(TestHostComponent).toBeDefined();

		expect(spySonarServiceOne).toHaveBeenCalled();

		expect(document.getElementById('sonarDashboard')).toBeNull();
		expect(document.getElementById('sonarTooOld')).toBeDefined();
		expect(document.getElementById('sonarUnreachable')).toBeNull();
	});

	it('should display the dashboard if the Sonar version allows this feature', () => {
		const sonarServer = new SonarServer('8.3.1', 'url', true);
		const spySonarServiceOne = spyOn(sonarService, 'sonarIsAccessible$').and.returnValue(of(true));
		const spySonarServiceTwo = spyOn(sonarService, 'getSonarServer').and.returnValue(sonarServer);

		fixture.detectChanges();

		expect(component).toBeTruthy();
		expect(TestHostComponent).toBeDefined();

		expect(spySonarServiceOne).toHaveBeenCalled();

		expect(document.getElementById('sonarDashboard')).toBeDefined();
		expect(document.getElementById('sonarTooOld')).toBeNull();
		expect(document.getElementById('sonarUnreachable')).toBeNull();
	});
});

