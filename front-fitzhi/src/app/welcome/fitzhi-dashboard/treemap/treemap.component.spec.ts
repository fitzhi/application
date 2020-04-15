import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TreemapComponent } from './treemap.component';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('TreemapComponent', () => {
	let component: TreemapComponent;
	let fixture: ComponentFixture<TreemapComponent>;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			imports: [NgxChartsModule, BrowserAnimationsModule],
			declarations: [ TreemapComponent ]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(TreemapComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
