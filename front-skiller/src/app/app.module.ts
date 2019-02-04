import {BrowserModule} from '@angular/platform-browser';
import {NgModule, ErrorHandler} from '@angular/core';
import {FormsModule} from '@angular/forms'; // <-- NgModel lives here
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { HttpClientModule } from '@angular/common/http';
import { HttpClientInMemoryWebApiModule } from 'angular-in-memory-web-api';
import { InMemoryDataService } from './net/in-memory-data.service';
import { ReactiveFormsModule } from '@angular/forms';

import {BsDropdownModule} from 'ngx-bootstrap/dropdown';
import {TooltipModule} from 'ngx-bootstrap/tooltip';
import {ModalModule} from 'ngx-bootstrap/modal';

import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {SkillComponent} from './skill/skill.component';
import {ListSkillComponent} from './list-skill/list-skill.component';
import {WelcomeComponent} from './welcome/welcome.component';

import {CinematicService} from './cinematic.service';
import {StaffComponent} from './staff/staff.component';
import {ListStaffComponent} from './list-staff/list-staff.component';

import {StaffService} from './staff.service';
import { MessageComponent } from './message/message.component';

import {ErrorsHandler} from './errors-handler';
import { ErrorComponent } from './error/error.component';

import { Ng2SmartTableModule } from 'ng2-smart-table';

import { StarsSkillLevelRenderComponent } from './staff/starsSkillLevelRenderComponent';
import { ProjectFormComponent } from './project/project-form/project-form.component';
import { ListProjectComponent } from './list-project/list-project.component';
import { ReferentialService } from './referential.service';

import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { StaffTabsComponent } from './staff/staff-tabs/staff-tabs.component';
import { UploadedSkillsPickupComponent } from './staff/staff-tabs/staff-experience/staff-upload-cv/pickup/uploaded-skills-pickup.component';
import { StaffFormComponent } from './staff/staff-form/staff-form.component';
import { StaffProjectsComponent } from './staff/staff-tabs/staff-projects/staff-projects.component';
import { StaffExperienceComponent } from './staff/staff-tabs/staff-experience/staff-experience.component';
import { StaffUploadCvComponent } from './staff/staff-tabs/staff-experience/staff-upload-cv/staff-upload-cv.component';
import {  MatSortModule, MatButtonToggleModule, MatSidenavModule, MatCardModule, MatFormFieldModule } from '@angular/material';
import {  MatInputModule } from '@angular/material';
import { MatTabsModule, MatDialogModule, MatProgressBarModule, MatPaginatorModule } from '@angular/material';
import { MessageBoxComponent } from './message-box/dialog/message-box.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';
import { SunburstTestComponent } from './d3/sunburst-test/sunburst-test.component';
import { ProjectComponent } from './project/project.component';
import { ProjectSunburstComponent } from './project/project-sunburst/project-sunburst.component';
import { ProjectStaffComponent } from './project/project-staff/project-staff.component';
import { DialogProjectGhostsComponent } from './project/project-sunburst/dialog-project-ghosts/dialog-project-ghosts.component';
import { ProjectGhostsComponent } from './project/project-sunburst/dialog-project-ghosts/project-ghosts/project-ghosts.component';
// tslint:disable-next-line:max-line-length
import { DialogUpdatedProjectGhostsComponent } from './project/project-sunburst/dialog-project-ghosts/dialog-updated-project-ghosts/dialog-updated-project-ghosts.component';
import { DialogLegendSunburstComponent } from './project/project-sunburst/dialog-legend-sunburst/dialog-legend-sunburst.component';

@NgModule({
  declarations: [
    AppComponent,
    SkillComponent,
    ListSkillComponent,
    WelcomeComponent,
    StaffComponent,
    ListStaffComponent,
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
    SunburstTestComponent,
    ProjectComponent,
    ProjectSunburstComponent,
    ProjectStaffComponent,
    DialogProjectGhostsComponent,
    ProjectGhostsComponent,
    DialogUpdatedProjectGhostsComponent,
    DialogLegendSunburstComponent,
  ],
  entryComponents: [
    StarsSkillLevelRenderComponent,
    StaffUploadCvComponent,
    MessageBoxComponent,
    UploadedSkillsPickupComponent,
    DialogProjectGhostsComponent,
    DialogUpdatedProjectGhostsComponent,
    DialogLegendSunburstComponent,
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

    // The HttpClientInMemoryWebApiModule module intercepts HTTP requests
    // and returns simulated server responses.
    // Remove it when a real server is ready to receive requests.
    /* We use now the server on a tomcat :8080
    HttpClientInMemoryWebApiModule.forRoot(
      InMemoryDataService, { dataEncapsulation: false }
    )
    */
  ],
  providers: [
    CinematicService,
    StaffService,
    ReferentialService,
  ],
  bootstrap: [AppComponent]
})
/*    {
      provide: ErrorHandler,
      useClass: ErrorsHandler,
    }
 */
export class AppModule {
}
