import {Component, OnInit} from '@angular/core';
import {FormGroup, FormControl} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';

import {ProjectService} from '../../project.service';
import {SkillService} from '../../skill.service';
import {MessageService} from '../../message.service';
import {CinematicService} from '../../cinematic.service';

import {Project} from '../../data/project';
import {ProjectDTO} from '../../data/external/projectDTO';
import {Constants} from '../../constants';
import { ListProjectsService } from '../../list-projects-service/list-projects.service';
import {LocalDataSource} from 'ng2-smart-table';

@Component({
  selector: 'app-project-form',
  templateUrl: './project-form.component.html',
  styleUrls: ['./project-form.component.css']
})
export class ProjectFormComponent implements OnInit {

  public project: Project;

  /**
   * id passed by the router.
   */
  private id: number;

  public DIRECT_ACCESS = 1;
  public REMOTE_FILE_ACCESS = 2;

  sourceSkills = new LocalDataSource([]);
  settings_skills = Constants.SETTINGS_SKILL_SMARTTABLE;

  profileProject = new FormGroup({
    projectName: new FormControl(''),
    urlRepository1: new FormControl(''),
    urlRepository2: new FormControl(''),
    username: new FormControl(''),
    password: new FormControl(''),
    filename: new FormControl('')
  });

  sub: any;

  /**
  * Member variable linked to the connection settings toggle.
  */
  public connection_settings: string;

  constructor(
    private cinematicService: CinematicService,
    private route: ActivatedRoute,
    private messageService: MessageService,
    private projectService: ProjectService,
    private listProjectsService: ListProjectsService,
    private skillService: SkillService,
    private router: Router) {}


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

