import {AppModule} from '../app.module';
import {Component, OnInit} from '@angular/core';
import {CinematicService} from '../cinematic.service';
import {Collaborater} from '../data/collaborater';

import {Level} from '../data/level';
import {Attribution} from '../data/attribution';
import {Project} from '../data/project';

import {LIST_OF_LEVELS} from '../data/List_of_levels';
import {PROJECTS} from '../mock/mock-projects';
import {Constants} from '../constants';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {


  levels: Level[] = LIST_OF_LEVELS;
  projects: Project[] = PROJECTS;

  collaborater: Collaborater;

  constructor(private cinematicService: CinematicService) {}

  ngOnInit() {
    this.collaborater = new Collaborater(0, 'Frédéric', 'VIDAL', 'altF4', 'frvidal@sqli.com', 'ET 2', []);
    this.collaborater.projects = [{project_id: 1, from_date: null, to_date: null}, {project_id: 2, from_date: null, to_date: null}];
    this.cinematicService.setForm(Constants.DEVELOPERS_CRUD);
  }

  /**
   * The Validate Button has been activated
   */
  save(): void {
    if (Constants.DEBUG) {
      console.log('Saving data for the collaborater below');
      console.log(this.collaborater);
    }
  }

}


