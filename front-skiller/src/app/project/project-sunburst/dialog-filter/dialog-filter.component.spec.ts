import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogFilterComponent } from './dialog-filter.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';

describe('DialogFilterComponent', () => {
	let component: DialogFilterComponent;
	let fixture: ComponentFixture<DialogFilterComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [  ],
			imports: [
				RootTestModule,
			]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(DialogFilterComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create!', () => {
		expect(component).toBeTruthy();
	});
});
