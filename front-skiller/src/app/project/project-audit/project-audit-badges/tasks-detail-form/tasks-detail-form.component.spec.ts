import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TasksDetailFormComponent } from './tasks-detail-form.component';

describe('TasksDetailFormComponent', () => {
	let component: TasksDetailFormComponent;
	let fixture: ComponentFixture<TasksDetailFormComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ TasksDetailFormComponent ]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TasksDetailFormComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
