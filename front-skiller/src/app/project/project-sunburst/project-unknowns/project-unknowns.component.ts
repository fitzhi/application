import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { MatTableDataSource, MatSort } from '@angular/material';
import { Unknown } from '../../../data/Unknown';

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
  @Input('unknowns') unknowns: Unknown[];

  sortedData: Unknown[];

  /**
   * Array will be sortable
   */
  @ViewChild(MatSort) sort: MatSort;

  /**
   * Source of data for the array of unknown commiters.
   */
  public dataSource;

  constructor() {
  }

  ngOnInit() {
    setTimeout(() => {
      this.dataSource = new MatTableDataSource(this.unknowns);
      this.dataSource.sort = this.sort;
    }, 0);
  }
}
