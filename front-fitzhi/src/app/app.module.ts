import { DatePipe, registerLocaleData } from '@angular/common';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
// Remove this line if you want to return to us_US local
import localeFr from '@angular/common/locales/fr';
import { LOCALE_ID, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms'; // <-- NgModel lives here
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSliderModule } from '@angular/material/slider';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSortModule } from '@angular/material/sort';
import { MatStepperModule } from '@angular/material/stepper';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { NgxPopper } from 'angular-popper';
import { ControlledRisingSkylineModule } from 'controlled-rising-skyline';
import { DynamicPieChartModule } from 'dynamic-pie-chart';
import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { ModalModule } from 'ngx-bootstrap/modal';
import { TooltipModule } from 'ngx-bootstrap/tooltip';
import { RisingSkylineModule } from 'rising-skyline';
import { BackendSetupComponent } from './admin/backend-setup/backend-setup.component';
import { CiaoComponent } from './admin/ciao/ciao.component';
import { ConnectUserComponent } from './admin/connect-user/connect-user.component';
import { DevOnOffComponent } from './admin/dev-on-off/dev-on-off.component';
import { RegisterUserComponent } from './admin/register-user/register-user.component';
import { AuthGuardService } from './admin/security/auth-guard.service';
import { HttpErrorInterceptorService } from './admin/service/http/http-error-interceptor-service';
import { HttpRefreshTokenErrorInterceptor } from './admin/service/http/http-refresh-token-error-interceptor';
import { HttpTokenInterceptor } from './admin/service/http/http-token-interceptor';
import { TokenService } from './admin/service/token/token.service';
import { StartingSetupComponent } from './admin/starting-setup/starting-setup.component';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { FitzhiDashboardComponent } from './dashboard/fitzhi-dashboard.component';
import { PieChartComponent } from './dashboard/pie-chart/pie-chart.component';
import { PieLegendComponent } from './dashboard/pie-legend/pie-legend.component';
import { PieProjectsComponent } from './dashboard/pie-projects/pie-projects.component';
import { PieDashboardService } from './dashboard/service/pie-dashboard.service';
import { SkylineComponent } from './dashboard/skyline/component/skyline.component';
import { SkylineIconComponent } from './dashboard/skyline/skyline-icon/skyline-icon.component';
import { TreemapSkillsChartComponent } from './dashboard/treemap-skills/treemap-skills-chart/treemap-skills-chart.component';
import { TreemapSkillsComponent } from './dashboard/treemap-skills/treemap-skills-container/treemap-skills.component';
import { TreemapHeaderComponent } from './dashboard/treemap-skills/treemap-skills-header/treemap-skills-header.component';
import { ErrorComponent } from './interaction/error/error.component';
import { MessageBoxComponent } from './interaction/message-box/dialog/message-box.component';
import { MessageComponent } from './interaction/message/message.component';
import { ToolbarComponent } from './interaction/toolbar/toolbar.component';
import { CinematicService } from './service/cinematic.service';
import { ReferentialService } from './service/referential/referential.service';
import { ListSkillComponent } from './skill/list-skill/list-skill.component';
import { SkillComponent } from './skill/skill.component';
import { ListProjectComponent } from './tabs-project/list-project/list-project.component';
import { AuditBadgeComponent } from './tabs-project/project-audit/project-audit-badges/audit-badge/audit-badge.component';
// tslint:disable-next-line:max-line-length
import { AuditGraphicBadgeComponent } from './tabs-project/project-audit/project-audit-badges/audit-badge/audit-graphic-badge/audit-graphic-badge.component';
// tslint:disable-next-line:max-line-length
import { AuditAttachmentComponent } from './tabs-project/project-audit/project-audit-badges/files-detail-form/audit-attachment-upload/audit-attachment.component';
// tslint:disable-next-line:max-line-length
import { AuditUploadAttachmentComponent } from './tabs-project/project-audit/project-audit-badges/files-detail-form/audit-attachment-upload/audit-upload-attachment/audit-upload-attachment.component';
import { FilesDetailFormComponent } from './tabs-project/project-audit/project-audit-badges/files-detail-form/files-detail-form.component';
import { ProjectAuditBadgesComponent } from './tabs-project/project-audit/project-audit-badges/project-audit-badges.component';
import { ReportDetailFormComponent } from './tabs-project/project-audit/project-audit-badges/report-detail-form/report-detail-form.component';
import { ProjectAuditComponent } from './tabs-project/project-audit/project-audit.component';
import { TableCategoriesComponent } from './tabs-project/project-audit/table-categories/table-categories.component';
import { BranchComponent } from './tabs-project/project-form/branch/branch.component';
import { ProjectFormComponent } from './tabs-project/project-form/project-form.component';
import { TechxhiMedalComponent } from './tabs-project/project-form/techxhi-medal/techxhi-medal.component';
import { ProjectInactivateComponent } from './tabs-project/project-inactivate/project-inactivate.component';
import { ProjectRemoveComponent } from './tabs-project/project-remove/project-remove.component';
import { ProjectSonarComponent } from './tabs-project/project-sonar/project-sonar.component';
import { SonarBadgeComponent } from './tabs-project/project-sonar/sonar-dashboard/sonar-badge/sonar-badge.component';
import { SonarDashboardComponent } from './tabs-project/project-sonar/sonar-dashboard/sonar-dashboard.component';
import { QuotationBadgeComponent } from './tabs-project/project-sonar/sonar-dashboard/sonar-quotation/quotation-badge/quotation-badge.component';
import { SonarQuotationComponent } from './tabs-project/project-sonar/sonar-dashboard/sonar-quotation/sonar-quotation.component';
import { SonarMetricsComponent } from './tabs-project/project-sonar/sonar-metrics/sonar-metrics.component';
import { SonarThumbnailsComponent } from './tabs-project/project-sonar/sonar-thumbnails/sonar-thumbnails.component';
import { ProjectStaffComponent } from './tabs-project/project-staff/project-staff.component';
import { ChartInProgressComponent } from './tabs-project/project-sunburst/chart-in-progress/chart-in-progress.component';
import { DialogFilterComponent } from './tabs-project/project-sunburst/dialog-filter/dialog-filter.component';
// tslint:disable-next-line:max-line-length
import { DialogLegendSunburstComponent } from './tabs-project/project-sunburst/legend-sunburst/legend-sunburst.component';
import { ListContributorsComponent } from './tabs-project/project-sunburst/node-detail/list-contributors/list-contributors.component';
import { ListFilenamesComponent } from './tabs-project/project-sunburst/node-detail/list-filenames/list-filenames.component';
import { NodeDetailComponent } from './tabs-project/project-sunburst/node-detail/node-detail.component';
import { ProjectGhostsComponent } from './tabs-project/project-sunburst/project-ghosts/project-ghosts.component';
import { TableGhostsComponent } from './tabs-project/project-sunburst/project-ghosts/table-ghosts/table-ghosts.component';
import { ProjectSunburstComponent } from './tabs-project/project-sunburst/project-sunburst.component';
import { SSEWatcherComponent } from './tabs-project/project-sunburst/ssewatcher/ssewatcher.component';
import { InLineEditDialogComponent } from './tabs-project/project-sunburst/table-dependencies/in-line-edit-dialog/in-line-edit-dialog.component';
import { TableDependenciesComponent } from './tabs-project/project-sunburst/table-dependencies/table-dependencies.component';
import { ProjectComponent } from './tabs-project/project.component';
import { StaffListComponent } from './tabs-staff-list/staff-list/staff-list.component';
import { TabsStaffListComponent } from './tabs-staff-list/tabs-staff-list.component';
import { StaffService } from './tabs-staff/service/staff.service';
import { StaffExperienceComponent } from './tabs-staff/staff-experience/staff-experience.component';
import { StaffUploadCvComponent } from './tabs-staff/staff-experience/staff-upload-cv/staff-upload-cv.component';
import { TagifyStarsComponent } from './tabs-staff/staff-experience/tagify-stars/tagify-stars.component';
import { StaffFormComponent } from './tabs-staff/staff-form/staff-form.component';
import { StaffProjectsComponent } from './tabs-staff/staff-projects/staff-projects.component';
import { StaffRemoveComponent } from './tabs-staff/staff-remove/staff-remove.component';
import { StaffComponent } from './tabs-staff/staff.component';
import { WelcomeComponent } from './welcome/welcome.component';
import { TreemapProjectsContainerComponent } from './dashboard/treemap-projects/treemap-projects-container/treemap-projects-container.component';
import { TreemapProjectsChartComponent } from './dashboard/treemap-projects/treemap-projects-chart/treemap-projects-chart.component';
import { TableProjectsFilterComponent } from './tabs-project/table-projects-filter/table-projects-filter.component';
import { StarfieldComponent } from './dashboard/starfield/starfield.component';
import { StarfieldHeaderComponent } from './dashboard/starfield/starfield-header/starfield-header.component';
import { StarfieldContentComponent } from './dashboard/starfield/starfield-content/starfield-content.component';
import { SummaryComponent } from './dashboard/summary/component/summary.component';

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
		AuditAttachmentComponent,
		AuditUploadAttachmentComponent,
		FitzhiDashboardComponent,
		PieChartComponent,
		PieProjectsComponent,
		SSEWatcherComponent,
		TreemapSkillsChartComponent,
		TreemapHeaderComponent,
		TreemapSkillsComponent,
		ProjectRemoveComponent,
		ProjectInactivateComponent,
		StaffRemoveComponent,
		PieLegendComponent,
		BranchComponent,
		SkylineComponent,
		SkylineIconComponent,
		ChartInProgressComponent,
		TreemapProjectsContainerComponent,
		TreemapProjectsChartComponent,
		TableProjectsFilterComponent,
		StarfieldComponent,
		StarfieldHeaderComponent,
		StarfieldContentComponent,
		SummaryComponent
	],
	imports: [
		BsDropdownModule.forRoot(),
		TooltipModule.forRoot(),
		ModalModule.forRoot(),
		BrowserModule,
		AppRoutingModule,
		NgbModule,
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
		NgxChartsModule,
		NgxPopper,
		DynamicPieChartModule,
		ControlledRisingSkylineModule,
		RisingSkylineModule
	],
	providers: [
		CinematicService,
		StaffService,
		ReferentialService,
		PieDashboardService,
		AuthGuardService,
		TokenService,
		{
			provide: HTTP_INTERCEPTORS,
			useClass: HttpErrorInterceptorService,
			multi: true
		},
		{
			provide: HTTP_INTERCEPTORS,
			useClass: HttpTokenInterceptor,
			multi: true
		},
		{
			provide: HTTP_INTERCEPTORS,
			useClass: HttpRefreshTokenErrorInterceptor,
			multi: true
		},
		DatePipe,
		// Remove this line or change the useValue property to your regional settings
		{ provide: LOCALE_ID, useValue: 'fr' }
	],
	bootstrap: [AppComponent]
})
export class AppModule {
	constructor() {
		// Remove this line to return back to the default us property,
		// or change the useValue property to your regional settings
		registerLocaleData(localeFr, 'fr');
	}
}
