import { Component, OnInit } from '@angular/core';
import { CinematicService } from '../cinematic.service';
import { Constants } from '../constants';
import { MatTabChangeEvent } from '@angular/material';
import { Router } from '@angular/router';

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css']
})
export class ProjectComponent implements OnInit {

  /**
   * Index of the tab selected.
   */
  public tabIndex = 0;

  constructor(
    private cinematicService: CinematicService,
    private router: Router) {
  }

  ngOnInit() {
    if (Constants.DEBUG) {
      console.log('Current url ' + this.router.url);
    }
    if (this.router.url.indexOf('/staff') !== -1) {
      this.tabIndex = 1;
      if (Constants.DEBUG) {
        console.log('Index selected ' + this.tabIndex + ' to display the project staff list');
      }
    }
  }

  public onTabChange(tabChangeEvent: MatTabChangeEvent): void {
    if (Constants.DEBUG) {
      console.log('The index ' + tabChangeEvent.index + ' is selected !');
    }
    this.cinematicService.setProjectTab(tabChangeEvent.index);
  }

}
