import { Component, OnInit, Input } from '@angular/core';

@Component({
	selector: 'app-tasks-detail-form',
	templateUrl: './tasks-detail-form.component.html',
	styleUrls: ['./tasks-detail-form.component.css']
})
export class TasksDetailFormComponent implements OnInit {

	/**
	 * The topic identifier.
	 */
	@Input() idTopic: number;

	/**
	 * Audit topic whose tasks & todos will be entered in this form.
	 */
	@Input() title: string;


	constructor() { }

	ngOnInit() {
	}

}
