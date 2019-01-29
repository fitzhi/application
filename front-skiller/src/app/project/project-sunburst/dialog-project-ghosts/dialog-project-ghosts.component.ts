import { Component, OnInit, Inject, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatSort } from '@angular/material';
import { Project } from '../../../data/project';
import { Constants } from '../../../constants';
import { ProjectGhostsDataSource } from './project-ghosts-data-source';

@Component({
  selector: 'app-dialog-project-ghosts',
  templateUrl: './dialog-project-ghosts.component.html',
  styleUrls: ['./dialog-project-ghosts.component.css']
})
export class DialogProjectGhostsComponent implements OnInit {

  /**
   * The undeclared contributors in the repository.
   */
  public dataSource: ProjectGhostsDataSource;

  public displayedColumns: string[] = ['pseudo', 'login', 'technical'];
  /**
   * Array will be sortable
   */
  @ViewChild(MatSort) sort: MatSort;

  constructor(
    public dialogRef: MatDialogRef<DialogProjectGhostsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProjectGhostsDataSource) { }

  ngOnInit() {
    this.dataSource = <ProjectGhostsDataSource>this.data;
    if (Constants.DEBUG) {
      console.log ('Working on project ' + this.dataSource.project);
    }
  }

  public submit() {

  }
}
