import { Component, Input, OnInit, AfterViewInit } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Slice } from './slice';
import { Constants } from 'src/app/constants';

@Component({
	selector: 'app-pie-dashboard',
	templateUrl: './pie-dashboard.component.html',
	styleUrls: ['./pie-dashboard.component.css']
})
export class PieDashboardComponent implements OnInit, AfterViewInit {

	@Input() slices$: BehaviorSubject<Slice[]>;

	constructor() { }

	ngOnInit() {
	}

	ngAfterViewInit(): void {
		if (Constants.DEBUG) {
			this.slices$.subscribe(slices => {
				if (Constants.DEBUG) {
					console.groupCollapsed ('slices received');
					console.log(...slices);
					console.groupEnd();
				}
				this.setupSlices(...slices);
			});
		}
	}

	setupSlices(...slices: Slice[]) {
		slices.forEach(slice => {
			const offset = 'rotate(' + slice.offset + 'deg)';
			this.setDivCSSVariable('pieSlice' + slice.id, 'transform', offset);
			this.setDivCSSVariable('pieSlice' + slice.id, '-webkit-transform', offset);
			this.setDivCSSVariable('pieSlice' + slice.id, '-moz-transform', offset);
			this.setDivCSSVariable('pieSlice' + slice.id, '-o-transform', offset);

			this.setPieCSSVariable('pieSlice' + slice.id, 'background-color', slice.color);
			const rotation = 'rotate(' + slice.angle + 'deg)';
			this.setPieCSSVariable('pieSlice' + slice.id, 'transform', rotation);
			this.setPieCSSVariable('pieSlice' + slice.id, '-webkit-transform', rotation);
			this.setPieCSSVariable('pieSlice' + slice.id, '-moz-transform', rotation);
			this.setPieCSSVariable('pieSlice' + slice.id, '-o-transform', rotation);
		});
	}

	setDivCSSVariable(id: string, variable: string, value: string) {
		const div = <HTMLElement>document.getElementById(id);
		div.style.setProperty(variable, value);
	}

	setPieCSSVariable(id: string, variable: string, value: string) {
		const pie = <HTMLElement>document.getElementById(id).children.item(0);
		pie.style.setProperty(variable, value);
	}
}
