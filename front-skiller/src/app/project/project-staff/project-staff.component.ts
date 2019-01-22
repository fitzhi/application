import { Component, OnInit, ViewChild } from '@angular/core';
import { ProjectService } from '../../project.service';
import { Constants } from '../../constants';
import { MessageService } from '../../message.service';
import { CinematicService } from '../../cinematic.service';
import { ActivatedRoute, Router } from '@angular/router';
import {MatSort, MatTableDataSource} from '@angular/material';
import { ProjectStaffService } from '../project-staff-service/project-staff.service';

@Component({
  selector: 'app-project-staff',
  templateUrl: './project-staff.component.html',
  styleUrls: ['./project-staff.component.css']
})
export class ProjectStaffComponent implements OnInit {

  public dataSource;

  public displayedColumns: string[] = ['fullname', 'active', 'external', 'firstCommit', 'lastCommit', 'numberOfCommits', 'numberOfFiles'];

  public idProject: number;

  public sub: any;

  @ViewChild(MatSort) sort: MatSort;

  constructor(
    private projectService: ProjectService,
    private route: ActivatedRoute,
    private messageService: MessageService,
    private cinematicService: CinematicService,
    private router: Router,
    private projectStaffService: ProjectStaffService) { }

  ngOnInit() {

    this.sub = this.route.params.subscribe(params => {
      if (Constants.DEBUG) {
        console.log('params[\'id\'] ' + params['id']);
      }
      if (params['id'] == null) {
        this.idProject = null;
      } else {
        this.idProject = + params['id']; // (+) converts string 'id' to a number
        this.loadContributors();
      }
    });

    // Either we reach this component with this url '/project/:id' and the selection of the tab Staff
    // Or we reach this component directly with this url '/project/:id/staff'
    // We notify the cinematicService with the complete url '/project/:id/staff
    // in order to be able to jump back directly to this list
    // when the user will click the list button on the navigation block at the top left corner.
    const urlProjectStaffList = (this.router.url.indexOf('/staff') === -1) ? this.router.url + '/staff' : this.router.url;

    this.cinematicService.setForm(Constants.PROJECT_TAB_STAFF, urlProjectStaffList);
  }

  /**
   * Load the contributors for the current project.
   */
  loadContributors() {
    this.projectService.contributors(this.idProject).subscribe(
      contributorsDTO => {
        this.dataSource = new MatTableDataSource(contributorsDTO.contributors);
        this.dataSource.sort = this.sort;
        this.projectStaffService.contributors = this.dataSource.data;
        this.dataSource.connect().subscribe(data => this.projectStaffService.contributors = data);
        this.dataSource.sortingDataAccessor = (data: any, sortHeaderId: string): string => {
          if (typeof data[sortHeaderId] === 'string') {
            return data[sortHeaderId].toLocaleLowerCase();
          }
          return data[sortHeaderId];
        };
      }, error => {
        if (error.status === 404) {
          if (Constants.DEBUG) {
            console.log('404 : cannot find contributors for the id ' + this.idProject);
          }
          this.messageService.error('Cannot retrieve the contributors for the project identifier ' + this.idProject);
        } else {
          console.error(error.message);
        }
      },
      () => {
        if (Constants.DEBUG) {
          console.log('Loading complete for id ' + this.idProject);
        }
      });
  }

  /**
   * Return the CSS class corresponding to the active vs inactive status of a developer.
   */
  public class_active_inactive(active: boolean) {
    return active ? 'contributor_active' : 'contributor_inactive';
  }
}
