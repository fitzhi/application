import { Component, OnInit, ViewChild } from '@angular/core';
import { ProjectService } from '../../project.service';
import { Constants } from '../../constants';
import { MessageService } from '../../message.service';
import { ActivatedRoute } from '@angular/router';
import {MatSort, MatTableDataSource} from '@angular/material';

@Component({
  selector: 'app-project-staff',
  templateUrl: './project-staff.component.html',
  styleUrls: ['./project-staff.component.css']
})
export class ProjectStaffComponent implements OnInit {

  public dataSource;

  public displayedColumns: string[] = ['fullname', 'active', 'firstCommit', 'lastCommit', 'numberOfCommits', 'numberOfFiles'];

  public idProject: number;

  public sub: any;

  @ViewChild(MatSort) sort: MatSort;

  constructor(
    private projectService: ProjectService,
    private route: ActivatedRoute,
    private messageService: MessageService) { }

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
  }

  /**
   * Load the contributors for the current project.
   */
  loadContributors() {
    this.projectService.contributors(this.idProject).subscribe(
      contributorsDTO => {
        this.dataSource = new MatTableDataSource(contributorsDTO.contributors);
        this.dataSource.sort = this.sort;
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
