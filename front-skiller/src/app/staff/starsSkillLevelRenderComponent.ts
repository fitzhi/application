import { Component, Input, OnInit } from '@angular/core';
import { ViewCell } from 'ng2-smart-table';

@Component({
  template: `
    {{renderValue}}
  `,
})
export class StarsSkillLevelRenderComponent implements ViewCell, OnInit {

  renderValue: string;

  @Input() value: number;
  @Input() rowData: any;

  ngOnInit() {
    this.renderValue = '*'.repeat(this.value);
  }
}
