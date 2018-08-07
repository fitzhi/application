import { Component, OnInit } from '@angular/core';
import {ActivatedRoute} from '@angular/router';

import {DataService} from '../data.service';
import {MessageService} from '../message.service';
import { CinematicService } from '../cinematic.service';

import { Constants } from '../constants';
import { LocalDataSource } from 'ng2-smart-table';

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css']
})
export class ProjectComponent implements OnInit {

  private sourceSkills = new LocalDataSource([]);
  private settings_skills = Constants.SETTINGS_SKILL_SMARTTABLE;

  constructor(
    private cinematicService: CinematicService,
    private route: ActivatedRoute,
    private dataService: DataService,
    private messageService: MessageService) { }

  ngOnInit() {
    this.cinematicService.setForm(Constants.PROJECT_CRUD);
  }

}
