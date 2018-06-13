import {AppModule} from '../app.module';
import {Component, OnInit} from '@angular/core';
import {CinematicService} from '../cinematic.service';
import {Collaborater} from '../collaborater';

import {Level} from '../data/level';

import {LIST_OF_LEVELS} from '../data/List_of_levels';
import {Constants} from '../constants';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent implements OnInit {


  levels: Level[] = LIST_OF_LEVELS;


  collaborater: Collaborater;

  constructor(private cinematicService: CinematicService) {}

  ngOnInit() {
    this.collaborater = new Collaborater(0, 'Frédéric', 'VIDAL', 'altF4', 'frvidal@sqli.com', 'ET 2');
    this.cinematicService.setForm('Welcome to a new developer !');
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


