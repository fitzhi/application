
import { CommonModule } from '@angular/common';
import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { RegisterUserComponent } from '../admin/register-user/register-user.component';
import { WelcomeComponent } from '../welcome/welcome.component';
import { ConnectUserComponent } from '../admin/connect-user/connect-user.component';
import { CiaoComponent } from '../ciao/ciao.component';
import { DevOnOffComponent } from '../dev-on-off/dev-on-off.component';
import { ListSkillComponent } from '../list-skill/list-skill.component';
import { SkillComponent } from '../skill/skill.component';
import { TabsStaffListComponent } from '../tabs-staff-list/tabs-staff-list.component';
import { StaffComponent } from '../tabs-staff/staff.component';
import { StaffFormComponent } from '../tabs-staff/staff-form/staff-form.component';
import { ListProjectComponent } from '../list-project/list-project.component';
import { ProjectComponent } from '../project/project.component';
import { ProjectSunburstComponent } from '../project/project-sunburst/project-sunburst.component';
import { ProjectStaffComponent } from '../project/project-staff/project-staff.component';
import { StartingSetupComponent } from '../admin/starting-setup/starting-setup.component';
import { StaffListComponent } from '../tabs-staff-list/staff-list/staff-list.component';
import { StaffProjectsComponent } from '../tabs-staff/staff-projects/staff-projects.component';
import { StaffExperienceComponent } from '../tabs-staff/staff-experience/staff-experience.component';
import { ProjectFormComponent } from '../project/project-form/project-form.component';
import { ProjectSonarComponent } from '../project/project-sonar/project-sonar.component';
import { ProjectAuditComponent } from '../project/project-audit/project-audit.component';
import { NodeDetailComponent } from '../project/project-sunburst/node-detail/node-detail.component';
import { ProjectGhostsComponent } from '../project/project-sunburst/project-ghosts/project-ghosts.component';
import { DialogLegendSunburstComponent } from '../project/project-sunburst/legend-sunburst/legend-sunburst.component';
import { TableDependenciesComponent } from '../project/project-sunburst/table-dependencies/table-dependencies.component';
import { BackendSetupComponent } from '../admin/backend-setup/backend-setup.component';
import { TagifyStarsComponent } from '../tabs-staff/staff-experience/tagify-stars/tagify-stars.component';
import { SonarThumbnailsComponent } from '../project/project-sonar/sonar-thumbnails/sonar-thumbnails.component';
import { ErrorComponent } from '../error/error.component';
import { SonarMetricsComponent } from '../project/project-sonar/sonar-metrics/sonar-metrics.component';
import { SonarDashboardComponent } from '../project/project-sonar/sonar-dashboard/sonar-dashboard.component';
import { MessageComponent } from '../message/message.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatButtonModule } from '@angular/material/button';
import { MatStepperModule } from '@angular/material/stepper';
import { ReferentialService } from '../service/referential.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { CinematicService } from '../service/cinematic.service';
import { AppComponent } from '../app.component';
import { ToolbarComponent } from '../toolbar/toolbar.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MessageBoxComponent } from '../message-box/dialog/message-box.component';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatOptionModule, MatNativeDateModule } from '@angular/material/core';
import { MatInputModule } from '@angular/material/input';
import { DialogFilterComponent } from '../project/project-sunburst/dialog-filter/dialog-filter.component';
import { ProjectStaffService } from '../project/project-staff-service/project-staff.service';
import { MatExpansionModule } from '@angular/material/expansion';
import { ListFilenamesComponent } from '../project/project-sunburst/node-detail/list-filenames/list-filenames.component';
import { ListContributorsComponent } from '../project/project-sunburst/node-detail/list-contributors/list-contributors.component';
import { TableGhostsComponent } from '../project/project-sunburst/project-ghosts/table-ghosts/table-ghosts.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { SonarBadgeComponent } from '../project/project-sonar/sonar-dashboard/sonar-badge/sonar-badge.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSelectModule } from '@angular/material/select';
import { MatSortModule } from '@angular/material/sort';
import { TabsStaffListService } from '../tabs-staff-list/service/tabs-staff-list.service';
import { StaffTabsComponent } from '../tabs-staff/staff-tabs/staff-tabs.component';
import { SonarQuotationComponent } from '../project/project-sonar/sonar-dashboard/sonar-quotation/sonar-quotation.component';
import { BackendSetupService } from '../service/backend-setup/backend-setup.service';
import { QuotationBadgeComponent } from '../project/project-sonar/sonar-dashboard/sonar-quotation/quotation-badge/quotation-badge.component';

@NgModule({
	declarations: [
		AppComponent,
		ToolbarComponent,
		RegisterUserComponent,
		WelcomeComponent,
		ConnectUserComponent,
		CiaoComponent,
		DevOnOffComponent,
		ListSkillComponent,
		SkillComponent,
		TabsStaffListComponent,
		StaffComponent,
		StaffFormComponent,
		ListProjectComponent,
		ProjectComponent,
		ProjectSunburstComponent,
		ProjectStaffComponent,
		StartingSetupComponent,
		StaffListComponent,
		StaffProjectsComponent,
		StaffExperienceComponent,
		ProjectFormComponent,
		ProjectSonarComponent,
		ProjectAuditComponent,
		NodeDetailComponent,
		ProjectGhostsComponent,
		ProjectSunburstComponent,
		DialogLegendSunburstComponent,
		TableDependenciesComponent,
		BackendSetupComponent,
		TagifyStarsComponent,
		SonarThumbnailsComponent,
		ErrorComponent,
		SonarDashboardComponent,
		SonarMetricsComponent,
		MessageComponent,
		MessageBoxComponent,
		DialogFilterComponent,
		ListFilenamesComponent,
		ListContributorsComponent,
		TableGhostsComponent,
		SonarBadgeComponent,
		StaffTabsComponent,
		SonarQuotationComponent,
		QuotationBadgeComponent
	],
	providers: [
		ReferentialService,
		CinematicService,
		ProjectStaffService,
		TabsStaffListService,
		BackendSetupService,
		{ provide: MAT_DIALOG_DATA, useValue: {} },
		{ provide: MatDialogRef, useValue: {} }
	],
	entryComponents: [
		MessageBoxComponent,
	],
	imports: [
		CommonModule,

		HttpClientTestingModule,

		RouterTestingModule,

		MatTableModule, MatTabsModule, MatDialogModule,
		MatPaginatorModule, MatSidenavModule, MatProgressBarModule, MatCardModule,
		MatStepperModule, MatButtonModule, MatButtonToggleModule, MatDatepickerModule,
		MatFormFieldModule, MatOptionModule, MatInputModule, MatNativeDateModule,
		MatExpansionModule, MatGridListModule, MatCheckboxModule, MatSelectModule,
		MatSortModule, MatSidenavModule,

		BrowserAnimationsModule,
		FormsModule, ReactiveFormsModule,
	],
	schemas: [NO_ERRORS_SCHEMA]
})
export class RootTestModule {
}
