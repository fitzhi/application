import { Component, OnInit } from '@angular/core';
import {CinematicService} from '../cinematic.service';
import {DataService} from '../data.service';
import {Constants} from '../constants';
import {Project} from '../data/project';

@Component({
  selector: 'app-list-project',
  templateUrl: './list-project.component.html',
  styleUrls: ['./list-project.component.css']
})
export class ListProjectComponent implements OnInit {

  private projects: Project[];

  constructor(
    private cinematicService: CinematicService,
    private dataService: DataService) {}

  ngOnInit() {
    this.cinematicService.setForm(Constants.PROJECT_SEARCH);
    this.projects = this.dataService.getProjects();
  }

  public search(source: string): void {
    if (Constants.DEBUG) {
      console.log('Searching a project');
    }
  }
}
