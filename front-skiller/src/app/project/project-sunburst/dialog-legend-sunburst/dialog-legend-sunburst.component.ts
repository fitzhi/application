import { Component, OnInit } from '@angular/core';
import { ReferentialService } from '../../../service/referential.service';
import { RiskLegend } from '../../../data/riskLegend';

@Component({
  selector: 'app-dialog-legend-sunburst',
  templateUrl: './dialog-legend-sunburst.component.html',
  styleUrls: ['./dialog-legend-sunburst.component.css']
})
export class DialogLegendSunburstComponent implements OnInit {

  public riskColumns: string[] = ['color', 'description'];

  public dataSource: RiskLegend[];

  constructor(private referentialService: ReferentialService) { }

  ngOnInit() {
    this.referentialService.subjectLegends.subscribe(legends => this.dataSource = legends);
  }

}
