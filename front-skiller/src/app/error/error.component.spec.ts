import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorComponent } from './error.component';
import { RouterTestingModule } from '@angular/router/testing';
import { RootTestModule } from '../root-test/root-test.module';

describe('ErrorComponent', () => {
	let component: ErrorComponent;
	let fixture: ComponentFixture<ErrorComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [  ],
			imports: [RootTestModule]

		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(ErrorComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
