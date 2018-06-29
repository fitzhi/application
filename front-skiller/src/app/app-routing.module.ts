import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from '@angular/router';

import {SkillComponent} from './skill/skill.component';
import {SearchSkillComponent} from './search-skill/search-skill.component';
import {WelcomeComponent} from './welcome/welcome.component';
import {UserComponent} from './user/user.component';
import {SearchUserComponent} from './search-user/search-user.component';

const routes: Routes = [
  {path: '', redirectTo: '/welcome', pathMatch: 'full'},
  {path: 'welcome', component: WelcomeComponent},
  {path: 'searchSkill', component: SearchSkillComponent},
  {path: 'skill', component: SkillComponent},
  {path: 'searchUser', component: SearchUserComponent},
  {path: 'user/:id', component: UserComponent}  
  {path: 'user', component: UserComponent}  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}