    this.cinematicService.setForm(Constants.PROJECT_TAB_FORM, this.router.url);
  }

  /**
   * Remove a skill from a project
   */
  onConfirmRemoveSkillFromProject(event) {
    if (!this.checkProjectExist(event)) {
      event.confirm.reject();
      return;
    }
    if (window.confirm('Are you sure you want to remove the skill '
      + event.data['title'] + ' from project ' +
      this.project.name
      + '?')) {
      /*
       * After the addition of a skill into a project, and before the reloadSkills has been completed,
       * there is a very little delay with a skill without ID into the skills list.
       */
      if (typeof event.data['id'] !== 'undefined') {
        this.projectService.removeSkill(this.project.id, event.data['id']).subscribe(
          (projectDTO: ProjectDTO) => {
            this.messageService.info('The project ' + projectDTO.project.name +
              ' does not require anymore the skill ' + event.data.title);
            this.reloadSkills(this.project.id);
            event.confirm.resolve();
          },
          response_error => {
            if (Constants.DEBUG) {
              console.log('Error ' + response_error.error.code + ' ' + response_error.error.message);
            }
            this.reloadSkills(this.project.id);
            event.confirm.reject();
            this.messageService.error(response_error.error.message);
          }
        );
      }
    } else {
      event.confirm.reject();
    }
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
      this.listProjectsService.getProject(this.id).subscribe(
        (project: Project) => {
         this.project = project;
         this.profileProject.get('projectName').setValue(project.name);
         this.connection_settings = String(this.project.connection_settings);
         this.profileProject.get('urlRepository1').setValue(this.project.urlRepository);
         this.profileProject.get('urlRepository2').setValue(this.project.urlRepository);
         this.profileProject.get('username').setValue(this.project.username);
         this.profileProject.get('password').setValue(this.project.password);
         this.profileProject.get('filename').setValue(this.project.filename);
         this.sourceSkills.load(this.project.skills);
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
   * Test if the project exists, before adding a skill into a project.
   * A project ID is required.
   */
  checkProjectExist(event): boolean {
    if (this.project.id === null) {
      this.messageService.error('You cannot add, or update a skill of an unregistered project. '
        + 'Please saved this new project first !');
      return false;
    } else {
      return true;
    }
  }

  onConfirmAddSkillToProject(event) {
    if (Constants.DEBUG) {
      console.log('onConfirmAddProjectSkill for event ' + event.newData.title);
    }
    if (this.checkProjectExist(event)) {
      this.projectService.addSkill(this.project.id, event.newData.title).subscribe(
        (projectDTO: ProjectDTO) => {
          this.messageService.info('The project ' + projectDTO.project.name +
            ' requires from now the skill ' + event.newData.title);
          this.reloadSkills(this.project.id);
          event.confirm.resolve();
        },
        response_error => {
          if (Constants.DEBUG) {
            console.log('Error ' + response_error.error.code + ' ' + response_error.error.message);
          }
          this.reloadSkills(this.project.id);
          this.messageService.error(response_error.error.message);
          event.confirm.reject();
        }
      );
    } else {
      event.confirm.reject();
    }
  }

  onConfirmEditSkillIntoProject(event) {
    if (Constants.DEBUG) {
      console.log('onConfirmEditProjectSkill for skill from ' + event.data.title + ' to ' + event.newData.title);
    }
    if (this.checkProjectExist(event)) {
      this.skillService.lookup(event.newData.title).subscribe(

        project_transfered => {
          this.projectService.changeSkill(this.project.id, event.data.title, event.newData.title).subscribe(
            (projectDTO: ProjectDTO) => {
              this.messageService.info(projectDTO.project.name + ' ' +
                ' has now the skill ' + event.newData.title);
              this.reloadSkills(this.project.id);
              event.confirm.resolve();
            },
            response_error => {
              if (Constants.DEBUG) {
                console.log('Error ' + response_error.error.code + ' ' + response_error.error.message);
              }
              this.reloadSkills(this.project.id);
              event.confirm.reject();
              this.messageService.error(response_error.error.message);
            }
          );
        },
        response_error => {
          if (Constants.DEBUG) {
            console.error(response_error);
          }
          this.messageService.error(response_error.error.message);
          event.confirm.reject();
        });
    } else {
      event.confirm.reject();
    }
  }

  /*
  * Refresh the skills of the project.
  */
  reloadSkills(idProject: number): void {
    if (Constants.DEBUG) {
      console.log('Refreshing skills for the project\'s id ' + idProject);
    }
    this.projectService.loadSkills(idProject).subscribe(
      skills => this.sourceSkills.load(skills),
      error => console.log(error),
    );
  }
  /**
   * Submit the change. The project will be created, or updated.
   */
  onSubmit() {
    this.project.name = this.profileProject.get('projectName').value;
    switch (this.project.connection_settings) {
      case this.DIRECT_ACCESS:
        this.project.urlRepository = this.profileProject.get('urlRepository1').value;
        this.project.username = this.profileProject.get('username').value;
        this.project.password = this.profileProject.get('password').value;
        this.project.filename = '';
        break;
      case this.REMOTE_FILE_ACCESS:
        this.project.urlRepository = this.profileProject.get('urlRepository2').value;
        this.project.username = '';
        this.project.password = '';
        this.project.filename = this.profileProject.get('filename').value;
        break;
    }
    if (Constants.DEBUG) {
      console.log('saving the project ' + this.project.name + ' with id ' + this.project.id);
    }
    this.projectService.save(this.project).subscribe(
        project => {
          this.project = project;
          this.messageService.info('Project ' + this.project.name + '  saved !');
        });
  }

  public onConnectionSettingsChange(val: string) {
    this.project.connection_settings = +val;
    switch (this.project.connection_settings) {
      case this.DIRECT_ACCESS:
        this.profileProject.get('filename').setValue('');
        this.profileProject.get('urlRepository1').setValue(this.profileProject.get('urlRepository2').value);
        break;
      case this.REMOTE_FILE_ACCESS:
        this.profileProject.get('username').setValue('');
        this.profileProject.get('password').setValue('');
        this.profileProject.get('urlRepository2').setValue(this.profileProject.get('urlRepository1').value);
        break;
    }
  }

  /**
   * Did the user select the direct access connection settings (user/password).
   */
  public directAccess() {
    // No choice have been made yet. We are the 2 pannels.
    if ((typeof this.project.connection_settings === 'undefined') || (this.project.connection_settings === 0)) {
      return true;
    }
    return (this.project.connection_settings === this.DIRECT_ACCESS);
  }

  /**
   * Did the user select the undirect access. Indicating a remote file containing the connection settings.
   */
  public undirectAccess() {
    // No choice have been made yet. We are the 2 pannels.
    if ((typeof this.project.connection_settings === 'undefined') || (this.project.connection_settings === 0)) {
      return true;
    }
    return (this.project.connection_settings === this.REMOTE_FILE_ACCESS);
  }

}
