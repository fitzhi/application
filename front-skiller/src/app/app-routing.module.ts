import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from '@angular/router';

import {SkillComponent} from './skill/skill.component';
import {ListSkillComponent} from './list-skill/list-skill.component';
import {ProjectComponent} from './project/project.component';
import {ListProjectComponent} from './list-project/list-project.component';
import {WelcomeComponent} from './welcome/welcome.component';
import {StaffComponent} from './staff/staff.component';
import {ListStaffComponent} from './list-staff/list-staff.component';
import {ErrorComponent} from './error/error.component';
import {SunburstTestComponent} from './d3/sunburst-test/sunburst-test.component';

const routes: Routes = [
  {path: '', redirectTo: '/welcome', pathMatch: 'full'},
  {path: 'welcome', component: WelcomeComponent},
  {path: 'searchSkill', component: ListSkillComponent},
  {path: 'skill', component: SkillComponent},
  {path: 'skill/:id', component: SkillComponent},
  {path: 'searchUser', component: ListStaffComponent},
  {path: 'user/:id', component: StaffComponent},
  {path: 'user', component: StaffComponent},
  {path: 'error', component: ErrorComponent},
  {path: 'searchProject', component: ListProjectComponent},
  {path: 'project', component: ProjectComponent},
  {path: 'project/:id', component: ProjectComponent},
  {path: 'sunburst-test', component: SunburstTestComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
