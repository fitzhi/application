import { async, ComponentFixture, TestBed, TestModuleMetadata } from '@angular/core/testing';

import { StaffTabsComponent } from './staff-tabs.component';
import { TableGhostsComponent } from 'src/app/project/project-sunburst/project-ghosts/table-ghosts/table-ghosts.component';
import { InitTest } from 'src/app/test/init-test';
import { StaffProjectsComponent } from '../staff-projects/staff-projects.component';
import { StaffExperienceComponent } from '../staff-experience/staff-experience.component';
import { TagifyStarsComponent } from '../staff-experience/tagify-stars/tagify-stars.component';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { RouterTestingModule } from '@angular/router/testing';
import { ReferentialService } from 'src/app/service/referential.service';
import { HttpClientModule } from '@angular/common/http';
import { MatDialogModule } from '@angular/material/dialog';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('StaffTabsComponent : ', () => {
	let component: StaffTabsComponent;
	let fixture: ComponentFixture<StaffTabsComponent>;

	beforeEach(async(() => {

		TestBed.configureTestingModule({
			declarations: [
				StaffTabsComponent,
				StaffProjectsComponent,
				StaffExperienceComponent,
				TagifyStarsComponent],
			providers: [ReferentialService],
			imports: [MatTabsModule,
				MatDialogModule,
				MatTableModule,
				BrowserAnimationsModule,
				RouterTestingModule.withRoutes([]),
				HttpClientModule]
		})
		.compileComponents();
	}));

	beforeEach(() => {
		fixture = TestBed.createComponent(StaffTabsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
