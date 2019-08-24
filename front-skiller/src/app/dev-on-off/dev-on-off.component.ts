import { Component, OnInit } from '@angular/core';
import { Constants } from '../constants';

@Component({
	selector: 'app-dev-on-off',
	templateUrl: './dev-on-off.component.html',
	styleUrls: ['./dev-on-off.component.css']
})
export class DevOnOffComponent implements OnInit {

	private devON = 'I was in Production mode. Now I am in Development mode !';

	private devOFF = 'I was in Development mode. Now I am in Production mode !';

	private devOnOff = ' ';

	constructor() { }

	ngOnInit() {
		if (Constants.DEBUG) {
			console.log (localStorage.getItem('dev'));
		}
		const dev = localStorage.getItem('dev');
		if ((!dev) || (dev === '0')) {
			this.devOnOff = this.devON;
			localStorage.setItem('dev', '1');
		} else {
			this.devOnOff = this.devOFF;
			localStorage.setItem('dev', '0');
		}
	}

}
