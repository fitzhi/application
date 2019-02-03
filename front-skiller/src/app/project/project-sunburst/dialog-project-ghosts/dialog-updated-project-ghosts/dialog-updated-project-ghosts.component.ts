import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material';
import { ProjectGhostsDataSource } from '../project-ghosts-data-source';
import { Constants } from '../../../../constants';

@Component({
  selector: 'app-dialog-updated-project-ghosts',
  templateUrl: './dialog-updated-project-ghosts.component.html',
  styleUrls: ['./dialog-updated-project-ghosts.component.css']
})
export class DialogUpdatedProjectGhostsComponent implements OnInit {

  /**
   * The undeclared contributors in the repository.
   */
  public dataSource: ProjectGhostsDataSource;

  constructor(
//    private dialogRef: MatDialogRef<DialogProjectGhostsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProjectGhostsDataSource) { }

  ngOnInit() {
    this.dataSource = this.data;
    if (Constants.DEBUG) {
      console.log ('Working on project ' + this.dataSource.project.name);
    }
  }

}
