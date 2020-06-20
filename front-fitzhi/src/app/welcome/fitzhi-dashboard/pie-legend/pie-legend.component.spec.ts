import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PieLegendComponent } from './pie-legend.component';
import { PieDashboardService } from '../service/pie-dashboard.service';
import { TypeSlice } from '../type-slice';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReferentialService } from 'src/app/service/referential.service';
import { ProjectService } from 'src/app/service/project.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { MatDialogModule } from '@angular/material/dialog';

describe('PieLegendComponent', () => {
	let component: PieLegendComponent;
	let fixture: ComponentFixture<PieLegendComponent>;
	let pieDashboardService: PieDashboardService;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ PieLegendComponent ],
			imports: [HttpClientTestingModule, MatDialogModule],
			providers: [PieDashboardService, ProjectService, ReferentialService, CinematicService]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(PieLegendComponent);
		component = fixture.componentInstance;
		pieDashboardService = TestBed.inject(PieDashboardService);
		pieDashboardService.projectSubject$.next(TypeSlice.Staff);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();

		setTimeout(() => {
			pieDashboardService.projectSubject$.next(TypeSlice.Sonar);
			fixture.detectChanges();
		}, 5000);

		setTimeout(() => {
			pieDashboardService.projectSubject$.next(TypeSlice.Audit);
			fixture.detectChanges();
		}, 10000);

	});
});
