import { Component, OnInit, Input, EventEmitter, Output, ViewChild } from '@angular/core';
import { Ghost } from '../../../../data/Ghost';
import { MatSort, MatTableDataSource } from '@angular/material';
import { ProjectGhostsDataSource } from '../project-ghosts-data-source';
import { Constants } from '../../../../constants';

@Component({
  selector: 'app-project-ghosts',
  templateUrl: './project-ghosts.component.html',
  styleUrls: ['./project-ghosts.component.css']
})
export class ProjectGhostsComponent implements OnInit {

  /**
   * The undeclared contributors in the repository.
   */
  @Input() dataSource;

  public displayedColumns: string[] = ['pseudo', 'login', 'technical'];

  /**
   * Array will be sortable
   */
  @ViewChild(MatSort) sort: MatSort;

  constructor() { }

  ngOnInit() {
    if (Constants.DEBUG) {
      console.log ('Working on project ' + this.dataSource.project.name);
    }
  }

}
