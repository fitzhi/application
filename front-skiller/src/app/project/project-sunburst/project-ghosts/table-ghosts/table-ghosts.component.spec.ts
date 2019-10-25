import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TableGhostsComponent } from './table-ghosts.component';
import { RootTestModule } from 'src/app/root-test/root-test.module';

describe('TableGhostsComponent', () => {
	let component: TableGhostsComponent;
	let fixture: ComponentFixture<TableGhostsComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [  ],
			imports: [RootTestModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TableGhostsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
