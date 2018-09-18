import { Component, OnInit } from '@angular/core';
import {CinematicService} from '../cinematic.service';
import {Constants} from '../constants';
import {Project} from '../data/project';
import { ListProjectsService } from '../list-projects-service/list-projects.service';

@Component({
  selector: 'app-list-project',
  templateUrl: './list-project.component.html',
  styleUrls: ['./list-project.component.css']
})
export class ListProjectComponent implements OnInit {

  private projects: Project[];

  constructor(
    private cinematicService: CinematicService,
    private listProjectsService: ListProjectsService) {}

  ngOnInit() {
    this.cinematicService.setForm(Constants.PROJECT_SEARCH);
    this.projects = this.listProjectsService.getProjects();
  }

  public search(source: string): void {
    if (Constants.DEBUG) {
      console.log('Searching a project');
    }
  }
}
