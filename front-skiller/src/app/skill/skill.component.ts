import { Component, OnInit } from '@angular/core';
import { CinematicService } from '../cinematic.service';
import {Constants} from '../constants';
import {Skill} from '../data/skill';

@Component({
  selector: 'app-skill',
  templateUrl: './skill.component.html',
  styleUrls: ['./skill.component.css']
})
export class SkillComponent implements OnInit {

  private skill: Skill;

  constructor(private cinematicService: CinematicService) {}

  ngOnInit() {
    this.cinematicService.setForm(Constants.SKILLS_CRUD);

    this.skill = new Skill();
  }

  /**
   * Save the skill created or updated.
   */
  save() {

  }
  
}
