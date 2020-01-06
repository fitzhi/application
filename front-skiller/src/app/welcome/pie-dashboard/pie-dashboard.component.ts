import { Component, Input, OnInit } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Slice } from './slice';
import { Constants } from 'src/app/constants';

@Component({
	selector: 'app-pie-dashboard',
	templateUrl: './pie-dashboard.component.html',
	styleUrls: ['./pie-dashboard.component.css']
})
export class PieDashboardComponent implements OnInit {

	@Input() slices$: BehaviorSubject<Slice[]>;

	constructor() { }

	ngOnInit() {
		if (Constants.DEBUG) {
			this.slices$.subscribe(slices => console.log(...slices));
		}
	}

}
