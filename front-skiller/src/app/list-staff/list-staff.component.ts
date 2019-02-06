import {Component, OnInit, Input} from '@angular/core';
import {CinematicService} from '../cinematic.service';
import {Constants} from '../constants';
import {Collaborator} from '../data/collaborator';
import {Experience} from '../data/experience';
import {Profile} from '../data/profile';
import {ListStaffService} from '../list-staff-service/list-staff.service';
import {ReferentialService} from '../referential.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-list-staff',
  templateUrl: './list-staff.component.html',
  styleUrls: ['./list-staff.component.css']
})
export class ListStaffComponent implements OnInit {

  public collaborators: Collaborator[];

  private profiles: Profile[];

  constructor(
    private cinematicService: CinematicService,
    private referentialService: ReferentialService,
    private listStaffService: ListStaffService,
    private router: Router) {}

  ngOnInit() {
    this.cinematicService.setForm(Constants.DEVELOPERS_SEARCH, this.router.url);
    this.collaborators = this.listStaffService.getStaff();

    this.referentialService.subjectProfiles.subscribe(
      (profiles: Profile[]) => this.profiles = profiles);
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
}
