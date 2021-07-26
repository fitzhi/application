import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TreemapHeaderComponent } from './treemap-skills-header.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { TagifyStarsComponent } from 'src/app/tabs-staff/staff-experience/tagify-stars/tagify-stars.component';

describe('TreemapHeaderComponent', () => {
	let component: TreemapHeaderComponent;
	let fixture: ComponentFixture<TreemapHeaderComponent>;

	beforeEach(waitForAsync(() => {
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
