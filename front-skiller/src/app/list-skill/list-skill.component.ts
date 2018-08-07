import {Component, OnInit, Output, EventEmitter} from '@angular/core';
import {CinematicService} from '../cinematic.service';
import {DataService} from '../data.service';
import {Constants} from '../constants';
import {Skill} from '../data/skill';

@Component({
  selector: 'app-list-skill',
  templateUrl: './list-skill.component.html',
  styleUrls: ['./list-skill.component.css']
})
export class ListSkillComponent implements OnInit {

  private skills: Skill[];

  constructor(
    private cinematicService: CinematicService,
    private dataService: DataService) {}

  ngOnInit() {
    this.cinematicService.setForm(Constants.SKILLS_SEARCH);
    this.skills = this.dataService.getSkills();
  }

  public search(source: string): void {
    if (Constants.DEBUG) {
      console.log('Searching a skill');
    }
  }
}
