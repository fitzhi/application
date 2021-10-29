import { Component, OnInit } from '@angular/core';
import { StarfieldService } from './service/starfield.service';

@Component({
	selector: 'app-starfield',
	templateUrl: './starfield.component.html',
	styleUrls: ['./starfield.component.css']
})
export class StarfieldComponent implements OnInit {

	constructor(public starfieldService: StarfieldService) { }

	ngOnInit(): void {
	}

}
