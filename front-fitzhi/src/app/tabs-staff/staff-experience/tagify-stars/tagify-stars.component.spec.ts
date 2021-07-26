import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TagifyStarsComponent } from './tagify-stars.component';
import { Component } from '@angular/core';
import { TagStar } from '../../staff-form/tag-star';
import { BehaviorSubject, Subject } from 'rxjs';
import { TagifyEditableState } from './tagify-editable-state';

describe('TagifyStarsComponent', () => {
	let component: TestHostComponent;
	let fixture: ComponentFixture<TestHostComponent>;

	@Component({
		selector: 'app-host-component',
		template: '<app-tagify-stars' +
			' [whitelist]="whitelist"' +
			' [colorON]="\'#28a745\'"' +
			' [colorOFF]="\'white\'"' +
			' [originalValues]="originalValues"' +
			' [additionalValues$]="additionalValues$"' +
			' [values$]="values$"' +
			' [editableState$]="editableState$"' +
			' (addTagEvent)="onAddTagEvent($event)"' +
			' (editTagEvent)="onEditTagEvent($event)"' +
			' (removeTagEvent)="onRemoveTagEvent($event)"></app-tagify-stars>'
	})

	class TestHostComponent {
		public whitelist = ['java', 'Angular'];
		public values$ = new Subject<TagStar[]>();
		public originalValues = [new TagStar('java', 2), new TagStar('Angular', 3)];
		public additionalValues$ = new Subject<TagStar[]>();
		public editableState$ = new BehaviorSubject<TagifyEditableState>(TagifyEditableState.ALL_ALLOWED);

		constructor() {
			this.values$.next([new TagStar('java', 2)]);
			this.additionalValues$.next([]);
		}
	}

	beforeEach(waitForAsync(() => {
		TestBed.configureTestingModule({
			declarations: [ TestHostComponent, TagifyStarsComponent ]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TestHostComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
