import { Component, OnDestroy, OnInit } from '@angular/core';
import { BaseDirective } from '../../../base/base-directive.directive';
import { RiskLegend } from '../../../data/riskLegend';
import { ReferentialService } from '../../../service/referential/referential.service';

@Component({
	selector: 'app-legend-sunburst',
	templateUrl: './legend-sunburst.component.html',
	styleUrls: ['./legend-sunburst.component.css']
})
export class DialogLegendSunburstComponent extends BaseDirective implements OnInit, OnDestroy {

	public riskColumns: string[] = ['color', 'description'];

	public dataSource: RiskLegend[];

	constructor(private referentialService: ReferentialService) {
		super();
	}

	ngOnInit() {
		this.subscriptions.add(
			this.referentialService.referentialLoaded$.subscribe(
				(doneAndOk: boolean) => {
					if (doneAndOk) {
						this.subscriptions.add(
							this.referentialService.legendsLoaded$.subscribe(
								legends => this.dataSource = this.referentialService.legends));
					}
				}
			)
		);
	}

	ngOnDestroy() {
		super.ngOnDestroy();
	}
}
