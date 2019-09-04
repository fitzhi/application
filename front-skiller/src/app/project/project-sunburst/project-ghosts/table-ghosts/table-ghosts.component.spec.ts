import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TableGhostsComponent } from './table-ghosts.component';

describe('TableGhostsComponent', () => {
	let component: TableGhostsComponent;
	let fixture: ComponentFixture<TableGhostsComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ TableGhostsComponent ]
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
