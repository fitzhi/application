import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { SkillComponent } from './skill/skill.component';
import { ListSkillComponent } from './list-skill/list-skill.component';
import { ProjectComponent } from './project/project.component';
import { ProjectSunburstComponent } from './project/project-sunburst/project-sunburst.component';
import { ListProjectComponent } from './list-project/list-project.component';
import { WelcomeComponent } from './welcome/welcome.component';
import { StaffComponent } from './tabs-staff/staff.component';
import { ErrorComponent } from './error/error.component';
import { ProjectStaffComponent } from './project/project-staff/project-staff.component';
import { TabsStaffListComponent } from './tabs-staff-list/tabs-staff-list.component';
import { AuthGuardService } from './admin/security/auth-guard.service';
import { StaffFormComponent } from './tabs-staff/staff-form/staff-form.component';
import { ConnectUserComponent } from './admin/connect-user/connect-user.component';

const routes: Routes = [
	{ path: '', redirectTo: '/welcome', pathMatch: 'full' },
	{ path: 'welcome', component: WelcomeComponent },
	{ path: 'login', component: ConnectUserComponent },
	{ path: 'searchSkill', component: ListSkillComponent, canActivate: [AuthGuardService] },
	{ path: 'skill', component: SkillComponent, canActivate: [AuthGuardService] },
	{ path: 'skill/:id', component: SkillComponent, canActivate: [AuthGuardService] },
	{ path: 'searchUser', component: TabsStaffListComponent, canActivate: [AuthGuardService] },
	{ path: 'user/:id', component: StaffComponent, canActivate: [AuthGuardService] },
	{ path: 'user', component: StaffComponent, canActivate: [AuthGuardService] },
	{ path: 'user/form', component: StaffFormComponent, canActivate: [AuthGuardService] },
	{ path: 'error', component: ErrorComponent, canActivate: [AuthGuardService] },
	{ path: 'searchProject', component: ListProjectComponent, canActivate: [AuthGuardService] },
	{ path: 'project', component: ProjectComponent, canActivate: [AuthGuardService] },
	{ path: 'project/:id', component: ProjectComponent, canActivate: [AuthGuardService] },
	{ path: 'project/:id/staff', component: ProjectComponent, canActivate: [AuthGuardService] },
	{ path: 'project/sunburst/:id', component: ProjectSunburstComponent, canActivate: [AuthGuardService] },
	{ path: 'project/contributors/:id', component: ProjectStaffComponent, canActivate: [AuthGuardService] },
];

@NgModule({
	imports: [RouterModule.forRoot(routes)],
	exports: [RouterModule]
})
export class AppRoutingModule { }
