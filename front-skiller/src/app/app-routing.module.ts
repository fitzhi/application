import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {SkillComponent} from './skill/skill.component';
import {ListSkillComponent} from './list-skill/list-skill.component';
import {ProjectComponent} from './project/project.component';
import {ProjectSunburstComponent} from './project/project-sunburst/project-sunburst.component';
import {ListProjectComponent} from './list-project/list-project.component';
import {WelcomeComponent} from './welcome/welcome.component';
import {StaffComponent} from './tabs-staff/staff.component';
import {ErrorComponent} from './error/error.component';
import { ProjectStaffComponent } from './project/project-staff/project-staff.component';
import { TabsStaffListComponent } from './tabs-staff-list/tabs-staff-list.component';

const routes: Routes = [
  {path: '', redirectTo: '/welcome', pathMatch: 'full'},
  {path: 'welcome', component: WelcomeComponent},
  {path: 'searchSkill', component: ListSkillComponent},
  {path: 'skill', component: SkillComponent},
  {path: 'skill/:id', component: SkillComponent},
  {path: 'searchUser', component: TabsStaffListComponent},
  {path: 'user/:id', component: StaffComponent},
  {path: 'user', component: StaffComponent},
  {path: 'error', component: ErrorComponent},
  {path: 'searchProject', component: ListProjectComponent},
  {path: 'project', component: ProjectComponent},
  {path: 'project/:id', component: ProjectComponent},
  {path: 'project/:id/staff', component: ProjectComponent},
  {path: 'project/sunburst/:id', component: ProjectSunburstComponent},
  {path: 'project/contributors/:id', component: ProjectStaffComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
