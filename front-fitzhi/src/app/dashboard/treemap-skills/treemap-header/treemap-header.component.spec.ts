import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TreemapHeaderComponent } from './treemap-header.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { TagifyStarsComponent } from 'src/app/tabs-staff/staff-experience/tagify-stars/tagify-stars.component';

describe('TreemapHeaderComponent', () => {
	let component: TreemapHeaderComponent;
	let fixture: ComponentFixture<TreemapHeaderComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ TreemapHeaderComponent, TagifyStarsComponent ],
			imports: [MatCheckboxModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TreemapHeaderComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});


});
