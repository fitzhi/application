import { Component, OnInit, OnDestroy } from '@angular/core';
import { ReferentialService } from '../../../service/referential.service';
import { RiskLegend } from '../../../data/riskLegend';
import { BaseComponent } from '../../../base/base.component';
import { take } from 'rxjs/operators';

@Component({
	selector: 'app-legend-sunburst',
	templateUrl: './legend-sunburst.component.html',
	styleUrls: ['./legend-sunburst.component.css']
})
export class DialogLegendSunburstComponent extends BaseComponent implements OnInit, OnDestroy {

	public riskColumns: string[] = ['color', 'description'];

	public dataSource: RiskLegend[];

	constructor(private referentialService: ReferentialService) {
		super();
	}

	ngOnInit() {
		this.subscriptions.add(
			this.referentialService.legendsLoaded$.subscribe(legends =>
				this.dataSource = this.referentialService.legends));
	}

	ngOnDestroy() {
		super.ngOnDestroy();
	}
}
