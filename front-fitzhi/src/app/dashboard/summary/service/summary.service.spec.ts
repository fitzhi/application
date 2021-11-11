import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { CinematicService } from 'src/app/service/cinematic.service';
import { DashboardService } from 'src/app/service/dashboard/dashboard.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { SummaryService } from './summary.service';


describe('SummaryService', () => {
	let service: SummaryService;
	let dashboardService: DashboardService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [ HttpClientTestingModule, MatDialogModule ],
			providers: [ DashboardService, ReferentialService, CinematicService]

		});
		service = TestBed.inject(SummaryService);
		dashboardService = TestBed.inject(DashboardService);
	});

	it('should be created without error.', () => {
		expect(service).toBeTruthy();
	});

	it('should emit the calculated score.', done => {
		const spy = spyOn(dashboardService, 'calculateGeneralAverage').and.returnValue(3);
		service.showGeneralAverage();
		service.summary$.subscribe({
			next: res => expect(res.overallAverage).toBe(true)
		});
		service.generalAverage$.subscribe({
			next: score => {
				expect(score).toBe(3);
				done();
			}
		});
	});

	it('should not display the general average summary if no data are available.', done => {
		const spy = spyOn(dashboardService, 'calculateGeneralAverage').and.returnValue(NaN);
		service.showGeneralAverage();
		service.summary$.subscribe({
			next: res =>  expect(res.overallAverage).toBe(false)
		});
		service.generalAverage$.subscribe({
			next: score => {
				expect(score).toBe(-1);
				done();
			}
		});
	});
});
