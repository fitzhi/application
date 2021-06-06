import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSliderModule } from '@angular/material/slider';
import { MatTableModule } from '@angular/material/table';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';
import { AppComponent } from './app.component';
import { Constants } from './constants';
import { ListCriteria } from './data/listCriteria';
import { MessageComponent } from './interaction/message/message.component';
import { ToolbarComponent } from './interaction/toolbar/toolbar.component';
import { CinematicService } from './service/cinematic.service';
import { ReferentialService } from './service/referential.service';
import { ListSkillComponent } from './skill/list-skill/list-skill.component';
import { SkillService } from './skill/service/skill.service';
import { ListProjectComponent } from './tabs-project/list-project/list-project.component';
import { ListProjectsService } from './tabs-project/list-project/list-projects-service/list-projects.service';
import { TabsStaffListService } from './tabs-staff-list/service/tabs-staff-list.service';
import { TabsStaffListComponent } from './tabs-staff-list/tabs-staff-list.component';
import { StaffService } from './tabs-staff/service/staff.service';
import { StaffFormComponent } from './tabs-staff/staff-form/staff-form.component';


describe('AppComponent', () => {

	let component: AppComponent;
	let fixture: ComponentFixture<AppComponent>;

	let router: Router;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [AppComponent, ToolbarComponent, MessageComponent],
			providers: [ReferentialService, CinematicService],
			imports: [MatCheckboxModule, MatTableModule, FormsModule, MatPaginatorModule, MatGridListModule,
				HttpClientTestingModule, HttpClientModule, BrowserAnimationsModule, MatFormFieldModule,
				ReactiveFormsModule, MatSliderModule, MatInputModule, MatDialogModule,
				RouterTestingModule.withRoutes([
					{ path: 'user', component: StaffFormComponent },
					{ path: 'searchUser', component: TabsStaffListComponent },
					{ path: 'searchSkill', component: ListSkillComponent },
					{ path: 'searchProject', component: ListProjectComponent },
				])
			]
			}).compileComponents();

		fixture = TestBed.createComponent(AppComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();

		router = TestBed.inject(Router);

	}));

	it('Searching staff members on a \'string\' criteria', async(() => {
		const tabsStaffListService = TestBed.inject(TabsStaffListService);
		const spyTabsStaffListService = spyOn(tabsStaffListService, 'addTabResult');

		const navigateSpy = spyOn(router, 'navigate');

		component.onChangeForm(Constants.TABS_STAFF_LIST);
		component.onRequestQuery('nope');

		expect(spyTabsStaffListService).toHaveBeenCalledWith('nope', true);
		expect(navigateSpy).not.toHaveBeenCalled();

	}));

	it('Searching staff members with an integer considered as an identifier', async(() => {
		const tabsStaffListService = TestBed.inject(TabsStaffListService);
		const spyTabsStaffListService = spyOn(tabsStaffListService, 'addTabResult');

		const navigateSpy = spyOn(router, 'navigate');

		component.onChangeForm(Constants.TABS_STAFF_LIST);
		component.onRequestQuery('1789');

		expect(spyTabsStaffListService).not.toHaveBeenCalled();
		expect(navigateSpy).toHaveBeenCalledWith(['/user/1789']);
	}));

	// If the end-user type a number in the search field wich is not an integer, and therefore cannot be a staff identifier
	it('Searching staff members with an number WHICH IS NOT AN INTEGER', async(() => {
		const tabsStaffListService = TestBed.inject(TabsStaffListService);
		const spyTabsStaffListService = spyOn(tabsStaffListService, 'addTabResult');

		const navigateSpy = spyOn(router, 'navigate');

		component.onChangeForm(Constants.TABS_STAFF_LIST);
		component.onRequestQuery('17.89');

		expect(spyTabsStaffListService).toHaveBeenCalled();
		expect(navigateSpy).not.toHaveBeenCalledWith(['/user/17.89']);
	}));

	it('Searching the projects corresponding to a criteria', async(() => {
		const listProjectsService = TestBed.inject(ListProjectsService);
		const spyReloadProjects = spyOn(listProjectsService, 'search').and.returnValue();

		component.onChangeForm(Constants.PROJECT_SEARCH);
		component.onRequestQuery('*');

		expect(spyReloadProjects).toHaveBeenCalled();
	}));

	it('Searching the skills corresponding to a criteria', async(() => {
		const skillService = TestBed.inject(SkillService);
		skillService.allSkillsLoaded$ = of(true);

		const spyFilterSkills = spyOn(skillService, 'filterSkills');

		const staffService = TestBed.inject(StaffService);
		const spyStaffService = spyOn(staffService, 'countAll_groupBy_experience');

		component.onChangeForm(Constants.SKILLS_SEARCH);
		component.onRequestQuery('test');

		expect(spyFilterSkills).toHaveBeenCalledWith(new ListCriteria('test', true));
		expect(spyStaffService).toHaveBeenCalledWith(true);
	}));

});
