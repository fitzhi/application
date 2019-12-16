import { BrowserModule } from '@angular/platform-browser';
import { NgModule, LOCALE_ID, ErrorHandler } from '@angular/core';
import { FormsModule } from '@angular/forms'; // <-- NgModel lives here
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { TooltipModule } from 'ngx-bootstrap/tooltip';
import { ModalModule } from 'ngx-bootstrap/modal';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { SkillComponent } from './skill/skill.component';
import { ListSkillComponent } from './list-skill/list-skill.component';
import { WelcomeComponent } from './welcome/welcome.component';

import { CinematicService } from './service/cinematic.service';
import { StaffComponent } from './tabs-staff/staff.component';

import { StaffService } from './service/staff.service';
import { MessageComponent } from './message/message.component';

import { ErrorComponent } from './error/error.component';

import { ProjectFormComponent } from './project/project-form/project-form.component';
import { ListProjectComponent } from './list-project/list-project.component';
import { ReferentialService } from './service/referential.service';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { StaffTabsComponent } from './tabs-staff/staff-tabs/staff-tabs.component';
import { StaffFormComponent } from './tabs-staff/staff-form/staff-form.component';
import { StaffProjectsComponent } from './tabs-staff/staff-projects/staff-projects.component';
import { StaffExperienceComponent } from './tabs-staff/staff-experience/staff-experience.component';
import { StaffUploadCvComponent } from './tabs-staff/staff-experience/staff-upload-cv/staff-upload-cv.component';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatNativeDateModule } from '@angular/material/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatStepperModule } from '@angular/material/stepper';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSortModule } from '@angular/material/sort';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatTabsModule } from '@angular/material/tabs';
import { MessageBoxComponent } from './message-box/dialog/message-box.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';
import { ProjectComponent } from './project/project.component';
import { ProjectSunburstComponent } from './project/project-sunburst/project-sunburst.component';
import { ProjectStaffComponent } from './project/project-staff/project-staff.component';
import { ProjectGhostsComponent } from './project/project-sunburst/project-ghosts/project-ghosts.component';
import { TableGhostsComponent } from './project/project-sunburst/project-ghosts/table-ghosts/table-ghosts.component';
// tslint:disable-next-line:max-line-length
import { DialogLegendSunburstComponent } from './project/project-sunburst/legend-sunburst/legend-sunburst.component';
import { registerLocaleData } from '@angular/common';

// Remove this line if you want to return to us_US local
import localeFr from '@angular/common/locales/fr';
import { DialogFilterComponent } from './project/project-sunburst/dialog-filter/dialog-filter.component';
import { ListFilenamesComponent } from './project/project-sunburst/node-detail/list-filenames/list-filenames.component';
import { NodeDetailComponent } from './project/project-sunburst/node-detail/node-detail.component';
import { ListContributorsComponent } from './project/project-sunburst/node-detail/list-contributors/list-contributors.component';
import { TabsStaffListComponent } from './tabs-staff-list/tabs-staff-list.component';
import { StaffListComponent } from './tabs-staff-list/staff-list/staff-list.component';
import { BackendSetupComponent } from './admin/backend-setup/backend-setup.component';
import { RegisterUserComponent } from './admin/register-user/register-user.component';
import { StartingSetupComponent } from './admin/starting-setup/starting-setup.component';
import { ToolbarComponent } from './toolbar/toolbar.component';
import { ConnectUserComponent } from './admin/connect-user/connect-user.component';
import { AuthGuardService } from './admin/security/auth-guard.service';
import { HttpErrorInterceptorService } from './admin/service/http/http-error-interceptor-service';
import { HttpTokenInterceptorService } from './admin/service/http/http-token-interceptor.service';
import { CiaoComponent } from './ciao/ciao.component';
import { TableDependenciesComponent } from './project/project-sunburst/table-dependencies/table-dependencies.component';
import { InLineEditDialogComponent } from './project/project-sunburst/table-dependencies/in-line-edit-dialog/in-line-edit-dialog.component';
import { TagifyStarsComponent } from './tabs-staff/staff-experience/tagify-stars/tagify-stars.component';
import { ProjectAuditComponent } from './project/project-audit/project-audit.component';
import { DevOnOffComponent } from './dev-on-off/dev-on-off.component';
import { ProjectSonarComponent } from './project/project-sonar/project-sonar.component';
import { SonarMetricsComponent } from './project/project-sonar/sonar-metrics/sonar-metrics.component';
import { SonarDashboardComponent } from './project/project-sonar/sonar-dashboard/sonar-dashboard.component';
import { SonarThumbnailsComponent } from './project/project-sonar/sonar-thumbnails/sonar-thumbnails.component';
import { SonarBadgeComponent } from './project/project-sonar/sonar-dashboard/sonar-badge/sonar-badge.component';
import { SonarQuotationComponent } from './project/project-sonar/sonar-dashboard/sonar-quotation/sonar-quotation.component';
import { QuotationBadgeComponent } from './project/project-sonar/sonar-dashboard/sonar-quotation/quotation-badge/quotation-badge.component';
import { TechxhiMedalComponent } from './project/project-form/techxhi-medal/techxhi-medal.component';
import { AuditBadgeComponent } from './project/project-audit/project-audit-badges/audit-badge/audit-badge.component';
import { ProjectAuditBadgesComponent } from './project/project-audit/project-audit-badges/project-audit-badges.component';
// tslint:disable-next-line:max-line-length
import { AuditGraphicBadgeComponent } from './project/project-audit/project-audit-badges/audit-badge/audit-graphic-badge/audit-graphic-badge.component';
import { TableCategoriesComponent } from './project/project-audit/table-categories/table-categories.component';
import {MatSliderModule} from '@angular/material/slider';
import { ReportDetailFormComponent } from './project/project-audit/project-audit-badges/report-detail-form/report-detail-form.component';
import { FilesDetailFormComponent } from './project/project-audit/project-audit-badges/files-detail-form/files-detail-form.component';
// tslint:disable-next-line:max-line-length
import { AuditAttachmentUploadComponent } from './project/project-audit/project-audit-badges/files-detail-form/audit-attachment-upload/audit-attachment-upload.component';

