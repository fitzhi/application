import {Component, OnInit, Input, OnDestroy} from '@angular/core';
import {CinematicService} from '../service/cinematic.service';
import {Constants} from '../constants';
import {Collaborator} from '../data/collaborator';
import {Experience} from '../data/experience';
import {Profile} from '../data/profile';
import {StaffListService} from '../staff-list-service/staff-list.service';
import {ReferentialService} from '../service/referential.service';
import { Router } from '@angular/router';
import { BaseComponent } from '../base/base.component';

@Component({
  selector: 'app-list-staff',
  templateUrl: './list-staff.component.html',
  styleUrls: ['./list-staff.component.css']
})
export class ListStaffComponent extends BaseComponent implements OnInit, OnDestroy {

  public collaborators: Collaborator[];

  private profiles: Profile[];

  constructor(
    private cinematicService: CinematicService,
    private referentialService: ReferentialService,
    private staffListService: StaffListService,
    private router: Router) {
      super();
    }

  ngOnInit() {
    this.cinematicService.setForm(Constants.DEVELOPERS_SEARCH, this.router.url);
    this.collaborators = this.staffListService.getStaff();

    this.subscriptions.add(
      this.referentialService.subjectProfiles.subscribe(
        (profiles: Profile[]) => this.profiles = profiles));
  }

  levelTitle(code: String): String {
    const found = this.profiles.find(profile => (profile.code === code));
    if (typeof found === 'undefined') {
      return '';
    } else {
      return found.title;
    }
  }

  mainSkills(experiences: Experience[]): String {
    let mainSkills = '';
    let evaluatedSkill: Experience[];
    for (let level = Constants.LEVEL_Expert; (level >= Constants.LEVEL_Beginner); level--) {
      evaluatedSkill = experiences.filter(expe => (expe.level === level));
      evaluatedSkill.forEach(experience => {mainSkills += experience.title + ', '; });
    }
    return mainSkills;
  }

  ngOnDestroy() {
    super.ngOnDestroy();
  }
}
