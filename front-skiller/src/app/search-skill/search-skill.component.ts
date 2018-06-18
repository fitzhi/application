import {Component, OnInit, Output, EventEmitter} from '@angular/core';
import { CinematicService } from '../cinematic.service';
import {Constants} from '../constants';

@Component({
  selector: 'app-search-skill',
  templateUrl: './search-skill.component.html',
  styleUrls: ['./search-skill.component.css']
})
export class SearchSkillComponent implements OnInit {

  constructor(private cinematicService:CinematicService) {}

  ngOnInit() {
    this.cinematicService.setForm(Constants.DEVELOPERS_SEARCH);
  }

}