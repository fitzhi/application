import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from '@angular/router';

import {SkillComponent} from './skill/skill.component';
import {ListSkillComponent} from './list-skill/list-skill.component';
import {WelcomeComponent} from './welcome/welcome.component';
import {UserComponent} from './user/user.component';
import {ListStaffComponent} from './list-staff/list-staff.component';
import {ErrorComponent} from './error/error.component';

const routes: Routes = [
  {path: '', redirectTo: '/welcome', pathMatch: 'full'},
  {path: 'welcome', component: WelcomeComponent},
  {path: 'searchSkill', component: ListSkillComponent},
  {path: 'skill', component: SkillComponent},
  {path: 'skill/:id', component: SkillComponent},
  {path: 'searchUser', component: ListStaffComponent},
  {path: 'user/:id', component: UserComponent},
  {path: 'user', component: UserComponent},
  {path: 'error', component: ErrorComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}