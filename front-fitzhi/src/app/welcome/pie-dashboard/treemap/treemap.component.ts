import { Component, OnInit, Input } from '@angular/core';
import {single} from './data';

@Component({
	selector: 'app-treemap',
	templateUrl: './treemap.component.html',
	styleUrls: ['./treemap.component.css']
})
export class TreemapComponent implements OnInit {

	single: any[];

	@Input() view: any[];

	@Input() gradient = false;

	@Input() animations = true;

	colorScheme = {
		domain: ['#5AA454', '#E44D25', '#CFC0BB', '#7aa3e5', '#a8385d', '#aae3f5']
	};

	constructor() {
		Object.assign(this, { single });
	}

	onSelect(event) {
		console.log(event);
	}

	labelFormatting(c) {
		return `${(c.label)} Population`;
	}

	ngOnInit() {
	}

}
