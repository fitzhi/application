import { Component, OnInit, ViewEncapsulation, Input, AfterViewInit } from '@angular/core';
import * as d3 from 'd3';
import { Subject } from 'rxjs';
import { PanelSwitchEvent } from '../../sonar-thumbnails/panel-switch-event';
import { Constants } from 'src/app/constants';

@Component({
	selector: 'app-sonar-quotation',
	templateUrl: './sonar-quotation.component.html',
	styleUrls: ['./sonar-quotation.component.css']
})
export class SonarQuotationComponent implements OnInit {

	/**
	* Observable emitting a PanelSwitchEvent when
	* another Sonar project is selected or
	* another panel is selected
	*/
	@Input() panelSwitchTransmitter$: Subject<PanelSwitchEvent>;

	constructor() { }

	ngOnInit() {
		this.panelSwitchTransmitter$.subscribe(
			panelSwitchEvent => {
				console.log(panelSwitchEvent.keySonar, Constants.TITLE_PANELS[panelSwitchEvent.idPanel]);
				if (panelSwitchEvent.idPanel === Constants.PROJECT_SONAR_PANEL.SONAR) {
					if (panelSwitchEvent.keySonar === 'front-skiller') {
						this.draw('red');
					} else {
						this.draw('green');
					}
				}
			}
		);
	}

	public draw(color: string) {

		d3.select('svg').remove();

		const svg = d3
			.select('p')
			.append('svg')
			.attr('width', '100%')
			.attr('height', '100%')
			.append('g')
			.attr('transform', 'translate(100,100)');

		svg.append('circle').attr('cx', 60).attr('cx', 60).attr('r', 50).attr('fill', color);
	}
}
