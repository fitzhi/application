import {BrowserModule} from '@angular/platform-browser';
import {NgModule, LOCALE_ID} from '@angular/core';
import {FormsModule} from '@angular/forms'; // <-- NgModel lives here
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';

import {BsDropdownModule} from 'ngx-bootstrap/dropdown';
import {TooltipModule} from 'ngx-bootstrap/tooltip';
import {ModalModule} from 'ngx-bootstrap/modal';

import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {SkillComponent} from './skill/skill.component';
import {ListSkillComponent} from './list-skill/list-skill.component';
import {WelcomeComponent} from './welcome/welcome.component';

import {CinematicService} from './service/cinematic.service';
import {StaffComponent} from './tabs-staff/staff.component';

import {StaffService} from './service/staff.service';
import { MessageComponent } from './message/message.component';

import { ErrorComponent } from './error/error.component';

import { Ng2SmartTableModule } from 'ng2-smart-table';

import { StarsSkillLevelRenderComponent } from './tabs-staff/starsSkillLevelRenderComponent';
import { ProjectFormComponent } from './project/project-form/project-form.component';
import { ListProjectComponent } from './list-project/list-project.component';
import { ReferentialService } from './service/referential.service';

import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { StaffTabsComponent } from './tabs-staff/staff-tabs/staff-tabs.component';
// tslint:disable-next-line:max-line-length
import { UploadedSkillsPickupComponent } from './tabs-staff/staff-experience/staff-upload-cv/pickup/uploaded-skills-pickup.component';
import { StaffFormComponent } from './tabs-staff/staff-form/staff-form.component';
import { StaffProjectsComponent } from './tabs-staff/staff-projects/staff-projects.component';
import { StaffExperienceComponent } from './tabs-staff/staff-experience/staff-experience.component';
import { StaffUploadCvComponent } from './tabs-staff/staff-experience/staff-upload-cv/staff-upload-cv.component';
import { MatNativeDateModule, MatExpansionModule, MatGridListModule } from '@angular/material';
import {  MatSortModule, MatButtonToggleModule, MatSidenavModule, MatCardModule, MatFormFieldModule } from '@angular/material';
import {  MatInputModule, MatSnackBarModule, MatSelectModule, MatDatepickerModule } from '@angular/material';
import { MatTabsModule, MatDialogModule, MatProgressBarModule, MatPaginatorModule } from '@angular/material';
import { MessageBoxComponent } from './message-box/dialog/message-box.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';
import { ProjectComponent } from './project/project.component';
import { ProjectSunburstComponent } from './project/project-sunburst/project-sunburst.component';
import { ProjectStaffComponent } from './project/project-staff/project-staff.component';
import { DialogProjectGhostsComponent } from './project/project-sunburst/dialog-project-ghosts/dialog-project-ghosts.component';
import { ProjectGhostsComponent } from './project/project-sunburst/dialog-project-ghosts/project-ghosts/project-ghosts.component';
// tslint:disable-next-line:max-line-length
import { DialogUpdatedProjectGhostsComponent } from './project/project-sunburst/dialog-project-ghosts/dialog-updated-project-ghosts/dialog-updated-project-ghosts.component';
import { DialogLegendSunburstComponent } from './project/project-sunburst/dialog-legend-sunburst/dialog-legend-sunburst.component';
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
import { ConnectionComponent } from './admin/connection/connection.component';

@NgModule({
  declarations: [
    AppComponent,
    SkillComponent,
    ListSkillComponent,
    WelcomeComponent,
    StaffComponent,
    MessageComponent,
    ErrorComponent,
    StarsSkillLevelRenderComponent,
    ProjectFormComponent,
    ListProjectComponent,
    StaffTabsComponent,
    StaffFormComponent,
    StaffProjectsComponent,
    StaffExperienceComponent,
    StaffUploadCvComponent,
    MessageBoxComponent,
    UploadedSkillsPickupComponent,
    ProjectComponent,
    ProjectSunburstComponent,
    ProjectStaffComponent,
    DialogProjectGhostsComponent,
    ProjectGhostsComponent,
    DialogUpdatedProjectGhostsComponent,
    DialogLegendSunburstComponent,
    DialogFilterComponent,
    ListFilenamesComponent,
    NodeDetailComponent,
    ListContributorsComponent,
    TabsStaffListComponent,
    StaffListComponent,
    BackendSetupComponent,
    ConnectionComponent,
  ],
  entryComponents: [
    StarsSkillLevelRenderComponent,
    StaffUploadCvComponent,
    MessageBoxComponent,
    UploadedSkillsPickupComponent,
    DialogProjectGhostsComponent,
    DialogUpdatedProjectGhostsComponent,
    DialogLegendSunburstComponent,
    DialogFilterComponent,
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
    Ng2SmartTableModule,
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
  ],
  providers: [
    CinematicService,
    StaffService,
    ReferentialService,
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
    registerLocaleData (localeFr, 'fr');
  }
}
