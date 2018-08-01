import {Component, OnInit, Output, EventEmitter} from '@angular/core';
import { CinematicService } from '../cinematic.service';
import { DataService } from '../data.service';
import {Constants} from '../constants';
import {Skill} from '../data/skill';

@Component({
  selector: 'app-search-skill',
  templateUrl: './search-skill.component.html',
  styleUrls: ['./search-skill.component.css']
})
export class SearchSkillComponent implements OnInit {

  private skills: Skill[];

  constructor(
    private cinematicService: CinematicService,
    private dataService: DataService) {}

	ngOnInit() {
		this.cinematicService.setForm(Constants.SKILLS_SEARCH);
		this.skills = this.dataService.getSkills();
  	}

	public search(source: string) : void {
		console.log("searching a skill");
	}
}