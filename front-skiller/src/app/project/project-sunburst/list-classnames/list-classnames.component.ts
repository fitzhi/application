import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-list-classnames',
  templateUrl: './list-classnames.component.html',
  styleUrls: ['./list-classnames.component.css']
})
export class ListClassnamesComponent implements OnInit {

  public tblColumns: string[] = ['filename', 'lastCommit'];

  @Input('classnames')
  public classnames;

  constructor() { }

  ngOnInit() {
  }

}
