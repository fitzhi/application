import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
	selector: 'app-message-box',
	templateUrl: './message-box.component.html',
	styleUrls: ['./message-box.component.css']
})
export class MessageBoxComponent implements OnInit {

	/**
	 * Will the button "Ok" be visible ?
	 */
	ok: boolean;

	/**
	 * Will thes buttons "Yes" or "No" be visible ?
	 */
	yes_no: boolean;

	image: string;

	constructor(@Inject(MAT_DIALOG_DATA) public data: any) { }

	ngOnInit() {
		this.ok = this.data.ok;
		this.yes_no = this.data.yes_no;
		this.image = this.yes_no ? './assets/img/questionMark.png' : './assets/img/exclamationMark.jpg';
	}

}
