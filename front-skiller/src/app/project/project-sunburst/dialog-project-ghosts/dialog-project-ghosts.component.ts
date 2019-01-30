import { Component, OnInit, Inject, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatSort } from '@angular/material';
import { Project } from '../../../data/project';
import { Constants } from '../../../constants';
import { ProjectGhostsDataSource } from './project-ghosts-data-source';
import { Ghost } from '../../../data/Ghost';

@Component({
  selector: 'app-dialog-project-ghosts',
  templateUrl: './dialog-project-ghosts.component.html',
  styleUrls: ['./dialog-project-ghosts.component.css']
})
export class DialogProjectGhostsComponent implements OnInit {

  /**
   * The undeclared contributors in the repository.
   */
  public dataSource: Ghost[];

  public displayedColumns: string[] = ['pseudo', 'login', 'technical'];
  /**
   * Array will be sortable
   */
  @ViewChild(MatSort) sort: MatSort;

  constructor(
    public dialogRef: MatDialogRef<DialogProjectGhostsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProjectGhostsDataSource) { }

  ngOnInit() {
    this.dataSource = this.data.getGhosts();
    /*
    setTimeout(() => {
      (<HTMLInputElement>document.getElementById('mat-input-0')).value = this.dataSource[0].login;
    }, 100);
    */
    if (Constants.DEBUG) {
      console.log ('Working on project ' + this.data.project);
    }
  }

  public submit() {
//    console.log (this.dataSource[0].login);
    this.dialogRef.close(this.dataSource);
  }
}
