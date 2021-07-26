import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { PieLegendComponent } from './pie-legend.component';
import { PieDashboardService } from '../service/pie-dashboard.service';
import { AnalysisTypeSlice } from '../analysis-type-slice';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ReferentialService } from 'src/app/service/referential.service';
import { ProjectService } from 'src/app/service/project/project.service';
import { CinematicService } from 'src/app/service/cinematic.service';
import { MatDialogModule } from '@angular/material/dialog';
import { LevelStaffRisk } from '../level-staff-risk';

describe('PieLegendComponent', () => {
	let component: PieLegendComponent;
	let fixture: ComponentFixture<PieLegendComponent>;
	let pieDashboardService: PieDashboardService;

	beforeEach(waitForAsync(() => {
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
		pieDashboardService.sliceActivated$.next(
			{
				id: 1,
				type: AnalysisTypeSlice.Staff,
				offset: 0,
				angle: 45,
				backgroundColor: 'green',
				textColor: 'black',
				textFontSize: '16px',
				children: [],
				data: LevelStaffRisk.low,
				activated: false,
				selected: false
			});
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();

		setTimeout(() => {
			pieDashboardService.sliceActivated$.next(
				{
					id: 1,
					type: AnalysisTypeSlice.Sonar,
					offset: 0,
					angle: 45,
					backgroundColor: 'green',
					textColor: 'black',
					textFontSize: '16px',
					children: [],
					data: LevelStaffRisk.low,
					activated: false,
					selected: false
				});
				fixture.detectChanges();
		}, 5000);

		setTimeout(() => {
			pieDashboardService.sliceActivated$.next(
				{
					id: 1,
					type: AnalysisTypeSlice.Audit,
					offset: 0,
					angle: 45,
					backgroundColor: 'green',
					textColor: 'black',
					textFontSize: '16px',
					children: [],
					data: LevelStaffRisk.low,
					activated: false,
					selected: false
				});
			fixture.detectChanges();
		}, 10000);

	});
});
