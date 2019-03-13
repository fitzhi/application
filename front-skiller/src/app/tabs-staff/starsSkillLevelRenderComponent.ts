import { Component, Input, OnInit } from '@angular/core';
import { ViewCell } from 'ng2-smart-table';

@Component({
  template: '<p style="width:100%;text-align:center;margin:0px">{{renderValue}}</p>',
})
export class StarsSkillLevelRenderComponent implements ViewCell, OnInit {

  renderValue: string;

  @Input() value: number;
  @Input() rowData: any;

  ngOnInit() {
    this.renderValue = '*'.repeat(this.value);
  }
}
