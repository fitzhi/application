import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { CinematicService } from 'src/app/service/cinematic.service';
import { ReferentialService } from 'src/app/service/referential/referential.service';
import { SSEWatcherComponent } from './ssewatcher.component';


describe('SSEWatcherComponent', () => {
	let component: SSEWatcherComponent;
	let fixture: ComponentFixture<SSEWatcherComponent>;

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ SSEWatcherComponent ],
			imports: [HttpClientTestingModule, MatDialogModule],
			providers: [ReferentialService, CinematicService],

		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(SSEWatcherComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
