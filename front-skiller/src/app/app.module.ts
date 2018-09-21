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
import { ProjectComponent } from './project/project.component';
import { ListProjectComponent } from './list-project/list-project.component';
import { ReferentialService } from './referential.service';

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
    ProjectComponent,
    ListProjectComponent
  ],
  entryComponents: [StarsSkillLevelRenderComponent],
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
