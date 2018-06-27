import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms'; // <-- NgModel lives here
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { BsDropdownModule } from 'ngx-bootstrap/dropdown';
import { TooltipModule } from 'ngx-bootstrap/tooltip';
import { ModalModule } from 'ngx-bootstrap/modal';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { SkillComponent } from './skill/skill.component';
import { SearchSkillComponent } from './search-skill/search-skill.component';
import { WelcomeComponent } from './welcome/welcome.component';

import { CinematicService } from './cinematic.service';
import { UserComponent } from './user/user.component';
import { SearchUserComponent } from './search-user/search-user.component';

import { DataService } from './data.service';

@NgModule({
  declarations: [
    AppComponent,
    SkillComponent,
    SearchSkillComponent,
    WelcomeComponent,
    UserComponent,
    SearchUserComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BsDropdownModule.forRoot(),
    TooltipModule.forRoot(),
    ModalModule.forRoot(),
    NgbModule.forRoot(),
    FormsModule
  ],
  providers: [CinematicService, DataService],
  bootstrap: [AppComponent]
})
export class AppModule { 
}
