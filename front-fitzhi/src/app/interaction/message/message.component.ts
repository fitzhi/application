import { Component, OnInit } from '@angular/core';
import { Constants } from '../../constants';
import { Message } from './message';
import { MessageService } from './message.service';


@Component({
	selector: 'app-message',
	templateUrl: './message.component.html',
	styleUrls: ['./message.component.css']
})
export class MessageComponent implements OnInit {

	/**
	 * Text of the message.
	 */
	myMessage = '';

	/**
	 * Style specicic for the container message
	 */
	classContainerMessage: string;

	constructor(
		private messageService: MessageService) { }

	ngOnInit() {
		this.messageService.newMessage$.subscribe((data: Message) => {

			switch (data.severity) {
				case Constants.MESSAGE_VOID:
					this.classContainerMessage = 'rounded void';
					break;
				case Constants.MESSAGE_ERROR:
					this.classContainerMessage = 'rounded error';
					break;
				case Constants.MESSAGE_INFO:
					this.classContainerMessage = 'rounded info';
					break;
				case Constants.MESSAGE_WARNING:
					this.classContainerMessage = 'rounded warning';
					break;
				case Constants.MESSAGE_SUCCESS:
					this.classContainerMessage = 'rounded success';
					break;

			}
			this.myMessage = data.message;
		});
	}
}
