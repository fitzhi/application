import { Component, OnInit, Input, EventEmitter, Output, ViewChild } from '@angular/core';
import { Ghost } from '../../../../data/Ghost';
import { MatSort, MatTableDataSource } from '@angular/material';

@Component({
  selector: 'app-project-ghosts',
  templateUrl: './project-ghosts.component.html',
  styleUrls: ['./project-ghosts.component.css']
})
export class ProjectGhostsComponent implements OnInit {

  /**
   * The undeclared contributors in the repository.
   */
  @Input() ghostList;

  @Output() ghostListChange = new EventEmitter<Ghost[]>();

  public displayedColumns: string[] = ['pseudo', 'login', 'technical'];


  dataSource: MatTableDataSource<Ghost>;

  /**
   * Array will be sortable
   */
  @ViewChild(MatSort) sort: MatSort;

  constructor() { }

  ngOnInit() {
  }

}
