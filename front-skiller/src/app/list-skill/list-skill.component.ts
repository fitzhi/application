import {Component, OnInit, Output, EventEmitter} from '@angular/core';
import {CinematicService} from '../cinematic.service';
import {Constants} from '../constants';
import {Skill} from '../data/skill';
import { ListSkillService } from '../list-skill-service/list-skill.service';
import { StaffService } from '../staff.service';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-list-skill',
  templateUrl: './list-skill.component.html',
  styleUrls: ['./list-skill.component.css']
})
export class ListSkillComponent implements OnInit {

  private skills: Skill[];

  private peopleCountExperience: Map<string, number> = null;

  private vide = {};
  public behaviorSubjectCountExperience = new BehaviorSubject(this.vide);

  constructor(
    private cinematicService: CinematicService,
    private staffService: StaffService,
    private listSkillService: ListSkillService) {}

  ngOnInit() {
    this.cinematicService.setForm(Constants.SKILLS_SEARCH);
    this.skills = this.listSkillService.getSkills();
    this.peopleCountExperience = this.staffService.getPeopleCountExperience();
  }

}
