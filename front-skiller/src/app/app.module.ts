import {BrowserModule} from '@angular/platform-browser';
import {NgModule, ErrorHandler} from '@angular/core';
import {FormsModule} from '@angular/forms'; // <-- NgModel lives here
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import { HttpClientModule } from '@angular/common/http';
import { HttpClientInMemoryWebApiModule } from 'angular-in-memory-web-api';
import { InMemoryDataService } from './net/in-memory-data.service';

import {BsDropdownModule} from 'ngx-bootstrap/dropdown';
import {TooltipModule} from 'ngx-bootstrap/tooltip';
import {ModalModule} from 'ngx-bootstrap/modal';

import {AppComponent} from './app.component';
import {AppRoutingModule} from './app-routing.module';
import {SkillComponent} from './skill/skill.component';
import {SearchSkillComponent} from './search-skill/search-skill.component';
import {WelcomeComponent} from './welcome/welcome.component';

import {CinematicService} from './cinematic.service';
import {UserComponent} from './user/user.component';
import {SearchUserComponent} from './search-user/search-user.component';

import {DataService} from './data.service';
import {CollaboratorService} from './collaborator.service';
import { MessageComponent } from './message/message.component';

import {ErrorsHandler} from './errors-handler';

@NgModule({
  declarations: [
    AppComponent,
    SkillComponent,
    SearchSkillComponent,
    WelcomeComponent,
    UserComponent,
    SearchUserComponent,
    MessageComponent
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
    DataService,
    CinematicService,
    CollaboratorService,
    {
      provide: ErrorHandler,
      useClass: ErrorsHandler,
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
