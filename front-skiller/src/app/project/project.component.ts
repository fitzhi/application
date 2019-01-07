import { Component, OnInit } from '@angular/core';
import {CinematicService} from '../cinematic.service';
import {Constants} from '../constants';
import { MatTabChangeEvent } from '@angular/material';

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css']
})
export class ProjectComponent implements OnInit {

  constructor(
    private cinematicService: CinematicService) {
  }

  ngOnInit() {
  }

  public onTabChange(tabChangeEvent: MatTabChangeEvent): void {
    if (Constants.DEBUG) {
      console.log ('The index ' + tabChangeEvent.index + ' is selected !');
    }
    this.cinematicService.setProjectTab(tabChangeEvent.index);
  }

}
