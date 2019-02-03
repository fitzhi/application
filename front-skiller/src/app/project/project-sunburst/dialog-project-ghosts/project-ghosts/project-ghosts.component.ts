import { Component, OnInit, Input, Output, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material';
import { Constants } from '../../../../constants';
import { Unknown } from '../../../../data/unknown';

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

  /**
   * Table is editable.
   */
  @Input() editable;

  public editableColumns: string[] = ['pseudo', 'login', 'technical'];
  public enhancedColumns: string[] = ['pseudo', 'login', 'fullName', 'technical'];

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

  /**
   * The check Box for the id "technical" has been checked or unchecked.
   */
  check (unknown: Unknown) {
    if (unknown.technical) {
      unknown.fullName = '';
      unknown.login = '';
      unknown.idStaff = -1;
    }
  }

  checkValue (technical: boolean): string {
    return 'Fred';
  }
}
