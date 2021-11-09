import { Component, OnInit } from '@angular/core';
import { SummaryService } from '../service/summary.service';

@Component({
	selector: 'app-summary',
	templateUrl: './summary.component.html',
	styleUrls: ['./summary.component.css']
})
export class SummaryComponent implements OnInit {

	constructor(public summaryService: SummaryService) { }

	ngOnInit(): void {
	}

}