@NgModule({
	declarations: [
		AppComponent,
		SkillComponent,
		ListSkillComponent,
		WelcomeComponent,
		StaffComponent,
		MessageComponent,
		ErrorComponent,
		ProjectFormComponent,
		ListProjectComponent,
		StaffTabsComponent,
		StaffFormComponent,
		StaffProjectsComponent,
		StaffExperienceComponent,
		StaffUploadCvComponent,
		MessageBoxComponent,
		ProjectComponent,
		ProjectSunburstComponent,
		ProjectStaffComponent,
		ProjectGhostsComponent,
		TableGhostsComponent,
		DialogLegendSunburstComponent,
		DialogFilterComponent,
		ListFilenamesComponent,
		NodeDetailComponent,
		ListContributorsComponent,
		TabsStaffListComponent,
		StaffListComponent,
		BackendSetupComponent,
		RegisterUserComponent,
		StartingSetupComponent,
		ToolbarComponent,
		ConnectUserComponent,
		CiaoComponent,
		TableDependenciesComponent,
		InLineEditDialogComponent,
		DialogLegendSunburstComponent,
		TagifyStarsComponent,
		ProjectAuditComponent,
		DevOnOffComponent,
		ProjectSonarComponent,
		SonarMetricsComponent,
		SonarDashboardComponent,
		SonarThumbnailsComponent,
		SonarBadgeComponent,
		SonarQuotationComponent,
		QuotationBadgeComponent,
		TechxhiMedalComponent,
		AuditBadgeComponent,
		ProjectAuditBadgesComponent,
		AuditGraphicBadgeComponent,
		TableCategoriesComponent,
		ReportDetailFormComponent,
		FilesDetailFormComponent,
		AuditAttachmentUploadComponent
	],
	entryComponents: [
		StaffUploadCvComponent,
		MessageBoxComponent,
		DialogFilterComponent,
		InLineEditDialogComponent
	],
	imports: [
		BrowserModule,
		AppRoutingModule,
		BsDropdownModule.forRoot(),
		TooltipModule.forRoot(),
		ModalModule.forRoot(),
		NgbModule.forRoot(),
		FormsModule,
		HttpClientModule,
		ReactiveFormsModule,
		BrowserAnimationsModule,
		MatTabsModule,
		MatDialogModule,
		MatProgressBarModule,
		MatTableModule,
		MatPaginatorModule,
		MatCheckboxModule,
		MatSortModule,
		MatButtonToggleModule,
		MatSidenavModule,
		MatCardModule,
		MatFormFieldModule,
		MatInputModule,
		MatSnackBarModule,
		MatDatepickerModule,
		MatNativeDateModule,
		MatSelectModule,
		MatExpansionModule,
		MatGridListModule,
		MatStepperModule,
		MatIconModule,
		MatAutocompleteModule,
		MatSliderModule,
	],
	providers: [
		CinematicService,
		StaffService,
		ReferentialService,
		AuthGuardService,
		{
			provide: HTTP_INTERCEPTORS,
			useClass: HttpErrorInterceptorService,
			multi: true
		},
		{
			provide: HTTP_INTERCEPTORS,
			useClass: HttpTokenInterceptorService,
			multi: true
		},
		/*   {provide: ErrorHandler, useClass: CustomErrorHandler}, */
		// Remove this line or change the useValue property to your regional settings
		{ provide: LOCALE_ID, useValue: 'fr' }
	],
	bootstrap: [AppComponent]
})
/*    {
      provide: ErrorHandler,
      useClass: ErrorsHandler,
    }
 */
export class AppModule {
	constructor() {
		// Remove this line to return back to the default us property,
		// or change the useValue property to your regional settings
		registerLocaleData(localeFr, 'fr');
	}
}
