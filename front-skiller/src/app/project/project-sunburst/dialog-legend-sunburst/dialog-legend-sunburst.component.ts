import { Component, OnInit, OnDestroy } from '@angular/core';
import { ReferentialService } from '../../../service/referential.service';
import { RiskLegend } from '../../../data/riskLegend';
import { BaseComponent } from '../../../base/base.component';

@Component({
  selector: 'app-dialog-legend-sunburst',
  templateUrl: './dialog-legend-sunburst.component.html',
  styleUrls: ['./dialog-legend-sunburst.component.css']
})
export class DialogLegendSunburstComponent extends BaseComponent implements OnInit, OnDestroy {

  public riskColumns: string[] = ['color', 'description'];

  public dataSource: RiskLegend[];

  constructor(private referentialService: ReferentialService) {
    super();
  }

  ngOnInit() {
    this.subscriptions.add(
      this.referentialService.subjectLegends.subscribe(legends => this.dataSource = legends));
  }

  ngOnDestroy() {
    super.ngOnDestroy();
  }
}
