import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { DialogLegendSunburstComponent } from './legend-sunburst.component';


describe('DialogLegendSunburstComponent', () => {
	let component: DialogLegendSunburstComponent;
	let fixture: ComponentFixture<DialogLegendSunburstComponent>;

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [DialogLegendSunburstComponent],
			providers: [ReferentialService, CinematicService],
			imports: [FormsModule, MatTableModule, HttpClientTestingModule]
		})
			.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(DialogLegendSunburstComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
