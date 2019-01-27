import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { MatTableDataSource, MatSort } from '@angular/material';
import { Unknown } from '../../../data/Unknown';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-project-unknowns',
  templateUrl: './project-unknowns.component.html',
  styleUrls: ['./project-unknowns.component.css']
})
export class ProjectUnknownsComponent implements OnInit {

  public displayedColumns: string[] = ['unknown'];

  /**
   * The name of the project.
   */
  @Input('projectName') projectName;

  /**
   * The unknown contributors in the repository.
   */
  @Input('dataSource') dataSource;

  /**
   * Array will be sortable
   */
  @ViewChild(MatSort) sort: MatSort;

  constructor() {
  }

  ngOnInit() {
    this.dataSource.sort = this.sort;
  }
}
