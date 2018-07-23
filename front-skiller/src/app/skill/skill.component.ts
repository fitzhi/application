import { Component, OnInit } from '@angular/core';
import { CinematicService } from '../cinematic.service';
import {Constants} from '../constants';
import {Skill} from '../data/skill';

import {DataService} from '../data.service';

@Component({
  selector: 'app-skill',
  templateUrl: './skill.component.html',
  styleUrls: ['./skill.component.css']
})
export class SkillComponent implements OnInit {

  private skill: Skill;

  constructor(
    private cinematicService: CinematicService,
    private dataService: DataService ) {}

  ngOnInit() {
    this.cinematicService.setForm(Constants.SKILLS_CRUD);

    this.skill = new Skill();
  }

  /**
   * Save the skill created or updated.
   */
  save() {
    if (Constants.DEBUG) {
      console.log('saving the skill ' + this.skill.title);
    }
    this.dataService.saveSkill(this.skill);
  }

}
