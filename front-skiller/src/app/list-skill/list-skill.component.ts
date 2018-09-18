import {Component, OnInit, Output, EventEmitter} from '@angular/core';
import {CinematicService} from '../cinematic.service';
import {DataService} from '../data.service';
import {Constants} from '../constants';
import {Skill} from '../data/skill';
import { ListSkillService } from '../list-skill-service/list-skill.service';

@Component({
  selector: 'app-list-skill',
  templateUrl: './list-skill.component.html',
  styleUrls: ['./list-skill.component.css']
})
export class ListSkillComponent implements OnInit {

  private skills: Skill[];

  constructor(
    private cinematicService: CinematicService,
    private listSkillService: ListSkillService,
    private dataService: DataService) {}

  ngOnInit() {
    this.cinematicService.setForm(Constants.SKILLS_SEARCH);
    this.skills = this.listSkillService.getSkills();
  }

  public search(source: string): void {
    if (Constants.DEBUG) {
      console.log('Searching a skill');
    }
  }
}
