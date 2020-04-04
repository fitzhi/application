import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SSEWatcherComponent } from './ssewatcher.component';
import { ReferentialService } from 'src/app/service/referential.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MatDialogModule } from '@angular/material/dialog';

describe('SSEWatcherComponent', () => {
	let component: SSEWatcherComponent;
	let fixture: ComponentFixture<SSEWatcherComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ SSEWatcherComponent ],
			imports: [HttpClientTestingModule, MatDialogModule],
			providers: [ReferentialService],

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
