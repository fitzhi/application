import { DatePipe } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SkylineComponent } from './skyline.component';

describe('SkylineComponent', () => {
	let component: SkylineComponent;
	let fixture: ComponentFixture<SkylineComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ SkylineComponent ],
			providers: [DatePipe],
			imports: [HttpClientTestingModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(SkylineComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
