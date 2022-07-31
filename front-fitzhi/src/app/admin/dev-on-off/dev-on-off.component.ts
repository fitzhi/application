import { Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';
import { traceOn } from '../../global';

@Component({
	selector: 'app-dev-on-off',
	templateUrl: './dev-on-off.component.html',
	styleUrls: ['./dev-on-off.component.css']
})
export class DevOnOffComponent implements OnInit {

	private devON = 'I was in Production mode. Now I am in Development mode !';

	private devOFF = 'I was in Development mode. Now I am in Production mode !';

	public devOnOff = ' ';

	constructor() { }

	ngOnInit() {
		traceOn() && console.log (localStorage.getItem('dev'));
		const dev = localStorage.getItem('dev');
		if ((!dev) || (dev === '0')) {
			this.devOnOff = this.devON;
			localStorage.setItem('dev', '1');
			// We activate the logging behavior.
			environment.debug = true;
		} else {
			this.devOnOff = this.devOFF;
			localStorage.setItem('dev', '0');
			environment.debug = false;
		}
	}

}
