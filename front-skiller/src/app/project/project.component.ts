import {Component, OnInit} from '@angular/core';
import {FormGroup, FormControl} from '@angular/forms';
import {ActivatedRoute} from '@angular/router';

import {DataService} from '../data.service';
import {MessageService} from '../message.service';
import {CinematicService} from '../cinematic.service';

import {Project} from '../data/project';
import {Constants} from '../constants';
import {LocalDataSource} from 'ng2-smart-table';

@Component({
  selector: 'app-project',
  templateUrl: './project.component.html',
  styleUrls: ['./project.component.css']
})
export class ProjectComponent implements OnInit {

  private project: Project;

  /**
   * id passed by the router.
   */
  private id: number;

  private sourceSkills = new LocalDataSource([]);
  private settings_skills = Constants.SETTINGS_SKILL_SMARTTABLE;

  private profileProject = new FormGroup({
    projectName: new FormControl('')
  });

  sub: any;

  constructor(
    private cinematicService: CinematicService,
    private route: ActivatedRoute,
    private dataService: DataService,
    private messageService: MessageService) {}


  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      if (Constants.DEBUG) {
        console.log('params[\'id\'] ' + params['id']);
      }
      if (params['id'] == null) {
        this.id = null;
      } else {
        this.id = + params['id']; // (+) converts string 'id' to a number
      }

      this.loadProject();

    });

    this.cinematicService.setForm(Constants.PROJECT_CRUD);
  }

  /**
 * Loading the project from the back-end.
 */
  loadProject() {
    // EITHER we are in creation mode,
    // OR we load the Project from the back-end...
    // Anyway, We create an empty project until the subscription is complete
    this.project = new Project();
    if (this.id != null) {
      this.dataService.getProject(this.id).subscribe(
        (project: Project) => {
         this.project = project;
         this.profileProject.get('projectName').setValue(project.name);
        },
        error => {
          if (error.status === 404) {
            if (Constants.DEBUG) {
              console.log('404 : cannot find a project for the id ' + this.id);
            }
            this.messageService.error('There is no project for id ' + this.id);
            this.project = new Project();
          } else {
            console.error(error.message);
          }
        },
        () => {
          if (this.project.id === 0) {
            console.log('No project found for the id ' + this.id);
          }
          if (Constants.DEBUG) {
            console.log('Loading complete for id ' + this.id);
          }
        }
      );
    }
  }

  /**
   * Submit the change. The project will be created, or updated.
   */
  onSubmit() {
    this.project.name = this.profileProject.get('projectName').value;
    if (Constants.DEBUG) {
      console.log('saving the project ' + this.project.name + ' with id ' + this.project.id);
    }
    this.dataService.saveProject(this.project).subscribe(
        project => {
          this.project = project;
          this.messageService.info('Project ' + this.project.name + '  saved !');
        });
  }
}
