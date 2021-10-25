import { Component, OnInit } from '@angular/core';
import { StarfieldService } from '../service/starfield.service';

@Component({
	selector: 'app-starfield-header',
	templateUrl: './starfield-header.component.html',
	styleUrls: ['./starfield-header.component.css']
})
export class StarfieldHeaderComponent implements OnInit {

	public displayHelp = false;

	public today = new Date();

	constructor(public starfieldService: StarfieldService) { }

	ngOnInit(): void {
	}

	help(): void {
	}
	
	next(): void {
	}

	previous(): void {
	}
}