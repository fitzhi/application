import {Component, OnInit, Input} from '@angular/core';
import {CinematicService} from '../cinematic.service';
import {Constants} from '../constants';
import {Collaborator} from '../data/collaborator';
import { Profile } from '../data/profile';
import { ListStaffService } from '../list-staff-service/list-staff.service';
import { ReferentialService } from '../referential.service';

@Component({
  selector: 'app-list-staff',
  templateUrl: './list-staff.component.html',
  styleUrls: ['./list-staff.component.css']
})
export class ListStaffComponent implements OnInit {

  private collaborators: Collaborator[];

  private profiles: Profile[];

  constructor(
    private cinematicService: CinematicService,
    private referentialService: ReferentialService,
    private listStaffService: ListStaffService) {}

  ngOnInit() {
    this.cinematicService.setForm(Constants.DEVELOPPERS_SEARCH);
    this.collaborators = this.listStaffService.getStaff();

    this.referentialService.behaviorSubjectProfiles.subscribe(
      (profiles: Profile[]) => this.profiles = profiles);
  }

  levelTitle (code: String): String {
    const found = this.profiles.find(profile => (profile.code === code));
    if (typeof found === 'undefined') {
      return '';
    } else {
      return found.title;
    }
  }
}
